/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.blocklock.api;

import li.l1t.common.exception.DatabaseException;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Optional;
import java.util.UUID;

/**
 * A repository for locked blocks.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-31
 */
public interface BlockLockRepository {
    /**
     * Finds the lock metadata for given location, if any.
     *
     * @param location the location to find the lock metadata for
     * @return the lock metadata for given location, or an empty optional if none
     * @throws DatabaseException if a database error occurs
     */
    Optional<BlockLock> findLockAt(Location location);

    /**
     * Locks a block using a block lock an writes that to the database.
     *
     * @param block   the block to lock
     * @param ownerId the unique id of the new owner of given block
     * @throws DatabaseException if a database error occurs
     * @throws DatabaseException if there is already a lock at given block
     */
    void lockBlock(Block block, UUID ownerId);

    /**
     * Unlocks given block, if a lock is present. If there is no lock present, no lock is created.
     *
     * @param block     the block to unlock
     * @param removerId the unique id of the player who unlocked given block
     * @throws DatabaseException if a database error occurs
     */
    void unlockBlock(Block block, UUID removerId);

    /**
     * Completely and permanently removes given lock from the database.
     *
     * @param lock the lock to delete
     */
    void deleteLock(BlockLock lock);
}
