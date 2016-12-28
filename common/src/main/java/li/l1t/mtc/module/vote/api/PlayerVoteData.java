/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.api;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents the data stored for a player related to the vote module.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-28
 */
public interface PlayerVoteData {
    UUID getPlayerId();

    Optional<Vote> getLatestVote();

    LocalDate getLatestVoteDay();

    int getVoteCount();
}
