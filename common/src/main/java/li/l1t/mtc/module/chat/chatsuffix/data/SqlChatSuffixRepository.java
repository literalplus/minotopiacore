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

package li.l1t.mtc.module.chat.chatsuffix.data;

import li.l1t.common.exception.InternalException;
import li.l1t.common.sql.QueryResult;
import li.l1t.common.sql.SpigotSql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * A chat suffix repository backed by a SQL database.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public class SqlChatSuffixRepository implements ChatSuffixRepository {
    public static final String TABLE_NAME = "mt_main.mtc_chatsuffix";
    private final SpigotSql sql;

    public SqlChatSuffixRepository(SpigotSql sql) {
        this.sql = sql;
    }

    @Override
    public String findChatSuffixById(UUID playerId) {
        try (QueryResult qr = executeSelectFor(playerId)) {
            return getChatSuffixFromResultSet(qr.getResultSet());
        } catch (SQLException e) {
            throw InternalException.wrap(e);
        }
    }

    private String getChatSuffixFromResultSet(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return "";
        } else {
            return resultSet.getString("suffix");
        }
    }

    private QueryResult executeSelectFor(UUID playerId) throws SQLException {
        return sql.executeQueryWithResult(
                "SELECT suffix FROM " + TABLE_NAME + " WHERE player_id = ?",
                playerId.toString()
        );
    }

    @Override
    public void saveChatSuffix(UUID playerId, String chatSuffix) {
        sql.executeSimpleUpdateAsync(
                "INSERT INTO " + TABLE_NAME + " SET " +
                        "player_id = ?, suffix = ? " +
                        "ON DUPLICATE KEY UPDATE suffix = ?",
                playerId.toString(), chatSuffix, chatSuffix
        );
    }

    @Override
    public void clearCache() {
        //no cache
    }
}
