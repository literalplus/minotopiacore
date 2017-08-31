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

package li.l1t.mtc.module.vote.listener;

import com.vexsoftware.votifier.model.VotifierEvent;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.metrics.StatsdModule;
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
    @InjectMe(required = false)
    private StatsdModule statsd;

    @InjectMe
    public VoteListener(VoteService voteService) {
        this.voteService = voteService;
    }

    @EventHandler(ignoreCancelled = true)
    public void onVote(VotifierEvent event) {
        String serviceName = event.getVote().getServiceName();
        String userName = event.getVote().getUsername();
        voteService.handleVote(userName, serviceName);
        recordVoteToStatsd(serviceName);
    }

    private void recordVoteToStatsd(String serviceName) {
        if(statsd != null) {
            String sanitizedServiceName = serviceName.replace('.', '_');
            statsd.statsd().increment("mtc.vote.receive." + sanitizedServiceName);
        }
    }
}
