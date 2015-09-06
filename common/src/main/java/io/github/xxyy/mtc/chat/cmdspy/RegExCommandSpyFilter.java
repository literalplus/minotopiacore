/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.chat.cmdspy;

import org.bukkit.entity.Player;

import io.github.xxyy.common.util.CommandHelper;

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
        super(notificationFormat, null); //overridden anyways
        this.patterns = patterns;
    }

    @Override
    public boolean matches(String command, Player sender) {
        for(Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(command);
            if(matcher.find()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void notifyOnMatch(String command, Player sender) {
        /*
        This implementation is separate from #matches(...)Z so that we can mark matches in the notification
         */
        for(Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(command);
            if(matcher.find()) {
                String message = formatMatch(matcher, sender, command);

                getOnlineSubscribers()
                        .forEach(plr -> plr.sendMessage(message));
                return;
            }
        }
    }

    protected String formatMatch(Matcher matcher, Player sender, String command) {
        return MessageFormat.format(getNotificationFormat(), sender.getName(), matcher.replaceFirst("§9$1§7 "));
    }

    public boolean hasCommandName(String commandName) {
        return CommandSpyFilters.getStringFilterPatterns(commandName)
                .anyMatch( //Any of the found patterns
                        pat -> patterns.stream(). //has a equal pattern in the known patterns.
                                anyMatch(exPat -> exPat.pattern().equalsIgnoreCase(pat.pattern()))
                );
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    @Override
    public String niceRepresentation() {
        return MessageFormat.format("{0} -> / {1} /ig",
                super.niceRepresentation(),
                CommandHelper.CSCollection(getPatterns().stream()
                        .map(Pattern::pattern)
                        .collect(Collectors.toList()))
        );
    }
}
