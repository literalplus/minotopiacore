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

import com.google.common.collect.ImmutableList;
import li.l1t.common.exception.DatabaseException;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.common.sql.sane.result.QueryResult;
import li.l1t.common.sql.sane.util.AbstractJdbcFetcher;
import li.l1t.common.sql.sane.util.JdbcEntityCreator;
import li.l1t.mtc.module.vote.sql.vote.SqlVoteRepository;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.UUID;

/**
 * Fetches the unique ids of queued votes from an underlying JDBC SQL data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-28
 */
public class QueuedVoteFetcher extends AbstractJdbcFetcher<UUID> {
    public QueuedVoteFetcher(JdbcEntityCreator<? extends UUID> creator, SaneSql saneSql) {
        super(creator, saneSql);
    }

    public Collection<UUID> findByUsername(String username) {
        ImmutableList.Builder<UUID> votes = ImmutableList.builder();
        try (QueryResult result = selectByUserName(username)) {
            while (result.rs().next()) {
                votes.add(creator.createFromCurrentRow(result.rs()));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return votes.build();
    }

    private QueryResult selectByUserName(String username) {
        return sql().query(buildInnerJoinSelect("WHERE v.username LIKE ?"), username);
    }

    public void purgeVotesOlderThan(Duration duration) {
        Temporal keepThreshold = duration.abs().subtractFrom(Instant.now());
        sql().updateRaw("DELETE FROM " + SqlVoteQueue.TABLE_NAME + " " +
                        "INNER JOIN " + SqlVoteRepository.TABLE_NAME + " " +
                        "ON v.id = vote_id " +
                        "WHERE v.creationdate < ?",
                keepThreshold);
    }

    private String buildInnerJoinSelect(String whereClause) {
        return buildSelect(
                "INNER JOIN " + SqlVoteRepository.TABLE_NAME + " v " +
                        "ON v.id = vote_id " + whereClause
        );
    }

    @Override
    protected String buildSelect(String whereClause) {
        return "SELECT vote_id FROM " + SqlVoteQueue.TABLE_NAME + " " + whereClause;
    }
}
