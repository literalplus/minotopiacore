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

import li.l1t.common.exception.DatabaseException;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.common.sql.sane.result.QueryResult;
import li.l1t.common.sql.sane.util.AbstractJdbcFetcher;
import li.l1t.common.sql.sane.util.JdbcEntityCreator;
import li.l1t.mtc.module.nub.api.NubProtection;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/**
 * Fetches N.u.b. protections from a JDBC SQL data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-08
 */
class JdbcProtectionFetcher extends AbstractJdbcFetcher<NubProtection> {
    JdbcProtectionFetcher(JdbcEntityCreator<? extends NubProtection> creator, SaneSql saneSql) {
        super(creator, saneSql);
    }

    public Optional<NubProtection> findByPlayerId(UUID playerId) {
        try (QueryResult qr = selectByPlayerId(playerId)) {
            if (qr.rs().next()) {
                return Optional.of(creator.createFromCurrentRow(qr.rs()));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw DatabaseException.wrap(e);
        }
    }

    private QueryResult selectByPlayerId(UUID playerId) {
        return sql().query(buildSelect("WHERE player_id = ?"), playerId.toString());
    }

    @Override
    protected String buildSelect(String whereClause) {
        return "SELECT player_id, minutesleft FROM " + SqlProtectionRepository.TABLE_NAME + " " + whereClause;
    }
}
