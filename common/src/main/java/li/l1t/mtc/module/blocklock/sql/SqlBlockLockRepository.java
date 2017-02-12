/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
