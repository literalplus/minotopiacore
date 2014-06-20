package io.github.xxyy.minotopiacore.chat.cmdspy;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides static utility methods for use with {@link io.github.xxyy.minotopiacore.chat.cmdspy.CommandSpyFilter}s.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 19.6.14
 */
public final class CommandSpyFilters {
    public static CommandSpyFilter ALL_FILTER = new MultiSubscriberCommandSpyFilter("§8[CmdSpy]§7{}: §o/{}", (cmd, plr) -> true) {
        @Override
        public String niceRepresentation() {
            return "(global) all";
        }
    };
    private static Set<CommandSpyFilter> activeFilters = new HashSet<>(Arrays.asList(ALL_FILTER));

    private CommandSpyFilters() {

    }

    public static Set<CommandSpyFilter> getActiveFilters() {
        return activeFilters;
    }

    public static void registerFilter(CommandSpyFilter filter) {
        activeFilters.add(filter);
    }

    public static void removeDeadFilters() {
        activeFilters.removeAll(activeFilters.stream()
                .filter(f -> f.getSubscribers().isEmpty())
                .collect(Collectors.toList()));
    }

    public static Stream<CommandSpyFilter> getSubscribedFilters(UUID subscriberId) {
        return activeFilters.stream()
                .filter(f -> f.getSubscribers().contains(subscriberId));
    }

    public static long unsubscribeFromAll(UUID subscriberId) {
        long rtrn = activeFilters.stream()
                .filter(filter -> filter.subscribable() && filter.getSubscribers().remove(subscriberId))
                .count();

        removeDeadFilters();

        return rtrn;
    }

    public static boolean toggleSubscribedAndRegister(CommandSpyFilter filter, Player spy) {
        if(!filter.getSubscribers().remove(spy.getUniqueId())) {
            filter.getSubscribers().add(spy.getUniqueId());
        } else {
            removeDeadFilters();
        }

        registerFilter(filter);

        return filter.getSubscribers().contains(spy.getUniqueId());
    }

    public static boolean togglePlayerFilter(UUID targetId, Player spy) { //This could eb generified more - see instanceof
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
        return new MultiSubscriberCommandSpyFilter("§e[CmdSpy]§7{}: §o/{}", (cmd, plr) -> plr.getUniqueId().equals(targetId));
    }

    protected static CommandSpyFilter regexFilter(Stream<Pattern> patterns) {
        return new RegExCommandSpyFilter(patterns.collect(Collectors.toList()));
    }

    public static CommandSpyFilter stringFilter(String input) {
        return stringFilter(input, CommandSpyFilters::regexFilter);
    }

    public static CommandSpyFilter stringFilter(String input, Function<Stream<Pattern>, CommandSpyFilter> filterBuilder) {
        Function<String, Pattern> patternBuilder = (str) -> Pattern.compile(str + "\\s*", Pattern.CASE_INSENSITIVE);

        if (input.startsWith("!r")) {
            return filterBuilder.apply(Stream.of(
                    patternBuilder.apply(input.replaceFirst("!r\\s*", "")) //Build a filter for the regex, removing the leading !r and any following whitespace.
            ));
        } else {
            PluginCommand foundCommand = Bukkit.getPluginCommand(input);
            List<String> commandsToMatch = new LinkedList<>();

            commandsToMatch.add(input);
            if (foundCommand != null) {
                commandsToMatch.addAll(foundCommand.getAliases());
            }

            return filterBuilder.apply(commandsToMatch.stream()
                    .map(Pattern::quote)
                    .map(patternBuilder));
        }
    }
}
