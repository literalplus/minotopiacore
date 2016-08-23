/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.chat;

import li.l1t.mtc.MTC;
import li.l1t.mtc.logging.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Deprecated //stateful static class
public class MTCChatHelper {
    public static List<String> spies = new ArrayList<>(); // /mtc spy
    private static Logger LOGGER = LogManager.getLogger(MTCChatHelper.class);

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
            plr.sendMessage(msg);
            i++;
        }
        if (i <= 1) {
            sender.sendMessage(MTC.chatPrefix + "Niemand hört dich :(");
        }
    }

}
