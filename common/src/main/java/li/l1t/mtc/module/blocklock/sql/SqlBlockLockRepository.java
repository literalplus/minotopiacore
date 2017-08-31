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

import com.google.common.base.Preconditions;
import li.l1t.common.misc.XyLocation;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.blocklock.api.BlockLock;
import li.l1t.mtc.module.blocklock.api.BlockLockRepository;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * A block lock repository backed by a SQL data source. Does not apply caching because, usually, a single block would
 * only be accessed once, not all the time.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-31
 */
public class SqlBlockLockRepository implements BlockLockRepository {
    public static final String TABLE_NAME = "mt_main.mtc_block_lock";
    private final JdbcBlockLockFetcher fetcher;
    private final JdbcBlockLockWriter writer;

    @InjectMe
    public SqlBlockLockRepository(JdbcBlockLockFetcher fetcher, JdbcBlockLockWriter writer) {
        this.fetcher = fetcher;
        this.writer = writer;
    }

    @Override
    public Optional<BlockLock> findLockAt(Location location) {
        return fetcher.findByLocation(location)
                .map(BlockLock.class::cast);
    }

    @Override
    public void lockBlock(Block block, UUID ownerId) {
        Location location = block.getLocation();
        SqlBlockLock lock = new SqlBlockLock(
                XyLocation.of(location), Instant.now(), block.getType(), ownerId, null, null
        );
        writer.create(lock);
    }

    @Override
    public void unlockBlock(Block block, UUID removerId) {
        fetcher.findByLocation(block.getLocation())
                .ifPresent(currentLock -> {
                    currentLock.markRemoved(removerId);
                    writer.update(currentLock);
                });
    }

    @Override
    public void deleteLock(BlockLock lock) {
        Preconditions.checkNotNull(lock, "lock");
        writer.delete(lock);
    }
}
