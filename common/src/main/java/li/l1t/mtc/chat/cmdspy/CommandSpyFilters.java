/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package li.l1t.mtc.chat.cmdspy;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides static utility methods for use with {@link CommandSpyFilter}s.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 19.6.14
 */
public final class CommandSpyFilters { //TODO: This needs to be refactored into a Manager or so
    private static CommandSpyFilter ALL_FILTER = new MultiSubscriberCommandSpyFilter("§8[CmdSpy]§7{0}: §o/{1}", (cmd, plr) -> true) {
        @Override
        public String niceRepresentation() {
            return "(global) all";
        }
    };
    private static BadCommandSpyFilter BAD_COMMAND_FILTER = new BadCommandSpyFilter();
    private static Set<CommandSpyFilter> activeFilters = new CopyOnWriteArraySet<>();

    static { //this is why we need to refactor it to a Manager
        registerFilter(BAD_COMMAND_FILTER);
    }

    private CommandSpyFilters() {

    }

    /**
     * Gets an unmodifiable Set of active filters. Use the {@link #registerFilter(CommandSpyFilter)}
     * and {@link #unregisterFilter(CommandSpyFilter)} methods to add and remove filters.
     *
     * @return Unmodifiable Set containing registered filters.
     */ //nobody needs to know that this is actually modifiable
    public static Set<CommandSpyFilter> getActiveFilters() {
        return activeFilters;
    }

    public static void registerFilter(CommandSpyFilter filter) {
        activeFilters.add(filter);
    }

    public static void unregisterFilter(CommandSpyFilter filter) {
        activeFilters.remove(filter);
    }

    /**
     * This removes dead filters. Dead filters are filters that do not have any subscribers
     * (online). This method ignores filters whose {@link CommandSpyFilter#canSubscribe()} method
     * returns FALSE.
     */
    public static void removeDeadFilters() {
        Set<CommandSpyFilter> filters = ImmutableSet.copyOf(getActiveFilters()).stream()
                .filter(CommandSpyFilter::canSubscribe)
                .collect(Collectors.toSet());

        Collection<UUID> onlinePlayers = Bukkit.getServer().getOnlinePlayers().stream()
                .map(OfflinePlayer::getUniqueId)
                .collect(Collectors.toList());
        filters.stream() //Remove offline subscribers for more accurate results
                .forEach(f -> removeOthers(f, onlinePlayers));

        filters.stream() //Needs the source to be a copy or will throw CME
                .filter(f -> f.getSubscribers().isEmpty())
                .forEach(CommandSpyFilters::unregisterFilter);
    }

    private static void removeOthers(CommandSpyFilter filter, Collection<UUID> uuidsToKeep) {
        Iterator<UUID> it = filter.getSubscribers().iterator();
        while (it.hasNext()) {
            if (!uuidsToKeep.contains(it.next())) {
                it.remove();
            }
        }
    }

    public static void removeOfflineSubscribers(CommandSpyFilter filter) {
        removeOthers(filter, Bukkit.getServer().getOnlinePlayers().stream()
                .map(Entity::getUniqueId)
                .collect(Collectors.toList()));
    }

    public static Stream<CommandSpyFilter> getSubscribedFilters(UUID subscriberId) {
        return activeFilters.stream()
                .filter(f -> f.getSubscribers().contains(subscriberId));
    }

    public static long unsubscribeFromAll(UUID subscriberId) {
        long rtrn = activeFilters.stream()
                .filter(filter -> filter.canSubscribe() && filter.removeSubscriber(subscriberId))
                .count();

        removeDeadFilters();

        return rtrn;
    }

    public static boolean toggleSubscribedAndRegister(CommandSpyFilter filter, Player spy) {
        if (filter.removeSubscriber(spy.getUniqueId())) {
            removeDeadFilters();
        } else {
            filter.addSubscriber(spy);
            registerFilter(filter);
        }

        return filter.getSubscribers().contains(spy.getUniqueId());
    }

    public static boolean toggleGlobalFilter(Player spy) {
        return toggleSubscribedAndRegister(ALL_FILTER, spy);
    }

    public static boolean togglePlayerFilter(UUID targetId, Player spy) { //This could be generified more - see instanceof
        return toggleSubscribedAndRegister(activeFilters.stream()
                .filter(f -> f instanceof PlayerCommandSpyFilter && ((PlayerCommandSpyFilter) f).getTargetId().equals(targetId))
                .findAny()
                .orElseGet(() -> playerFilter(targetId)), spy);
    }

    public static boolean toggleStringFilter(String input, Player spy) {
        return toggleSubscribedAndRegister(activeFilters.stream()
                .filter(f -> f instanceof RegExCommandSpyFilter && ((RegExCommandSpyFilter) f).hasCommandName(input))
                .findAny()
                .orElseGet(() -> stringFilter(input)), spy);
    }

    public static CommandSpyFilter playerFilter(UUID targetId) {
        return new PlayerCommandSpyFilter("§e[CmdSpy]§7{0}: §o/{1}", targetId);
    }

    protected static CommandSpyFilter regexFilter(Stream<Pattern> patterns) {
        return new RegExCommandSpyFilter(patterns.collect(Collectors.toList()));
    }

    public static CommandSpyFilter stringFilter(String input) {
        return stringFilter(input, CommandSpyFilters::regexFilter);
    }

    public static CommandSpyFilter stringFilter(String input, Function<Stream<Pattern>, CommandSpyFilter> filterBuilder) {
        return filterBuilder.apply(getStringFilterPatterns(input));
    }

    public static void addBadCommand(String badCommand) {
        BAD_COMMAND_FILTER.addCommand(badCommand);
    }

    public static Stream<Pattern> getStringFilterPatterns(String input) {
        Function<String, Pattern> regExPatternBuilder = (str) -> Pattern.compile("(" + str + ")(?:\\s+|$)", Pattern.CASE_INSENSITIVE);
        Function<String, Pattern> cmdPatternBuilder = (str) -> Pattern.compile("^(" + str + ")(?:\\s+|$)", Pattern.CASE_INSENSITIVE);

        if (input.startsWith("!r")) {
            return Stream.of(
                    regExPatternBuilder.apply(input.replaceFirst("!r\\s*", "")) //Build a filter for the regex, removing the leading !r and any following whitespace.
            );
        } else {
            PluginCommand foundCommand = Bukkit.getPluginCommand(input);
            Set<String> commandsToMatch = new HashSet<>();

            commandsToMatch.add(input);
            if (foundCommand != null) {
                commandsToMatch.addAll(foundCommand.getAliases());
            }

            return commandsToMatch.stream()
                    .map(Pattern::quote)
                    .map(cmdPatternBuilder);
        }
    }
}
