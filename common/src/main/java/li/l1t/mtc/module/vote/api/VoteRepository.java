/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.api;

import li.l1t.mtc.module.vote.sql.vote.SqlVote;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * A repository for votes.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-28
 */
public interface VoteRepository {
    Optional<Vote> findVoteById(UUID voteId);

    Optional<Vote> findLatestVoteByPlayer(UUID playerId);

    Collection<Vote> findVotesFromToday();

    /**
     * Creates and saves a vote for given data, inferring all other values from the database.
     *
     * @param username    the username the vote was issued for
     * @param serviceName the service name the vote was received from
     * @param playerId    the unique id of the player the user name was resolved to, or null for none (yet)
     * @return the created vote
     */
    SqlVote createVote(String username, String serviceName, UUID playerId);

    void save(Vote vote);
}
