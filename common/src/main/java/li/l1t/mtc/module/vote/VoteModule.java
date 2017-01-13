/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote;

import li.l1t.common.sql.sane.SaneSql;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.vote.listener.QueueJoinListener;
import li.l1t.mtc.module.vote.listener.VoteListener;
import li.l1t.mtc.module.vote.reward.loader.RewardConfigs;
import li.l1t.mtc.module.vote.sql.queue.SqlVoteQueue;
import li.l1t.mtc.module.vote.sql.vote.SqlVoteRepository;

/**
 * Main entry point for the Vote module which listens for votes and dispatches rewards.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-28
 */
public class VoteModule extends ConfigurableMTCModule {
    private final RewardConfigs rewardConfigs;
    private final SqlVoteQueue voteQueue;
    private final SqlVoteRepository voteRepository;

    @InjectMe
    protected VoteModule(MTCPlugin plugin, SaneSql sql, RewardConfigs rewardConfigs,
                         SqlVoteQueue voteQueue, SqlVoteRepository voteRepository) {
        super("Vote", "modules/vote.cfg.yml", ClearCacheBehaviour.RELOAD);
        this.rewardConfigs = rewardConfigs;
        this.voteQueue = voteQueue;
        this.voteRepository = voteRepository;
        ConfigurationRegistration.registerAll();
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        registerListener(inject(VoteListener.class));
        registerListener(inject(QueueJoinListener.class));
    }

    @Override
    protected void reloadImpl() {
        rewardConfigs.loadAll();
    }

    public SqlVoteQueue getVoteQueue() {
        return voteQueue;
    }

    public SqlVoteRepository votes() {
        return voteRepository;
    }
}
