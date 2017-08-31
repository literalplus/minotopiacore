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

package li.l1t.mtc.module.vote.sql.vote;

import com.google.common.base.Preconditions;
import li.l1t.mtc.module.vote.api.Vote;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a vote backed by a SQL data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-28
 */
public class SqlVote implements Vote {
    private final UUID uniqueId;
    private final String username;
    private final String serviceName;
    private final Instant timestamp;
    private final int streakLength;
    private UUID playerId;

    public SqlVote(UUID uniqueId, String username, String serviceName, Instant timestamp, int streakLength, UUID playerId) {
        Preconditions.checkArgument(streakLength > 0, "streakLength must be positive", streakLength);
        this.uniqueId = Preconditions.checkNotNull(uniqueId, "uniqueId");
        this.username = Preconditions.checkNotNull(username, "username");
        this.serviceName = Preconditions.checkNotNull(serviceName, "serviceName");
        this.timestamp = Preconditions.checkNotNull(timestamp, "timestamp");
        this.streakLength = streakLength;
        this.playerId = playerId;
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public String getUserName() {
        return username;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public int getStreakLength() {
        return streakLength;
    }

    @Override
    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    @Override
    public boolean hasPlayerId() {
        return getPlayerId() != null;
    }


    @Override
    public String toString() {
        return "SqlVote{" +
                "uniqueId=" + uniqueId +
                ", username='" + username + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", timestamp=" + timestamp +
                ", streakLength=" + streakLength +
                ", playerId=" + playerId +
                '}';
    }
}
