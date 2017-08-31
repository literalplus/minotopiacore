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
        insertOrUpdate(
                vote.getUniqueId(), vote.getPlayerId(), vote.getServiceName(),
                vote.getUserName(), vote.getStreakLength()
        );
    }

    private void insertOrUpdate(UUID voteId, UUID playerId, String serviceName, String userName, int streakLength) {
        String playerIdString = playerId == null ? null : playerId.toString();
        sql().updateRaw(
                buildInsert(),
                voteId.toString(), playerIdString,
                userName, serviceName, streakLength,
                playerIdString
        );
    }

    private String buildInsert() {
        return "INSERT INTO " + SqlVoteRepository.TABLE_NAME + " " +
                "SET id=?, player_id=?, username=?, service=?, streak=? " +
                "ON DUPLICATE KEY UPDATE player_id=?";
    }
}
