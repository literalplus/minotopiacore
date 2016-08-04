/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.chat;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.xxyy.common.localisation.LangHelper;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.api.MTCPlugin;


public final class CommandChatClear implements CommandExecutor { //REFACTOR

    private final MTCPlugin plugin;

    public CommandChatClear(MTCPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkPermAndMsg(sender, "mtc.chatclear", label)) {
            return true;
        }
        for (Player plr : Bukkit.getOnlinePlayers()) {
            if (plr.hasPermission("mtc.chatclear.exempt")) {
                plr.sendMessage(String.format(LangHelper.localiseString("XU-ccex", plr.getName(), plugin.getName()), sender.getName()));
                continue;
            }
            for (int i = 0; i < 150; i++) {
                plr.sendMessage(" §r ");     //Apparently this helps with hack clients' message combining stuffs which are apparently 9001% exploits
                plr.sendMessage("    §3  "); //Even though one could just use the logs, but yeah, it looks professional or something. Yell at Janmm14.
            }
        }
        String msg = LangHelper.localiseString("XU-ccglo", "CONSOLE", plugin.getName());
        if (args.length >= 1) {
            msg += " §aGrund: §7";
            for (int i = 0; i < args.length; i++) {
                msg += (i == 0 ? "" : " ") + args[i];
            }
        }
        Bukkit.broadcastMessage(msg);
        return true;
    }

}
