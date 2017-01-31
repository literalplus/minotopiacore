/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
                        "x=?, y=?, z=?, world=?, type=?, creatoruuid=?, removaltime=?, removedby=?",
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(),
                lock.getType().name(), lock.getOwnerId().toString(), lock.getRemovalInstant().orElse(null),
                lock.getRemoverId().toString()
        );
    }

    public void update(BlockLock lock) {
        XyLocation loc = lock.getLocation();
        sql().updateRaw(
                "UPDATE " + SqlBlockLockRepository.TABLE_NAME + " SET " +
                        "x=?, y=?, z=?, world=?, type=?, creatoruuid=?, removaltime=?, removedby=? " +
                        "WHERE x=? AND y=? AND z=? AND world=?",
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(),
                lock.getType().name(), lock.getOwnerId().toString(), lock.getRemovalInstant().orElse(null),
                lock.getRemoverId().toString(),
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName()
        );
    }
}
