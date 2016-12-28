/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.api;

import java.time.Duration;
import java.util.Collection;

/**
 * Provides access to the queue of votes that have been queued for players who were offline at the time the vote was
 * received.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-28
 */
public interface VoteQueue {
    Collection<Vote> findQueuedVotes(String userName);

    void purgeVotesOlderThan(Duration duration);
}
