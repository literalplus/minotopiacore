/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.reward;

import li.l1t.mtc.api.chat.ChatConstants;
import li.l1t.mtc.module.vote.api.Vote;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Replaces vote reward macros in strings.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-30
 */
public class MacroReplacementService {
    public static MacroReplacementService INSTANCE = new MacroReplacementService();

    public List<String> replaceMacros(Player player, Vote vote, List<String> input) {
        if (input == null || input.isEmpty()) {
            return Collections.emptyList();
        } else {
            return input.stream()
                    .map(item -> replaceMacros(player, vote, item))
                    .collect(Collectors.toList());
        }
    }

    public String replaceMacros(Player player, Vote vote, String input) {
        if (input == null) {
            return null;
        } else {
            String result = input.replaceAll("\\$player", player.getName())
                    .replaceAll("\\$uuid", player.getUniqueId().toString())
                    .replaceAll("\\$weekday", getCurrentWeekDayNameInGerman())
                    .replaceAll("\\$service", vote.getServiceName())
                    .replaceAll("\\$streak", String.valueOf(currentStreakBasedOn(vote)));
            return ChatConstants.convertCustomColorCodes(result);
        }
    }

    private String getCurrentWeekDayNameInGerman() {
        return LocalDateTime.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN);
    }

    private int currentStreakBasedOn(Vote vote) {
        if (streakHasNotYetExpired(vote)) {
            return vote.getStreakLength();
        } else {
            return 0;
        }
    }

    private boolean streakHasNotYetExpired(Vote vote) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return !voteDay(vote).isBefore(yesterday);
    }

    private LocalDate voteDay(Vote vote) {
        return LocalDate.from(vote.getTimestamp().atZone(ZoneId.systemDefault()));
    }
}
