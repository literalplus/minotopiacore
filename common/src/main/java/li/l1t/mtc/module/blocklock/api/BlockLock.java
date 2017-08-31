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

import li.l1t.common.misc.XyLocation;
import org.bukkit.Material;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a lock on a block that designates the player who placed it.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-31
 */
public interface BlockLock {
    XyLocation getLocation();

    Instant getCreationInstant();

    Material getType();

    /**
     * @return the unique id of the player who last placed a block at the locked location
     */
    UUID getOwnerId();

    /**
     * @return an optional containing the instant the locked block was removed at, or an empty instant if the block is
     * still placed
     */
    Optional<Instant> getRemovalInstant();

    /**
     * @return an optional containing the unique id of the player who removed the last block at the locked location, or
     * an empty optional if the block is still placed
     */
    Optional<UUID> getRemoverId();

    /**
     * @return whether the block at the locked location has been removed
     */
    boolean hasBeenRemoved();
}
