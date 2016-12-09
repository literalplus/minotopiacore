/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
