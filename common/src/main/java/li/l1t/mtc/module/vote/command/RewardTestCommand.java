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

package li.l1t.mtc.module.vote.command;

import li.l1t.common.command.BukkitExecution;
import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.command.MTCExecutionExecutor;
import li.l1t.mtc.module.vote.reward.loader.RewardConfig;
import li.l1t.mtc.module.vote.reward.loader.RewardConfigs;
import li.l1t.mtc.module.vote.service.VoteService;

import java.util.function.Supplier;

/**
 * Executes the /rwtest command, which allows to test the vote rewards.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-13
 */
public class RewardTestCommand extends MTCExecutionExecutor {
    private final RewardConfigs rewards;
    private final VoteService voteService;

    @InjectMe
    public RewardTestCommand(RewardConfigs rewards, VoteService voteService) {
        this.rewards = rewards;
        this.voteService = voteService;
    }

    @Override
    public boolean execute(BukkitExecution exec) throws UserException, InternalException {
        exec.requirePermission("mtc.vote.rwtest");
        if (exec.hasNoArgs()) {
            exec.respondUsage("", "<Spieler> [Service]", "Gibt testweise die Votebelohnung aus.");
            exec.respond(MessageType.RESULT_LINE, "Wenn kein Service angegeben wird, wird irgendeiner verwendet," +
                    " für den Belohnungen konfiguriert sind.");
            return true;
        }

        String userName = exec.arg(0);
        String serviceName = exec.findArg(1).orElseGet(firstServiceName());
        voteService.handleVote(userName, serviceName);
        exec.respond(MessageType.RESULT_LINE_SUCCESS,
                "Dank deiner internationalen konspirativen Beziehungen konntest du" +
                        " einen Vote von %s über %s vortäuschen.",
                userName, serviceName);
        return true;
    }

    public Supplier<String> firstServiceName() {
        return () -> rewards.configs()
                .findFirst()
                .map(RewardConfig::getServiceName)
                .orElseThrow(() -> new UserException("Es gibt keine Belohnungen. Gib explizit einen Servicenamen an."));
    }
}
