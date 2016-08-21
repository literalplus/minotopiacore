/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.misc.cmd;

import li.l1t.mtc.helper.MTCHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public final class CommandList extends MTCCommandExecutor {

    @Override
    public boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
        return MTCHelper.sendLocArgs("XU-listformat", sender, true, Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
    }
}
