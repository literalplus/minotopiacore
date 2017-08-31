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

package li.l1t.mtc.module.nub;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import li.l1t.mtc.module.nub.api.NubProtection;

import java.util.*;

/**
 * Manages local protections.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-08
 */
public class LocalProtectionManager {
    private final Map<UUID, NubProtection> protections = new HashMap<>();

    public void addProtection(NubProtection protection) {
        Preconditions.checkNotNull(protection, "protection");
        protections.put(protection.getPlayerId(), protection);
    }

    public NubProtection removeProtection(UUID playerId) {
        Preconditions.checkNotNull(playerId, "playerId");
        return protections.remove(playerId);
    }

    public Optional<NubProtection> getProtection(UUID playerId) {
        Preconditions.checkNotNull(playerId, "playerId");
        return Optional.ofNullable(protections.get(playerId));
    }

    public boolean hasProtection(UUID playerId) {
        Preconditions.checkNotNull(playerId, "playerId");
        return protections.containsKey(playerId);
    }

    public Collection<NubProtection> getAllProtections() {
        return ImmutableList.copyOf(protections.values());
    }
}
