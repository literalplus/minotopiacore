/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
