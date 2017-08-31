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
