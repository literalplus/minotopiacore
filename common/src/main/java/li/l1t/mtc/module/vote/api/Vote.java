/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
