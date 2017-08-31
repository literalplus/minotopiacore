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

package li.l1t.mtc.module.lanatus.perk.repository;

import li.l1t.common.sql.sane.AbstractSqlConnected;
import li.l1t.common.sql.sane.SaneSql;

import java.time.Instant;
import java.util.UUID;

/**
 * Makes perks available, either temporarily or permanently, via a JDBC data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
class JdbcAvailablePerksWriter extends AbstractSqlConnected {
    JdbcAvailablePerksWriter(SaneSql saneSql) {
        super(saneSql);
    }

    public void makeAvailablePermanently(UUID playerId, UUID perkId) {
        insertNewRow(playerId, perkId, null);
    }

    public void makeAvailableUntil(UUID playerId, UUID perkId, Instant expiryInstant) {
        insertNewRow(playerId, perkId, expiryInstant);
    }

    private int insertNewRow(UUID playerId, UUID perkId, Instant expiryInstant) {
        return sql().updateRaw(buildInsert(), perkId.toString(), playerId.toString(), expiryInstant, expiryInstant);
    }

    private String buildInsert() {
        return "INSERT INTO " + SqlPerkRepository.AVAILABLE_TABLE_NAME + " " +
                "SET product_id=?, player_id=?, validuntil=? " +
                "ON DUPLICATE KEY UPDATE validuntil=?";
    }
}
