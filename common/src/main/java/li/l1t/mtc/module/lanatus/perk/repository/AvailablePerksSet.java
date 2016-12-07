/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
