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

import li.l1t.common.misc.XyLocation;
import li.l1t.common.sql.sane.AbstractSqlConnected;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.blocklock.api.BlockLock;

/**
 * Writes block lock metadata to the database.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-31
 */
class JdbcBlockLockWriter extends AbstractSqlConnected {
    @InjectMe
    JdbcBlockLockWriter(SaneSql saneSql) {
        super(saneSql);
    }

    public void delete(BlockLock lock) {
        XyLocation loc = lock.getLocation();
        sql().updateRaw(
                "DELETE FROM " + SqlBlockLockRepository.TABLE_NAME + " WHERE " +
                        "x=? AND y=? AND z=? AND world=?",
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName()
        );
    }

    public void create(BlockLock lock) {
        XyLocation loc = lock.getLocation();
        sql().updateRaw(
                "INSERT INTO " + SqlBlockLockRepository.TABLE_NAME + " SET " +
                        "x=?, y=?, z=?, world=?, type=?, creatoruuid=?, removaldate=?, removeruuid=?",
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(),
                lock.getType().name(), lock.getOwnerId().toString(), lock.getRemovalInstant().orElse(null),
                lock.getRemoverId().orElse(null)
        );
    }

    public void update(BlockLock lock) {
        XyLocation loc = lock.getLocation();
        sql().updateRaw(
                "UPDATE " + SqlBlockLockRepository.TABLE_NAME + " SET " +
                        "x=?, y=?, z=?, world=?, type=?, creatoruuid=?, removaldate=?, removeruuid=? " +
                        "WHERE x=? AND y=? AND z=? AND world=?",
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(),
                lock.getType().name(), lock.getOwnerId().toString(), lock.getRemovalInstant().orElse(null),
                lock.getRemoverId().orElse(null),
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName()
        );
    }
}
