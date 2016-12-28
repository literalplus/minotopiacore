/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
