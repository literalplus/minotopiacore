/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
        return expiryInstant.isAfter(Instant.now());
    }
}
