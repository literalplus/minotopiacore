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

package li.l1t.mtc.module.vote.sql.vote;

import com.google.common.collect.ImmutableList;
import li.l1t.common.exception.DatabaseException;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.common.sql.sane.result.QueryResult;
import li.l1t.common.sql.sane.util.AbstractJdbcFetcher;
import li.l1t.common.sql.sane.util.JdbcEntityCreator;
import li.l1t.mtc.module.vote.api.Vote;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Fetches vote data from a JDBC data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-28
 */
class SqlVoteFetcher extends AbstractJdbcFetcher<Vote> {
    SqlVoteFetcher(JdbcEntityCreator<? extends Vote> creator, SaneSql saneSql) {
        super(creator, saneSql);
    }

    Optional<Vote> findVoteById(UUID voteId) {
        try (QueryResult result = selectVoteById(voteId)) {
            if (result.rs().next()) {
                return Optional.of(creator.createFromCurrentRow(result.rs()));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private QueryResult selectVoteById(UUID voteId) {
        return sql().query(buildSelect("id=?"), voteId.toString());
    }

    Optional<Vote> findLatestVoteByPlayerId(UUID playerId) {
        try (QueryResult result = selectLatestVoteByPlayerId(playerId)) {
            if (result.rs().next()) {
                return Optional.of(creator.createFromCurrentRow(result.rs()));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private QueryResult selectLatestVoteByPlayerId(UUID playerId) {
        return sql().query(buildSelect("player_id=? ORDER BY creationdate DESC"), playerId.toString());
    }

    Optional<Vote> findLatestVoteByUserName(String username) {
        try (QueryResult result = selectLatestVoteByUserName(username)) {
            if (result.rs().next()) {
                return Optional.of(creator.createFromCurrentRow(result.rs()));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private QueryResult selectLatestVoteByUserName(String username) {
        return sql().query(buildSelect("username=? ORDER BY creationdate DESC"), username);
    }


    Collection<Vote> findVotesFromToday() {
        ImmutableList.Builder<Vote> votes = ImmutableList.builder();
        try (QueryResult result = selectVotesCreatedToday()) {
            while (result.rs().next()) {
                votes.add(creator.createFromCurrentRow(result.rs()));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return votes.build();
    }

    private QueryResult selectVotesCreatedToday() {
        return sql().query(buildSelectVotesCreatedToday());
    }

    private String buildSelectVotesCreatedToday() {
        return "SELECT " + relevantColumns() + " FROM " + SqlVoteRepository.TABLE_NAME + " " +
                "WHERE creationdate > CURRENT_DATE";
    }

    @Override
    protected String buildSelect(String whereClause) {
        return "SELECT " + relevantColumns() + " FROM " +
                SqlVoteRepository.TABLE_NAME + " " +
                "WHERE " + whereClause;
    }

    private String relevantColumns() {
        return "id, player_id, username, creationdate, service, streak";
    }
}
