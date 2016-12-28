/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.sql.vote;

import li.l1t.common.sql.sane.util.AbstractJdbcEntityCreator;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Creates vote instances from SQL result sets.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-28
 */
class SqlVoteCreator extends AbstractJdbcEntityCreator<SqlVote> {
    @Override
    public SqlVote createFromCurrentRow(ResultSet rs) throws SQLException {
        return new SqlVote(
                uuid(rs, "id"), rs.getString("username"),
                rs.getString("service"), rs.getTimestamp("creationdate").toInstant(),
                rs.getInt("streak"), uuid(rs, "player_id")
        );
    }
}
