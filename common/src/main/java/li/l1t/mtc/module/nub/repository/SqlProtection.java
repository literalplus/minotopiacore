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

package li.l1t.mtc.module.nub.repository;

import com.google.common.base.Preconditions;
import li.l1t.mtc.module.nub.api.NubProtection;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Simple implementation of a N.u.b. protection metadata storage object, using an SQL data source as backend and
 * calculating the minutes left on the fly from the instant the object was created.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-08
 */
class SqlProtection implements NubProtection {
    private final UUID playerId;
    private final Instant creationInstant;
    private final int minutesLeftAtStart;
    private final Instant estimatedExpiry;

    public SqlProtection(UUID playerId, int minutesLeftAtStart) {
        this.playerId = Preconditions.checkNotNull(playerId, "playerId");
        this.minutesLeftAtStart = minutesLeftAtStart;
        this.creationInstant = Instant.now();
        this.estimatedExpiry = creationInstant.plus(minutesLeftAtStart, ChronoUnit.MINUTES);
    }

    @Override
    public UUID getPlayerId() {
        return playerId;
    }

    @Override
    public Instant getEstimatedExpiry() {
        return estimatedExpiry;
    }

    @Override
    public int getMinutesLeft() {
        int minutesSinceCreation = Math.toIntExact(ChronoUnit.MINUTES.between(creationInstant, Instant.now()));
        if (minutesSinceCreation > minutesLeftAtStart) {
            return 0;
        } else {
            return minutesLeftAtStart - minutesSinceCreation;
        }
    }

    @Override
    public boolean isExpired() {
        return getMinutesLeft() <= 0;
    }
}
