/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub;

import com.google.common.base.Preconditions;
import li.l1t.mtc.module.nub.api.NubProtection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    public NubProtection getProtection(UUID playerId) {
        Preconditions.checkNotNull(playerId, "playerId");
        return protections.get(playerId);
    }

    public boolean hasProtection(UUID playerId) {
        Preconditions.checkNotNull(playerId, "playerId");
        return protections.containsKey(playerId);
    }
}
