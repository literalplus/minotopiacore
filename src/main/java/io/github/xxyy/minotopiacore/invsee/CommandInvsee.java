package io.github.xxyy.minotopiacore.invsee;

import io.github.xxyy.minotopiacore.misc.cmd.MTCPlayerOnlyCommandExecutor;
import io.github.xxyy.minotopiacore.helper.MTCHelper;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;


public class CommandInvsee extends MTCPlayerOnlyCommandExecutor
{

    @Override
    public boolean catchCommand(Player plr, String plrName, Command cmd, String label, String[] args)
    {
        if(args.length == 0 || args[0].equalsIgnoreCase("help")) return MTCHelper.sendLoc("XU-ishelp", plr, true);
        return false;
    }
    
}
