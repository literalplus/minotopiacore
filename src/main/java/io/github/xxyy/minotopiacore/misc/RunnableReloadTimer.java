package io.github.xxyy.minotopiacore.misc;

import io.github.xxyy.minotopiacore.LogHelper;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.misc.cmd.CommandBReload;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;



public class RunnableReloadTimer implements Runnable {
    
    public static List<Integer> seconds;
    public static Iterator<Integer> iSeconds;
    public static List<Long> delays;
    public static Iterator<Long> iDelays;
    public CommandSender sender = null;
    
    public RunnableReloadTimer(CommandSender sender){
        this.sender=sender;
    }
    
    @Override
    public void run() {
        int second = RunnableReloadTimer.iSeconds.next();
        if(second == 0){
            try {
//                Bukkit.broadcastMessage(MTC.chatPrefix+"§dReload gestartet!");
                for(Player plr : Bukkit.getOnlinePlayers()){
                    if(plr.getItemOnCursor() != null){
                        LogHelper.getMainLogger().fine("ItemOnCursor @"+plr.getName()+": "+plr.getItemOnCursor());
                        plr.setItemOnCursor(null);
                    }
                    plr.closeInventory();
                }
                Bukkit.reload();
                for(Player plr : Bukkit.getOnlinePlayers()){
                    if(plr.getItemOnCursor() != null){
                        LogHelper.getMainLogger().fine("ItemOnCursor @"+plr.getName()+": "+plr.getItemOnCursor());
                        plr.setItemOnCursor(null);
                    }
                    plr.closeInventory();
                }
                Command.broadcastCommandMessage((this.sender == null) ? Bukkit.getConsoleSender() : this.sender, "§a§oReload complete.§7§o");
                Bukkit.broadcastMessage(MTC.chatPrefix+"§aReload erfolgreich.");
            } catch (Exception e) {
                e.printStackTrace();
                Command.broadcastCommandMessage((this.sender == null) ? Bukkit.getConsoleSender() : this.sender, "§4§o[SEVERE] RELOAD EXCEPTION: "+e.getClass().getName()+"§7§o");
                Bukkit.broadcastMessage(MTC.chatPrefix+"§4Es ist ein Fehler aufgetreten!");
            }
            return;
        }
        Bukkit.broadcastMessage(MTC.chatPrefix+"§dReload in "+RunnableReloadTimer.getFormattedTime(second)+"§d!");
        if(RunnableReloadTimer.iDelays.hasNext()){
            long delay = RunnableReloadTimer.iDelays.next();
            CommandBReload.taskId = Bukkit.getScheduler().runTaskLater(MTC.instance(), this, delay).getTaskId();
        }
        for(Player plr : Bukkit.getOnlinePlayers()){
            if(plr.getItemOnCursor() != null){
                LogHelper.getMainLogger().fine("ItemOnCursor @"+plr.getName()+": "+plr.getItemOnCursor());
                plr.setItemOnCursor(null);
            }
            plr.closeInventory();
        }
    }
    
    private static String getFormattedTime(int seconds){
        if(seconds < 60) {
            return "§l" + seconds + " §dSekunde" + ((seconds == 1) ? "" : "n");
        }
        int minutes = (seconds / 60);
        seconds = seconds - (60 * minutes);
        return minutes+" Minute"+((minutes == 1) ? "" : "n")+((seconds == 0) ? "" : " und "+seconds+" Sekunde"+((seconds == 1) ? "" : "n"));
    }
}
