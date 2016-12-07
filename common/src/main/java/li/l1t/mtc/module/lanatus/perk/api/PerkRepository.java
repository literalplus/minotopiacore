/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk.api;

import li.l1t.lanatus.api.LanatusRepository;
import li.l1t.mtc.module.lanatus.perk.repository.AvailablePerksSet;
import li.l1t.mtc.module.lanatus.perk.repository.PerkMeta;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * A repository for perks.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public interface PerkRepository extends LanatusRepository {
    Optional<PerkMeta> findByProductId(UUID productId);

    AvailablePerksSet findAvailableByPlayerId(UUID playerId);

    boolean isPerkAvailable(UUID playerId, UUID perkId);

    Collection<PerkMeta> findEnabledByPlayerId(UUID playerId);

    boolean isPerkEnabled(UUID playerId, PerkMeta perk);

    boolean isPerkEnabled(UUID playerId, UUID perkId);

    void enablePlayerPerk(UUID playerId, UUID perkId);

    void disablePlayerPerk(UUID playerId, UUID perkId);

    void makeAvailablePermanently(UUID playerId, UUID perkId);

    void makeAvailableUntil(UUID playerId, UUID perkId, Instant expiryTime);
}
