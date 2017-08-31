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

import li.l1t.common.util.PredicateHelper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stores the set of perks available to a player.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public class AvailablePerksSet {
    private final Set<AvailablePerk> availablePerks;

    public AvailablePerksSet(Collection<AvailablePerk> availablePerks) {
        this.availablePerks = new HashSet<>(availablePerks);
    }

    public Set<AvailablePerk> getValidPerks() {
        return availablePerks.stream()
                .filter(PredicateHelper.not(AvailablePerk::isExpired))
                .collect(Collectors.toSet());
    }

    public Set<UUID> getValidPerkIds() {
        return availablePerks.stream()
                .filter(PredicateHelper.not(AvailablePerk::isExpired))
                .map(AvailablePerk::getProductId)
                .collect(Collectors.toSet());
    }

    public Set<AvailablePerk> removeExpired() {
        return availablePerks.stream()
                .filter(AvailablePerk::isExpired)
                .peek(availablePerks::remove)
                .collect(Collectors.toSet());
    }

    public Stream<AvailablePerk> stream() {
        return availablePerks.stream();
    }
}
