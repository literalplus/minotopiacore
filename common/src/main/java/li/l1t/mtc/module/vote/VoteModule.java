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

package li.l1t.mtc.module.vote;

import li.l1t.common.sql.sane.SaneSql;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.vote.command.RewardTestCommand;
import li.l1t.mtc.module.vote.listener.QueueJoinListener;
import li.l1t.mtc.module.vote.listener.VoteListener;
import li.l1t.mtc.module.vote.reminder.ReminderConfig;
import li.l1t.mtc.module.vote.reminder.VoteReminderTask;
import li.l1t.mtc.module.vote.reward.loader.RewardConfigs;
import li.l1t.mtc.module.vote.sql.queue.SqlVoteQueue;
import li.l1t.mtc.module.vote.sql.vote.SqlVoteRepository;

import java.time.Duration;

/**
 * Main entry point for the Vote module which listens for votes and dispatches rewards.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-28
 */
public class VoteModule extends ConfigurableMTCModule {
    private final RewardConfigs rewardConfigs;
    private final ReminderConfig reminderConfig;
    private final SqlVoteQueue voteQueue;
    private final SqlVoteRepository voteRepository;

    @InjectMe
    protected VoteModule(MTCPlugin plugin, SaneSql sql, RewardConfigs rewardConfigs,
                         ReminderConfig reminderConfig, SqlVoteQueue voteQueue, SqlVoteRepository voteRepository) {
        super("Vote", "modules/vote/vote.cfg.yml", ClearCacheBehaviour.RELOAD, false);
        this.rewardConfigs = rewardConfigs;
        this.reminderConfig = reminderConfig;
        this.voteQueue = voteQueue;
        this.voteRepository = voteRepository;
        ConfigurationRegistration.registerAll();
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        registerListener(inject(VoteListener.class));
        registerListener(inject(QueueJoinListener.class));
        registerCommand(inject(RewardTestCommand.class), "rwtest");
        inject(VoteReminderTask.class).start();
    }

    @Override
    public void clearCache(boolean forced, MTCPlugin plugin) {
        //RewardConfigs is registered in the ManagedConfiguration constructor
        voteQueue.purgeVotesOlderThan(Duration.ofDays(14L));
    }

    @Override
    protected void reloadImpl() {
        rewardConfigs.loadAll();
        reminderConfig.load(configuration);
        configuration.options().copyDefaults(true);
        save();
    }

    public SqlVoteQueue getVoteQueue() {
        return voteQueue;
    }

    public SqlVoteRepository votes() {
        return voteRepository;
    }
}
