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

import com.google.common.collect.ImmutableList;
import li.l1t.common.exception.DatabaseException;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.common.sql.sane.result.QueryResult;
import li.l1t.common.sql.sane.util.AbstractJdbcEntityCreator;
import li.l1t.common.sql.sane.util.AbstractJdbcFetcher;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

/**
 * Fetches enabled perks by player.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
class JdbcEnabledPerksFetcher extends AbstractJdbcFetcher<UUID> {
    JdbcEnabledPerksFetcher(SaneSql saneSql) {
        super(new UUIDEntityCreator(), saneSql);
    }

    public Collection<UUID> getEnabledPerksByPlayerId(UUID playerId) {
        ImmutableList.Builder<UUID> resultBuilder = ImmutableList.builder();
        try(QueryResult qr = sql().query(buildSelect("WHERE player_id = ?"), playerId.toString())) {
            while(qr.rs().next()) {
                resultBuilder.add(creator.createFromCurrentRow(qr.rs()));
            }
        } catch (SQLException e) {
            throw DatabaseException.wrap(e);
        }
        return resultBuilder.build();
    }

    @Override
    protected String buildSelect(String whereClause) {
        return "SELECT product_id FROM " + SqlPerkRepository.ENABLED_TABLE_NAME + " "+whereClause;
    }

    private static class UUIDEntityCreator extends AbstractJdbcEntityCreator<UUID> {
        @Override
        public UUID createFromCurrentRow(ResultSet rs) throws SQLException {
            return uuid(rs, "product_id");
        }
    }
}
