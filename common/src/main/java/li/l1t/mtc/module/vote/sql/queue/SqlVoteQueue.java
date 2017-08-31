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

package li.l1t.mtc.module.vote.sql.queue;

import li.l1t.common.sql.sane.AbstractSqlConnected;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.mtc.api.module.inject.InjectMe;
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

    @InjectMe
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
    public void deleteVoteFromQueue(Vote vote) {
        writer.deleteQueuedVote(vote.getUniqueId());
    }

    @Override
    public void purgeVotesOlderThan(Duration duration) {
        fetcher.purgeVotesOlderThan(duration);
    }
}
