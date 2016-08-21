/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.chat;

import li.l1t.common.util.ChatHelper;
import li.l1t.common.util.CommandHelper;
import li.l1t.mtc.MTC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public final class CommandGlobalMute implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!CommandHelper.checkPermAndMsg(sender, "mtc.globalmute.toggle", label)) {
            return true;
        }
        ChatHelper.isGlobalMute = !ChatHelper.isGlobalMute;
        String msg = MTC.chatPrefix + "GlobalMute wurde " + ((ChatHelper.isGlobalMute) ? "" : "de") + "aktiviert!";
        String reason = "";
        if (args.length >= 1) {
            reason = " §aGrund: §7";
            for (int i = 0; i < args.length; i++) {
                reason += (i == 0 ? "" : " ") + args[i];
            }
        }
        ChatHelper.gloMuReason = reason;
        Bukkit.broadcastMessage(msg + reason);
        return true;
    }

}
