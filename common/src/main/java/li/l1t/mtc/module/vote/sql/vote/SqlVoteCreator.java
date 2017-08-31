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
