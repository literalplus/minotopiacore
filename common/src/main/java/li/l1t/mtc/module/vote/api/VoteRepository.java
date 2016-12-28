/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.api;

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

    void save(Vote vote);
}
