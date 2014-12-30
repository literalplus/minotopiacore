/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.chat.cmdspy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
public final class CommandSpyFilters {
    private static CommandSpyFilter ALL_FILTER = new MultiSubscriberCommandSpyFilter("§8[CmdSpy]§7{0}: §o/{1}", (cmd, plr) -> true) {
        @Override
        public String niceRepresentation() {
            return "(global) all";
        }
    };
    private static Set<CommandSpyFilter> activeFilters = Sets.newHashSet();

    private CommandSpyFilters() {

    }

    /**
     * Gets an unmodifiable Set of active filters.
     * Use the {@link #registerFilter(CommandSpyFilter)} and {@link #unregisterFilter(CommandSpyFilter)} methods to add and remove filters.
     *
     * @return Unmodifiable Set containing registered filters.
     */
    public static Set<CommandSpyFilter> getActiveFilters() {
        return ImmutableSet.copyOf(activeFilters);
    }

    public static void registerFilter(CommandSpyFilter filter) {
        activeFilters.add(filter);
    }

    public static void unregisterFilter(CommandSpyFilter filter) {
        activeFilters.remove(filter);
    }

    /**
     * This removes dead filters. Dead filters are filters that do not have any subscribers (online).
     * This method ignores filters whose {@link CommandSpyFilter#canSubscribe()} method returns FALSE.
     */
    public static void removeDeadFilters() {
        Set<CommandSpyFilter> filters = getActiveFilters().stream()
                .filter(CommandSpyFilter::canSubscribe)
                .collect(Collectors.toSet());

        filters.stream() //Remove offline subscribers for more accurate results
                .forEach(CommandSpyFilters::removeOfflineSubscribers);

        filters.stream() //Needs the source to be a copy or will throw CME
                .filter(f -> f.getSubscribers().isEmpty())
                .forEach(CommandSpyFilters::unregisterFilter);
    }

    public static void removeOfflineSubscribers(CommandSpyFilter filter) {
        ImmutableList.copyOf(filter.getSubscribers()).stream()
                .filter(id -> Bukkit.getPlayer(id) == null)
                .forEach(filter.getSubscribers()::remove);
    }

    public static Stream<CommandSpyFilter> getSubscribedFilters(UUID subscriberId) {
        return activeFilters.stream()
                .filter(f -> f.getSubscribers().contains(subscriberId));
    }

    public static long unsubscribeFromAll(UUID subscriberId) {
        long rtrn = activeFilters.stream()
                .filter(filter -> filter.canSubscribe() && filter.getSubscribers().remove(subscriberId))
                .count();

        removeDeadFilters();

        return rtrn;
    }

    public static boolean toggleSubscribedAndRegister(CommandSpyFilter filter, Player spy) {
        if (filter.getSubscribers().remove(spy.getUniqueId())) {
            removeDeadFilters();
        } else {
            filter.getSubscribers().add(spy.getUniqueId());
            registerFilter(filter);
        }

        return filter.getSubscribers().contains(spy.getUniqueId());
    }

    public static boolean toggleGlobalFilter(Player spy) {
        return toggleSubscribedAndRegister(ALL_FILTER, spy);
    }

    public static boolean togglePlayerFilter(UUID targetId, Player spy) { //This could be generified more - see instanceof
        return toggleSubscribedAndRegister(activeFilters.stream()
                .filter(f -> f instanceof PlayerCommandSpyFilter && ((PlayerCommandSpyFilter) f).getTarget().equals(targetId))
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

    public static Stream<Pattern> getStringFilterPatterns(String input) {
        Function<String, Pattern> regExPatternBuilder = (str) -> Pattern.compile("(" + str + ")\\s*", Pattern.CASE_INSENSITIVE);
        Function<String, Pattern> stringPatternBuilder = (str) -> Pattern.compile("(^" + str + ")\\s*", Pattern.CASE_INSENSITIVE);

        if (input.startsWith("!r")) {
            return Stream.of(
                    regExPatternBuilder.apply(input.replaceFirst("!r\\s*", "")) //Build a filter for the regex, removing the leading !r and any following whitespace.
            );
        } else {
            PluginCommand foundCommand = Bukkit.getPluginCommand(input);
            List<String> commandsToMatch = new LinkedList<>();

            commandsToMatch.add(input);
            if (foundCommand != null) {
                commandsToMatch.addAll(foundCommand.getAliases());
            }

            return commandsToMatch.stream()
                    .map(Pattern::quote)
                    .map(stringPatternBuilder);
        }
    }
}
