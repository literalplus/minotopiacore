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
