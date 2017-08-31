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
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Enables and disables perks via a JDBC data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
class JdbcEnabledPerksWriter extends AbstractSqlConnected {
    JdbcEnabledPerksWriter(SaneSql saneSql) {
        super(saneSql);
    }

    public void enablePlayerPerk(UUID playerId, UUID perkId) {
        insertWithPlayerIdAndProductId(playerId, perkId);
    }

    private int insertWithPlayerIdAndProductId(UUID playerId, UUID perkId) {
        return sql().updateRaw(buildInsert(), perkId.toString(), playerId.toString());
    }

    private String buildInsert() {
        return "INSERT IGNORE INTO " + SqlPerkRepository.ENABLED_TABLE_NAME + " " +
                "SET product_id=?, player_id=?";
    }


    public void disablePlayerPerk(UUID playerId, UUID perkId) {
        deleteByPlayerIdAndPerkId(playerId, perkId);
    }

    private int deleteByPlayerIdAndPerkId(UUID playerId, UUID perkId) {
        return sql().updateRaw(buildDeleteByProductIdAndPlayerId(), perkId.toString(), playerId.toString());
    }

    private String buildDeleteByProductIdAndPlayerId() {
        return buildDelete("WHERE product_id=? AND player_id=?");
    }

    @NotNull
    private String buildDelete(String whereClause) {
        return "DELETE FROM " + SqlPerkRepository.ENABLED_TABLE_NAME + " " + whereClause;
    }
}
