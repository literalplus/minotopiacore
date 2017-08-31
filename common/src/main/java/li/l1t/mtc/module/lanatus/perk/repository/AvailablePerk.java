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

package li.l1t.mtc.module.lanatus.perk.repository;

import li.l1t.mtc.module.lanatus.base.product.AbstractProductMetadata;

import java.time.Instant;
import java.util.UUID;

/**
 * Stores metadata for an available perk.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public class AvailablePerk extends AbstractProductMetadata {
    private final Instant expiryInstant;
    private final UUID playerId;

    public AvailablePerk(UUID productId, Instant expiryInstant, UUID playerId) {
        super(productId);
        this.expiryInstant = expiryInstant;
        this.playerId = playerId;
    }

    public Instant getExpiryInstant() {
        return expiryInstant;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public boolean isExpired() {
        return expiryInstant != null && expiryInstant.isAfter(Instant.now());
    }
}
