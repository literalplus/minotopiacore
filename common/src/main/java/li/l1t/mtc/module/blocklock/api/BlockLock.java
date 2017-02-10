/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
