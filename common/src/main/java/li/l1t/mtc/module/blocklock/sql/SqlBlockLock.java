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
