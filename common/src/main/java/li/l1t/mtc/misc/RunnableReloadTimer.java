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

package li.l1t.mtc.misc;

import li.l1t.mtc.MTC;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.misc.cmd.CommandBReload;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;


public final class RunnableReloadTimer implements Runnable {

    public static List<Integer> seconds;
    public static Iterator<Integer> iSeconds;
    public static List<Long> delays;
    public static Iterator<Long> iDelays;
    private final MTCPlugin plugin;
    public CommandSender sender = null;

    public RunnableReloadTimer(CommandSender sender, MTCPlugin plugin) {
        this.sender = sender;
        this.plugin = plugin;
    }

    private static String getFormattedTime(int seconds) {
        if (seconds < 60) {
            return "§l" + seconds + " §dSekunde" + ((seconds == 1) ? "" : "n");
        }
        int minutes = (seconds / 60);
        seconds = seconds - (60 * minutes);
        return minutes + " Minute" + ((minutes == 1) ? "" : "n") + ((seconds == 0) ? "" : " und " + seconds + " Sekunde" + ((seconds == 1) ? "" : "n"));
    }

    @Override
    public void run() {
        int second = RunnableReloadTimer.iSeconds.next();
        if (second == 0) {
            try {
//                Bukkit.broadcastMessage(MTC.chatPrefix+"§dReload gestartet!");
                Bukkit.reload();
                Bukkit.getOnlinePlayers().forEach(Player::closeInventory);
                Command.broadcastCommandMessage((this.sender == null) ? Bukkit.getConsoleSender() : this.sender, "§a§oReload complete.§7§o");
                Bukkit.broadcastMessage(MTC.chatPrefix + "§aReload erfolgreich.");
            } catch (Exception e) {
                e.printStackTrace();
                Command.broadcastCommandMessage((this.sender == null) ? Bukkit.getConsoleSender() : this.sender, "§4§o[SEVERE] RELOAD EXCEPTION: " + e.getClass().getName() + "§7§o");
                Bukkit.broadcastMessage(MTC.chatPrefix + "§4Es ist ein Fehler aufgetreten!");
            }
            return;
        }
        Bukkit.broadcastMessage(MTC.chatPrefix + "§dReload in " + RunnableReloadTimer.getFormattedTime(second) + "§d!");
        if (RunnableReloadTimer.iDelays.hasNext()) {
            long delay = RunnableReloadTimer.iDelays.next();
            CommandBReload.taskId = Bukkit.getScheduler().runTaskLater(plugin, this, delay).getTaskId();
        }
        Bukkit.getOnlinePlayers().forEach(Player::closeInventory);
    }
}
