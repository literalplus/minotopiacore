/*
 * Copyright (c) 2013-2014.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.misc.cmd;

import io.github.xxyy.common.util.CommandHelper;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public abstract class MTCPlayerOnlyCommandExecutor extends MTCCommandExecutor
{
    /**
     * DO NOT OVERRIDE
     */
    @Override
    public final boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args)
    {
        if(CommandHelper.kickConsoleFromMethod(sender, label)) {
            return true;
        }
        Player plr = (Player)sender;
        return this.catchCommand(plr, senderName, cmd, label, args);
    }
    
    /**
     * Override this to catch a command that can only be executed by players.
     * @param plr Player who issued the command
     * @param plrName Name of <code>plr</code>
     * @param cmd Command
     * @param label Alias used
     * @param args Arguments passed
     * @return return false to pretend this command is not loaded. This is not intended in most cases, so return true.
     */
    public abstract boolean catchCommand(Player plr, String plrName, Command cmd, String label, String[] args);
    
}
