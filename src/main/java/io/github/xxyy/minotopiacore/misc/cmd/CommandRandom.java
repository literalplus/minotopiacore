package io.github.xxyy.minotopiacore.misc.cmd;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.MTCCommandExecutor;
import io.github.xxyy.minotopiacore.helper.MTCHelper;

import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import com.google.common.collect.Lists;

public class CommandRandom extends MTCCommandExecutor
{
    private class RunnableAnnounceChoice implements Runnable{
        private String plrName;
        
        RunnableAnnounceChoice(String plrName)
        {
            Bukkit.broadcastMessage(MTCHelper.locArgs("XU-randomplr", "CONSOLE", true, plrName));
            this.plrName = plrName;
        }
        
        @Override
        public void run()
        {
            Bukkit.broadcastMessage(MTCHelper.locArgs("XU-chosenone", "CONSOLE", true, this.plrName));
        }
        
    }
    
    @Override
    public boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args)
    {
        if(!CommandHelper.checkPermAndMsg(sender, "mtc.random", label)) return true;
        
        Player[] plrs = Bukkit.getOnlinePlayers();
        
        if(args.length > 0){
            switch(args[0].toLowerCase()){
            case "-p":
                if(args.length < 2) return MTCHelper.sendLoc("XU-rdmhelp", sender, false);
                Bukkit.getScheduler().runTaskLater(MTC.instance(), new RunnableAnnounceChoice(args[1]),
                        MTC.instance().getConfig().getLong("random.tickdelay", 100));
                return true;
            case "-m":
                if(args.length < 2) return MTCHelper.sendLoc("XU-rdmhelp", sender, false);
                List<Player> newPlrs = Lists.newArrayList();
                for(Player target : plrs){
                    if(target.hasPermission(args[1])){
                        newPlrs.add(target);
                    }
                }
                plrs = newPlrs.toArray(new Player[newPlrs.size()]);
                break;
            default:
                return MTCHelper.sendLoc("XU-rdmhelp", sender, false);
            }
        }
        
        if(plrs == null || plrs.length == 0) return MTCHelper.sendLoc("XU-nordmplrs", sender, true);
        Player chosenOne = plrs[RandomUtils.nextInt(plrs.length)];
        
        Bukkit.getScheduler().runTaskLater(MTC.instance(), new RunnableAnnounceChoice(chosenOne.getName()),
                MTC.instance().getConfig().getLong("random.tickdelay", 100));
        
        return true;
    }
}
