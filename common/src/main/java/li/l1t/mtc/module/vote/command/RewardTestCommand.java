/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.command;

import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.command.CommandExecution;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.command.BukkitExecutionExecutor;
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
public class RewardTestCommand extends BukkitExecutionExecutor {
    private final RewardConfigs rewards;
    private final VoteService voteService;

    @InjectMe
    public RewardTestCommand(RewardConfigs rewards, VoteService voteService) {
        this.rewards = rewards;
        this.voteService = voteService;
    }

    @Override
    public boolean execute(CommandExecution exec) throws UserException, InternalException {
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
                .orElseThrow(() -> new UserException("Es gibt keine Belohnungen."));
    }
}
