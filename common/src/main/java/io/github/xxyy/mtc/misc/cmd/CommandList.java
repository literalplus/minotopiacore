package io.github.xxyy.mtc.misc.cmd;

import io.github.xxyy.mtc.helper.MTCHelper;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public final class CommandList extends MTCCommandExecutor {
    
    @Override
    public boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
        return MTCHelper.sendLocArgs("XU-listformat", sender, true, Bukkit.getOnlinePlayers().length, Bukkit.getMaxPlayers());
    }
}