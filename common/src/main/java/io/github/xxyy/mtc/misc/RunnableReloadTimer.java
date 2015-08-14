/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.misc;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.xxyy.mtc.LogHelper;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.misc.cmd.CommandBReload;

import java.util.Iterator;
import java.util.List;


public final class RunnableReloadTimer implements Runnable {

    public static List<Integer> seconds;
    public static Iterator<Integer> iSeconds;
    public static List<Long> delays;
    public static Iterator<Long> iDelays;
    public CommandSender sender = null;
    private final MTC plugin;

    public RunnableReloadTimer(CommandSender sender, MTC plugin) {
        this.sender = sender;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        int second = RunnableReloadTimer.iSeconds.next();
        if (second == 0) {
            try {
//                Bukkit.broadcastMessage(MTC.chatPrefix+"§dReload gestartet!");
                for (Player plr : Bukkit.getOnlinePlayers()) {
                    if (plr.getItemOnCursor() != null) {
                        LogHelper.getMainLogger().fine("ItemOnCursor @" + plr.getName() + ": " + plr.getItemOnCursor());
                        plr.setItemOnCursor(null);
                    }
                    plr.closeInventory();
                }
                Bukkit.reload();
                for (Player plr : Bukkit.getOnlinePlayers()) {
                    if (plr.getItemOnCursor() != null) {
                        LogHelper.getMainLogger().fine("ItemOnCursor @" + plr.getName() + ": " + plr.getItemOnCursor());
                        plr.setItemOnCursor(null);
                    }
                    plr.closeInventory();
                }
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
        for (Player plr : Bukkit.getOnlinePlayers()) {
            if (plr.getItemOnCursor() != null) {
                LogHelper.getMainLogger().fine("ItemOnCursor @" + plr.getName() + ": " + plr.getItemOnCursor());
                plr.setItemOnCursor(null);
            }
            plr.closeInventory();
        }
    }

    private static String getFormattedTime(int seconds) {
        if (seconds < 60) {
            return "§l" + seconds + " §dSekunde" + ((seconds == 1) ? "" : "n");
        }
        int minutes = (seconds / 60);
        seconds = seconds - (60 * minutes);
        return minutes + " Minute" + ((minutes == 1) ? "" : "n") + ((seconds == 0) ? "" : " und " + seconds + " Sekunde" + ((seconds == 1) ? "" : "n"));
    }
}
