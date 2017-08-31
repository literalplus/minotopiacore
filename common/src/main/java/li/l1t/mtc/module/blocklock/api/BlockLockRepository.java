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
