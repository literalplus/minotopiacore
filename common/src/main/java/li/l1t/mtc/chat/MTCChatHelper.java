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
