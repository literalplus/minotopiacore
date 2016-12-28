/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.sql.queue;

import li.l1t.common.sql.sane.AbstractSqlConnected;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.mtc.module.vote.api.Vote;
import li.l1t.mtc.module.vote.api.VoteQueue;

import java.time.Duration;
import java.util.Collection;
import java.util.UUID;

/**
 * Manages a vote queue backed by a JDBC SQL database.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-28
 */
public class SqlVoteQueue extends AbstractSqlConnected implements VoteQueue {
    public static final String TABLE_NAME = "mt_main.mtc_vote_queue";
    private final QueuedVoteFetcher fetcher;
    private final QueuedVoteWriter writer;

    public SqlVoteQueue(SaneSql sql) {
        super(sql);
        fetcher = new QueuedVoteFetcher(new QueuedVoteIdCreator(), sql);
        writer = new QueuedVoteWriter(sql);
    }

    @Override
    public Collection<UUID> findQueuedVotes(String userName) {
        return fetcher.findByUsername(userName);
    }

    @Override
    public void queueVote(Vote vote) {
        writer.writeQueuedVote(vote.getUniqueId());
    }

    @Override
    public void purgeVotesOlderThan(Duration duration) {
        fetcher.purgeVotesOlderThan(duration);
    }
}
