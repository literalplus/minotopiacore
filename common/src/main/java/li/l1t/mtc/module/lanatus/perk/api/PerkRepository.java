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
