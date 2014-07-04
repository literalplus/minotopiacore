package io.github.xxyy.minotopiacore.chat.cmdspy;

import io.github.xxyy.common.util.CommandHelper;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Handles commandspy messages with regex.
 *
* @author <a href="http://xxyy.github.io/">xxyy</a>
* @since 20.6.14
*/
public class RegExCommandSpyFilter extends MultiSubscriberCommandSpyFilter {
    private final List<Pattern> patterns;

    public RegExCommandSpyFilter(List<Pattern> patterns) {
        this("§9[CmdSpy]§7{0}: §o/{1}", patterns);
    }

    public RegExCommandSpyFilter(String notificationFormat, List<Pattern> patterns) {
        super(notificationFormat, (cmd, plr) -> patterns.stream().anyMatch(pat -> pat.matcher(cmd).matches()));
        this.patterns = patterns;
    }

    @Override
    public boolean notifyOnMatch(String command, Player sender) {
        return patterns.stream().filter(pattern -> {
            Matcher matcher = pattern.matcher(command);

            if (matcher.find()) {
                String message = MessageFormat.format(getNotificationFormat(), sender.getName(), matcher.replaceFirst("§9$1§7"));

                getOnlineSubscriberStream()
                        .forEach(plr -> plr.sendMessage(message));

                return true;
            }
            return false;
        }).findAny().isPresent();
    }

    public boolean hasCommandName(String commandName) {
        String regex;
        if(commandName.startsWith("!r")) {
            regex = commandName.replaceFirst("!r\\s*", "").concat("\\s*");
        } else {
            regex = Pattern.quote(commandName).concat("\\s*");
        }

        return patterns.stream()
                .anyMatch(pat -> pat.pattern().equalsIgnoreCase(regex));
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    @Override
    public String niceRepresentation() {
        return MessageFormat.format("{0} -> /{1}/ig",
                super.niceRepresentation(),
                CommandHelper.CSCollection(getPatterns().stream()
                        .map(Pattern::pattern)
                        .collect(Collectors.toList()))
        );
    }
}
