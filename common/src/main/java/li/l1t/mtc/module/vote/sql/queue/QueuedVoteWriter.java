/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.sql.queue;

import li.l1t.common.sql.sane.AbstractSqlConnected;
import li.l1t.common.sql.sane.SaneSql;

import java.util.UUID;

/**
 * Writes queued votes to a JDBC SQL data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-28
 */
public class QueuedVoteWriter extends AbstractSqlConnected {
    protected QueuedVoteWriter(SaneSql sql) {
        super(sql);
    }

    public void writeQueuedVote(UUID voteId) {
        sql().updateRaw("INSERT INTO " + SqlVoteQueue.TABLE_NAME + " SET vote_id=?", voteId.toString());
    }
}
