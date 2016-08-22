/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.gettime;

import li.l1t.common.util.CommandHelper;
import li.l1t.mtc.helper.MTCHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public final class CommandTime implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!MTCHelper.isEnabledAndMsg(".command.gtime", sender)) {
            return true;
        }
        if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.gtime", label)) {
            return true;
        }
        if (args.length != 0) {
            sender.sendMessage("§7[MTS][Info]Dieser Befehl ben§tigt keine Argumente!");
        }
        sender.sendMessage(ChatColor.GOLD + "============={§7Serverzeit" + ChatColor.RESET + ChatColor.GOLD + "}=============");
        if (CommandHelper.checkActionPermAndMsg(sender, "mtc.cmd.gtime.time", "Die RL-Serverzeit anzeigen")) {
            sender.sendMessage(ChatColor.GOLD + "Aktuelle RL-Serverzeit: " + ChatColor.BLUE + (new SimpleDateFormat("HH:mm:ss")).format(Calendar.getInstance().getTime()));
        }
        if (!"gd".equalsIgnoreCase(label) && !"getdate".equalsIgnoreCase(label) && CommandHelper.checkActionPermAndMsg(sender, "mtc.cmd.gtime.date", "Das Serverdatum anzeigen")) {
            sender.sendMessage(ChatColor.GOLD + "Aktuelles Serverdatum: " + ChatColor.BLUE + (new SimpleDateFormat("dd.MM.yyyy")).format(Calendar.getInstance().getTime()));
        }
        sender.sendMessage(ChatColor.GOLD + "============={§7Serverzeit" + ChatColor.RESET + ChatColor.GOLD + "}=============");
        return true;
    }

}