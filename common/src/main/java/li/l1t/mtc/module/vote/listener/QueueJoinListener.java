/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.listener;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.vote.service.VoteService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Listens for join events and applies queued votes for players.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-13
 */
public class QueueJoinListener implements Listener {
    private final VoteService voteService;
    private final MTCPlugin plugin;

    @InjectMe
    public QueueJoinListener(VoteService voteService, MTCPlugin plugin) {
        this.voteService = voteService;
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin,
                () -> voteService.checkQueuedVotes(
                        player,
                        vote -> voteService.dispatchVoteReward(player, vote)
                )
        );
    }
}
