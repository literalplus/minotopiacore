/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.listener;

import com.vexsoftware.votifier.model.VotifierEvent;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.vote.service.VoteService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Listens for vote events and dispatches them to rewards.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-12
 */
public class VoteListener implements Listener {
    private final VoteService voteService;

    @InjectMe
    public VoteListener(VoteService voteService) {
        this.voteService = voteService;
    }

    @EventHandler(ignoreCancelled = true)
    public void onVote(VotifierEvent event) {
        voteService.handleVote(event.getVote().getUsername(), event.getVote().getServiceName());
    }
}
