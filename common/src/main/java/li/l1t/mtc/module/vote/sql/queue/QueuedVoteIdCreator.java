/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.sql.queue;

import li.l1t.common.sql.sane.util.AbstractJdbcEntityCreator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Creates the unique id of queued votes from JDBC result sets.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-28
 */
public class QueuedVoteIdCreator extends AbstractJdbcEntityCreator<UUID> {
    @Override
    public UUID createFromCurrentRow(ResultSet rs) throws SQLException {
        return uuid(rs, "vote_id");
    }
}
