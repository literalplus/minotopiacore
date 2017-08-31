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

package li.l1t.mtc.module.vote.reminder;

import com.google.common.base.Preconditions;
import li.l1t.common.util.task.NonAsyncBukkitRunnable;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.vote.api.Vote;
import li.l1t.mtc.module.vote.api.VoteRepository;
import li.l1t.mtc.module.vote.reward.MacroReplacementService;
import li.l1t.mtc.module.vote.sql.vote.SqlVoteRepository;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

/**
 * Periodically reminds people who have not yet voted today to go and vote.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-24
 */
public class VoteReminderTask extends NonAsyncBukkitRunnable {
    private final ReminderConfig config;
    private final MTCPlugin plugin;
    private final VoteRepository votes;

    @InjectMe
    public VoteReminderTask(ReminderConfig config, MTCPlugin plugin, SqlVoteRepository votes) {
        this.config = config;
        this.plugin = plugin;
        this.votes = votes;
    }

    public void start() {
        if (isCheckEnabled()) {
            runTaskTimer(plugin, config.getCheckIntervalTicks());
        }
    }

    @Override
    public void run() {
        if (isCheckEnabled()) {
            plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getUniqueId)
                    .map(votes::findLatestVoteByPlayer)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(this::wasNotSubmittedToday)
                    .filter(Vote::hasPlayerId)
                    .forEach(this::sendReminderMessage);
        }
    }

    private boolean wasNotSubmittedToday(Vote vote) {
        return !voteDay(vote).equals(LocalDate.now());
    }

    private LocalDate voteDay(Vote vote) {
        return LocalDate.from(vote.getTimestamp().atZone(ZoneId.systemDefault()));
    }

    private void sendReminderMessage(Vote latestVote) {
        Player player = plugin.getServer().getPlayer(latestVote.getPlayerId());
        Preconditions.checkNotNull(player, "player");
        String message = MacroReplacementService.INSTANCE.replaceMacros(player, latestVote, config.getMessage());
        player.sendMessage(message);
    }

    private boolean isCheckEnabled() {
        return config.getCheckIntervalSeconds() > 0;
    }
}
