/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub.api;

import java.time.Instant;
import java.util.UUID;

/**
 * Stores a player's protection data, as known locally. Might not represent the latest database state.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-08
 */
public interface NubProtection {
    /**
     * @return the unique id of the player this protection applies to
     */
    UUID getPlayerId();

    /**
     * @return the estimated instant of expiration of this protection, assuming the player does not leave the server
     * until then
     */
    Instant getEstimatedExpiry();

    /**
     * @return the current time left on this protection, in minutes, or zero if the protection has expired
     */
    int getMinutesLeft();

    /**
     * @return whether this protection has expired
     */
    boolean isExpired();
}
