package io.github.xxyy.minotopiacore.cron;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.LogHelper;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.fulltag.FullInfo;
import io.github.xxyy.minotopiacore.fulltag.FullTagHelper;
import io.github.xxyy.minotopiacore.helper.MTCHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class RunnableCheckInvsForFull implements Runnable {
    private int plrsDone = 0;
    private StopWatch watch = new StopWatch();
    private Iterator<Player> it = null;
    private List<Integer> fullsFound = new ArrayList<>();
    @Override
    public void run() {
        if(this.it == null) {
            this.it = Arrays.asList(Bukkit.getOnlinePlayers()).iterator();
        }
        if(this.plrsDone < 1){
            this.watch.start();
            CommandHelper.broadcast(MTC.chatPrefix+"Durchsuche alle Spieler nach bösen Fulls...", "mtc.saveallmsg");
            LogHelper.getFullLogger().warning("******CRONJOB******");
        }
        short i = 0;
        while(this.it.hasNext() && i <= 10){
            Player plr = this.it.next();
            PlayerInventory inv = plr.getInventory();
            Location loc = plr.getLocation();
            for(ItemStack is : plr.getInventory().getContents()){//comparing contains(Material) is *NOT* more efficient since it also just loops through
               if(is == null || is.getType().equals(Material.AIR)) {
                continue;
            }
                int id = FullTagHelper.getFullId(is);
                if(id < 0) {
                    continue;
                }
                if(is.getAmount() > 1){
                    is.setAmount(1);
                    inv.remove(is);
                    MTCHelper.addViolation("FULL-STACKED", plr.getName(),"CJ|"+MTCHelper.locToShortString(loc));
                    LogHelper.getFullLogger().warning("[CJ]Caught VIOLATION: FULL-STACKED @ "+plr.getName());
                    plr.sendMessage(MTC.chatPrefix+"§cEin Fullteil in deinem Inventar ist gestackt und wurde daher entfernt. §eFür Beschwerden notiere dir bitte unbedingt das aktuelle Datum und melde dich frühstmöglich!");
                }
                FullInfo fi = FullInfo.getById(id);
                if(fi.id == -10){
                    inv.remove(is);
                    MTCHelper.addViolation("FULL-UNKNOWN", plr.getName(),"CJ|"+MTCHelper.locToShortString(loc));
                    LogHelper.getFullLogger().warning("[CJ]Caught VIOLATION: FULL-UNKNOWN @ "+plr.getName());
                    plr.sendMessage(MTC.chatPrefix+"§cEin Fullteil in deinem Inventar ist unbekannt und wurde daher entfernt. §eFür Beschwerden notiere dir bitte unbedingt das aktuelle Datum und melde dich frühstmöglich!");
                    continue;
                }
                if(fi.id < 0){
                    System.out.println("CJ-FULL-ERR: "+fi.id); continue;
                }
                if(this.fullsFound.contains(fi.id)){
                    MTCHelper.addViolation("FULL-DUPE", plr.getName(),"CJ|"+MTCHelper.locToShortString(loc));
                    LogHelper.getFullLogger().warning("[CJ]Caught VIOLATION: FULL-DUPE @ "+plr.getName());
                    plr.sendMessage(MTC.chatPrefix+"§cEin Fullteil in deinem Inventar ist doppelt. Wir haben dir die Arbeit abgenommen und es entfernt. §eFür Beschwerden notiere dir bitte unbedingt das aktuelle Datum und melde dich frühstmöglich!");
                    continue;
                }
                this.fullsFound.add(fi.id);
                fi.lastseen = (Calendar.getInstance().getTimeInMillis() / 1000);
                fi.lastCode = "cronjob_loop";
                fi.inEnderchest = false;
                fi.x = loc.getBlockX();
                fi.y = loc.getBlockY();
                fi.z = loc.getBlockZ();
                fi.lastOwnerName = plr.getName();
                fi.flush();
                LogHelper.getFullLogger().fine("[CJ]Caught Full: "+fi.toLogString()+" at player: "+plr.getName());
            }
            LogHelper.getFullLogger().fine("[CJ]Finished player: "+plr.getName());
        }
        if(this.it.hasNext()){
            Bukkit.getScheduler().runTaskLater(MTC.instance(), this, 20);
            return;
        }
        this.watch.stop();
        LogHelper.getFullLogger().warning("***CRONJOB ENDED ("+this.watch.getTime()+"ms)***");
        CommandHelper.broadcast(MTC.chatPrefix+"Inventur fertig! ("+this.watch.getTime()+"ms)", "mtc.saveallmsg");
    }
    
}
