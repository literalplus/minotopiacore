/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.chatsuffix.data;

import li.l1t.common.sql.QueryResult;
import li.l1t.common.sql.SpigotSql;
import li.l1t.mtc.api.exception.InternalException;

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
