/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.mute.api;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents information about a player that is muted.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-23
 */
public interface Mute {
    /**
     * Updates the mute represented by this metadata.
     *
     * @param source     the unique id of the player who caused the update
     * @param expiryTime the instant the mute expires at, may not be null
     * @param reason     the reason string provided by the source
     */
    void update(UUID source, Instant expiryTime, String reason);

    /**
     * @return whether this mute has expired
     */
    boolean hasExpired();

    /**
     * @return the unique id of the player affected by this mute
     */
    UUID getPlayerId();

    /**
     * @return the instant that the mute was last updated at
     */
    Instant getUpdateTime();

    /**
     * @return the instant that the mute expires at
     */
    Instant getExpiryTime();

    /**
     * @return the unique id of the player that last updated the mute
     */
    UUID getSourceId();

    /**
     * @return the most recent string reason for this mute
     */
    String getReason();
}
