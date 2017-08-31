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
