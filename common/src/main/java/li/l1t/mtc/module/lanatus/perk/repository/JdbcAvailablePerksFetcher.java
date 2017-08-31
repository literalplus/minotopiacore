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
import li.l1t.common.sql.sane.util.AbstractJdbcFetcher;
import li.l1t.common.sql.sane.util.JdbcEntityCreator;

import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

/**
 * Fetches available perks from a JDBC data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
class JdbcAvailablePerksFetcher extends AbstractJdbcFetcher<AvailablePerk> {
    public JdbcAvailablePerksFetcher(JdbcEntityCreator<AvailablePerk> creator, SaneSql saneSql) {
        super(creator, saneSql);
    }

    public AvailablePerksSet findByPlayerId(UUID playerId) {
        return evictExpired(findByPlayerIdNoExpiryCheck(playerId));
    }

    private AvailablePerksSet evictExpired(AvailablePerksSet perks) {
        Set<AvailablePerk> expiredPerks = perks.removeExpired();
        if (!expiredPerks.isEmpty()) {
            evictExpiredPerks();
        }
        return perks;
    }

    private AvailablePerksSet findByPlayerIdNoExpiryCheck(UUID playerId) {
        ImmutableList.Builder<AvailablePerk> resultBuilder = ImmutableList.builder();
        try (QueryResult qr = queryByPlayerId(playerId)) {
            while (qr.rs().next()) {
                resultBuilder.add(creator.createFromCurrentRow(qr.rs()));
            }
        } catch (SQLException e) {
            throw DatabaseException.wrap(e);
        }
        return new AvailablePerksSet(resultBuilder.build());
    }

    private QueryResult queryByPlayerId(UUID playerId) {
        return sql().query(selectByPlayerId(), playerId.toString());
    }

    private String selectByPlayerId() {
        return buildSelect("player_id = ?");
    }

    @Override
    protected String buildSelect(String whereClause) {
        return "SELECT product_id, player_id, validuntil FROM " + SqlPerkRepository.AVAILABLE_TABLE_NAME + " WHERE " + whereClause;
    }

    private void evictExpiredPerks() {
        deleteExpired();
    }

    private int deleteExpired() {
        return sql().updateRaw(buildDelete("validuntil < NOW()"));
    }

    private String buildDelete(String whereClause) {
        return "DELETE FROM " + SqlPerkRepository.AVAILABLE_TABLE_NAME + " WHERE " + whereClause;
    }
}
