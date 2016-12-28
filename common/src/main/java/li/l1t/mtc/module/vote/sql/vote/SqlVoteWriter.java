/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.sql.vote;

import com.google.common.base.Preconditions;
import li.l1t.common.sql.sane.AbstractSqlConnected;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.mtc.module.vote.api.Vote;

import java.util.UUID;

/**
 * Writes votes to a SQL JDBC data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-28
 */
class SqlVoteWriter extends AbstractSqlConnected {
    protected SqlVoteWriter(SaneSql saneSql) {
        super(saneSql);
    }

    public void save(Vote vote) {
        Preconditions.checkNotNull(vote, "vote");
        insertOrUpdate(vote.getUniqueId(), vote.getPlayerId(), vote.getServiceName(), vote.getStreakLength());
    }

    private void insertOrUpdate(UUID voteId, UUID playerId, String serviceName, int streakLength) {
        String playerIdString = playerId == null ? null : playerId.toString();
        sql().updateRaw(
                buildInsert(),
                voteId.toString(), playerIdString,
                serviceName, streakLength,
                playerIdString
        );
    }

    private String buildInsert() {
        return "INSERT INTO " + SqlVoteRepository.TABLE_NAME + " " +
                "SET id=?, player_id=?, username=?, service=?, streak=? " +
                "ON DUPLICATE KEY UPDATE player_id=?";
    }
}
