/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
