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

package li.l1t.mtc.module.nub.repository;

import li.l1t.common.sql.sane.AbstractSqlConnected;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.mtc.module.nub.api.NubProtection;

import java.util.UUID;

/**
 * Writes N.u.b. protection data to an underlying JDBC SQL data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-08
 */
class JdbcProtectionWriter extends AbstractSqlConnected {
    JdbcProtectionWriter(SaneSql saneSql) {
        super(saneSql);
    }

    public void deleteByPlayerId(UUID playerId) {
        sql().updateRaw("DELETE FROM " + SqlProtectionRepository.TABLE_NAME + " WHERE player_id = ?", playerId.toString());
    }

    public void createOrUpdate(NubProtection protection) {
        insertOrUpdateProtection(protection.getMinutesLeft(), protection.getPlayerId());
    }

    private int insertOrUpdateProtection(int minutesLeft, UUID playerId) {
        return sql().updateRaw("INSERT INTO " +
                        SqlProtectionRepository.TABLE_NAME + " " +
                        "SET player_id = ?, minutesleft = ? " +
                        "ON DUPLICATE KEY UPDATE minutesleft = ?",
                playerId.toString(), minutesLeft, minutesLeft);
    }
}
