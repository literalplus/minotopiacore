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
