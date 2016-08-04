/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.chat;

import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.common.util.ChatHelper;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.logging.LogManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MTCChatHelper extends ChatHelper {
    public static List<String> spies = new ArrayList<>(); // /mtc spy
    public static volatile Map<Integer, PrivateChat> directChats = new HashMap<>();
    public static Map<String, String> cfCache = new HashMap<>(); //chatfarbe cache
    private static Logger LOGGER = LogManager.getLogger(MTCChatHelper.class);

    public static void clearPrivateChats() {
        if (MTCChatHelper.directChats.size() <= 0) {
            return;
        }
        for (Integer pcId : MTCChatHelper.directChats.keySet()) {
            PrivateChat pc = MTCChatHelper.directChats.get(pcId);
            MTCChatHelper.directChats.remove(pcId);
            for (Player plr : pc.recipients) {
                plr.sendMessage(MTC.chatPrefix + "Du hast den privaten Chat wegen einem Reload verlassen!");
            }
        }
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
            sql.formatAndPrintException(e, "[MTC] Could not fetch player chat color.");
            return defaultCol;
        }
    }

    public static String getFinalChatColorByCSender(CommandSender sender) {
        if (MTCChatHelper.cfCache.containsKey(sender.getName())) {
            return MTCChatHelper.cfCache.get(sender.getName());
        }
        String color = MTCChatHelper.getDbChatColorByPlayer(sender.getName());
        color = MTCChatHelper.parseChatColByCSenderAndPerms(sender, color);
        MTCChatHelper.cfCache.put(sender.getName(), color);
        return color;
    }

    public static String getFinalChatColorByNameIgnorePerms(String plrName) {
        if (MTCChatHelper.cfCache.containsKey(plrName)) {
            return MTCChatHelper.cfCache.get(plrName);
        }
        String color = MTCChatHelper.getDbChatColorByPlayer(plrName);
        OfflinePlayer plr = Bukkit.getOfflinePlayer(plrName);
        if (plr == null || !plr.isOnline()) {
            MTCChatHelper.cfCache.put(plrName, MTCChatHelper.parseChatColByName(plrName, color));
        } else {
            MTCChatHelper.cfCache.put(plrName, MTCChatHelper.parseChatColByCSenderAndPerms(plr.getPlayer(), color));
        }
//		cfCache.put(plrName, color);
        return color;
    }

    public static boolean hasChatColor(String plrName) {
        SafeSql sql = MTC.instance().getSql();
        if (sql == null) {
            System.err.println("[MTC] Tried to ask if player chat color was set before reload was complete!");
            return false;
        }
        ResultSet rs = sql.safelyExecuteQuery("SELECT user_name FROM " + sql.dbName + ".mts_chatfarbe WHERE user_name=?", plrName);
        try {
            return rs.isBeforeFirst();
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "[MTC] Error when trying to ask if a player had a chat color.");
            return false;
        }
    }

    public static String parseChatColByCSenderAndPerms(CommandSender sender, String color) {
        if (!sender.hasPermission("mtc.chatfarbe.use")) {
            return "§f";
        }
        if (!sender.hasPermission("mtc.chatfarbe.special")) {
            color = MTCChatHelper.convertDefaultChatColors(color);
        } else {
            color = ChatColor.translateAlternateColorCodes('&', color);
        }
        color = MTCChatHelper.replaceSpecialWords(color);
        return color;
    }

    public static String parseChatColByName(String name, String color) {
        Player plr = Bukkit.getPlayerExact(name);
        if (plr != null && plr.hasPermission("mtc.chatfarbe.special")) {
            color = MTCChatHelper.convertDefaultChatColors(color);
        } else {
            color = ChatColor.translateAlternateColorCodes('&', color);
        }
        if (plr != null && !plr.hasPermission("mtc.chatfarbe.use")) {
            return "§f";
        }
        color = MTCChatHelper.replaceSpecialWords(color);
        return color;
    }

    public static void sendClanSpyMsg(String msg, String clnName) {
        if (MTCChatHelper.spies.size() == 0) {
            return;
        }
        for (String plrName : MTCChatHelper.spies) {
            OfflinePlayer plr = Bukkit.getOfflinePlayer(plrName);
            if (!plr.isOnline()) {
                MTCChatHelper.spies.remove(plr.getName());
                continue;
//				MinoTopiaCore.instance().getConfig().set("spies", ChatHelper.spies);
            }
            ((Player) plr).sendMessage("§b[C-" + clnName + "]§7§o" + msg);
        }
    }

    public static void sendMessage(String msg, Player sender) {
        sendMessage(msg, sender, Bukkit.getOnlinePlayers());
    }

    private static void sendMessage(String msg, Player sender, Collection<? extends Player> receivers) {
        int i = 0;
        LOGGER.info(ChatColor.stripColor(msg));
        for (Player plr : receivers) {
            if (!PrivateChat.activeChats.containsKey(plr) && MTC.instance().getXLoginHook().isAuthenticated(sender)) {
                plr.sendMessage(msg);
                i++;
            }
        }
        if (i <= 1) {
            sender.sendMessage(MTC.chatPrefix + "Niemand hört dich :(");
        }
    }

    public static void sendMessageWorld(String msg, Player plr, World world) {
        sendMessage(msg, plr, world.getPlayers());
    }

    public static String sendPrivateChat(Player sender, String msg, String clanTag) {
        String senderName = sender.getName();
        String finalMsg = "§d[P]" + clanTag + "§7" + senderName + ":§f " + msg;
        PrivateChat pc = PrivateChat.getActiveChat(sender);
        LOGGER.info("[P-{}]{}: {}", pc.chatId, senderName, msg);
        if (pc.activeRecipients.isEmpty() || pc.activeRecipients.size() == 1) {
            return "Niemand hört dich. Spieler einladen? §a/chat add <Spieler>";
        }
        pc.sendMessage(finalMsg);
        MTCChatHelper.sendSpyMsg("§d[P-" + pc.chatId + "]§7" + senderName + ": §o" + msg);
        return null;
    }

    public static void sendSpyMsg(String msg) {
        if (MTCChatHelper.spies.size() == 0) {
            return;
        }
        for (String plrName : MTCChatHelper.spies) {
            OfflinePlayer plr = Bukkit.getOfflinePlayer(plrName); //REFACTOR
            if (!plr.isOnline()) {
                MTCChatHelper.spies.remove(plr.getName());
                continue;
            }
            ((Player) plr).sendMessage(msg);
        }
    }

    public static void logClanChatMessage(String message, String clanPrefix, String playerName) {
        LOGGER.info("clan {} - {}: {}",
                clanPrefix, playerName, ChatColor.stripColor(message));
    }

    public static void setChatColorByName(String plrName, String newChatColor) {
        SafeSql sql = MTC.instance().getSql();
        if (MTCChatHelper.hasChatColor(plrName)) {
            sql.safelyExecuteUpdate("UPDATE " + sql.dbName + ".mts_chatfarbe SET chatfarbe=? WHERE user_name=?", newChatColor, plrName);
        } else {
            sql.safelyExecuteUpdate("INSERT INTO " + sql.dbName + ".mts_chatfarbe SET user_name=?,chatfarbe=?", plrName, newChatColor);
        }
        OfflinePlayer plr = Bukkit.getOfflinePlayer(plrName);
        if (plr == null || !plr.isOnline()) {
            MTCChatHelper.cfCache.put(plrName, MTCChatHelper.parseChatColByName(plrName, newChatColor));
        } else {
            MTCChatHelper.cfCache.put(plrName, MTCChatHelper.parseChatColByCSenderAndPerms(plr.getPlayer(), newChatColor));
        }
    }

    public static void setChatColorByPlayer(Player plr, String newChatColor) {
        MTCChatHelper.setChatColorByName(plr.getName(), newChatColor);
    }
}
