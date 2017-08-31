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

package li.l1t.mtc.module.blocklock.sql;

import li.l1t.common.exception.DatabaseException;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.common.sql.sane.result.QueryResult;
import li.l1t.common.sql.sane.util.AbstractJdbcFetcher;
import li.l1t.mtc.api.module.inject.InjectMe;
import org.bukkit.Location;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Fetches block lock metadata from a JDBC SQL data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-31
 */
class JdbcBlockLockFetcher extends AbstractJdbcFetcher<SqlBlockLock> {
    @InjectMe
    JdbcBlockLockFetcher(JdbcBlockLockCreator creator, SaneSql sql) {
        super(creator, sql);
    }

    public Optional<SqlBlockLock> findByLocation(Location location) {
        try (QueryResult result = queryByLocation(location)) {
            if (result.rs().next()) {
                return Optional.of(creator.createFromCurrentRow(result.rs()));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private QueryResult queryByLocation(Location location) {
        return sql().query(
                buildSelect("x = ? AND y = ? AND z = ? AND world = ?"),
                location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName()
        );
    }

    @Override
    protected String buildSelect(String whereClause) {
        return "SELECT * FROM " + SqlBlockLockRepository.TABLE_NAME + " WHERE " + whereClause;
    }
}
