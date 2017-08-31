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

package li.l1t.mtc.module.chat.chatsuffix;

import li.l1t.common.sql.SafeSql;
import li.l1t.common.sql.SpigotSql;
import li.l1t.mtc.MTC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts old chat suffix format to new one on join
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-24
 */
@Deprecated //5 minutes of work, sorry
public class ChatSuffixJoinConverter implements Listener {
    private final ChatSuffixModule module;
    public static Map<String, String> cfCache = new HashMap<>(); //chatfarbe cache

    public ChatSuffixJoinConverter(ChatSuffixModule module) {
        this.module = module;
    }

    public static String getDbChatColorByPlayer(String plrName) {
        SafeSql sql = MTC.instance().getSql();
        String defaultCol = MTC.instance().getConfig().getString("chat.farbe.default", "§f");
        if (sql == null) {
            System.err.println("[MTC] Tried to fetch player chat color before reload was complete!");
            return defaultCol;
        }
        ResultSet rs = sql.safelyExecuteQuery("SELECT chatfarbe FROM " + sql.dbName + ".mts_chatfarbe WHERE user_name=?", plrName);
        try {
            if (rs == null || !rs.isBeforeFirst()) {
                return defaultCol;
            }
            rs.next();
            return rs.getString("chatfarbe");
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "[MTC] Could not fetch plyer chat color.");
            return defaultCol;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent evt) {
        String legacyColor = getDbChatColorByPlayer(evt.getPlayer().getName());
        String defaultCol = MTC.instance().getConfig().getString("chat.farbe.default", "§f");
        if (legacyColor.equalsIgnoreCase(defaultCol)) {
            return;
        }
        module.getRepository().saveChatSuffix(
                evt.getPlayer().getUniqueId(),
                ChatColor.translateAlternateColorCodes('&', legacyColor)
        );
        SpigotSql sql = module.getPlugin().getSql();
        sql.executeSimpleUpdateAsync("DELETE FROM " + sql.dbName + ".mts_chatfarbe " +
                "WHERE user_name=?", evt.getPlayer().getName());
    }
}
