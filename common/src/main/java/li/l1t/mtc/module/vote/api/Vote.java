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

package li.l1t.mtc.module.vote.api;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a single vote received by the listener.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-28
 */
public interface Vote {
    /**
     * @return the unique id of this vote
     */
    UUID getUniqueId();

    /**
     * @return the unique id of the player that was resolved to be the target of this vote, or null if no player could
     * be resolved
     */
    UUID getPlayerId();

    /**
     * @param playerId the new player id to set
     */
    void setPlayerId(UUID playerId);

    /**
     * @return whether a player was resolved from the user name this vote was sent to
     */
    boolean hasPlayerId();

    /**
     * @return the name the vote was sent for
     */
    String getUserName();

    /**
     * @return the persistent name of the service this vote was sent from
     */
    String getServiceName();

    /**
     * @return the timestamp the vote was received at
     */
    Instant getTimestamp();

    /**
     * @return the amount of votes on subsequent days, including this vote (1 mean no streak)
     */
    int getStreakLength();
}
