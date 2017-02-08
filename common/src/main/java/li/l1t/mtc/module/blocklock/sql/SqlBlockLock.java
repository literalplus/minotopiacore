/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.blocklock.sql;

import li.l1t.common.misc.XyLocation;
import li.l1t.mtc.module.blocklock.api.BlockLock;
import org.bukkit.Material;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * A block lock backed by a SQL data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-31
 */
class SqlBlockLock implements BlockLock {
    private final XyLocation location;
    private final Instant creationInstant;
    private final Material type;
    private final UUID ownerId;
    private Instant removalInstant;
    private UUID removerId;

    SqlBlockLock(XyLocation location, Instant creationInstant, Material type, UUID ownerId,
                 Instant removalInstant, UUID removerId) {
        this.location = location;
        this.creationInstant = creationInstant;
        this.type = type;
        this.ownerId = ownerId;
        this.removalInstant = removalInstant;
        this.removerId = removerId;
    }

    @Override
    public XyLocation getLocation() {
        return location;
    }

    @Override
    public Instant getCreationInstant() {
        return creationInstant;
    }

    @Override
    public Material getType() {
        return type;
    }

    @Override
    public UUID getOwnerId() {
        return ownerId;
    }

    @Override
    public Optional<Instant> getRemovalInstant() {
        return Optional.ofNullable(removalInstant);
    }

    @Override
    public Optional<UUID> getRemoverId() {
        return Optional.ofNullable(removerId);
    }

    public void markRemoved(UUID removerId) {
        this.removerId = removerId;
        this.removalInstant = Instant.now();
    }

    @Override
    public boolean hasBeenRemoved() {
        return removerId != null || removalInstant != null;
    }

    @Override
    public String toString() {
        return "SqlBlockLock{" +
                "location=" + location +
                ", creationInstant=" + creationInstant +
                ", type=" + type +
                ", ownerId=" + ownerId +
                ", removalInstant=" + removalInstant +
                ", removerId=" + removerId +
                '}';
    }
}
