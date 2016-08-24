/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
