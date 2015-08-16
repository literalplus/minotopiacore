/*
 * Copyright (c) 2013-2015.
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
import io.github.xxyy.mtc.MTC;


public final class CommandChatClear implements CommandExecutor { //REFACTOR

    private final MTC plugin;

    public CommandChatClear(MTC plugin) {
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
            for (int i = 0; i < 200; i++) {
                plr.sendMessage("  ");
            }
        }
        String msg = LangHelper.localiseString("XU-ccglo", "CONSOLE", plugin.getName());
        if (args.length >= 1) {
            msg += " §bGrund: §7";
            for (int i = 0; i < args.length; i++) {
                msg += (i == 0 ? "" : " ") + args[i];
            }
        }
        Bukkit.broadcastMessage(msg);
        return true;
    }

}
