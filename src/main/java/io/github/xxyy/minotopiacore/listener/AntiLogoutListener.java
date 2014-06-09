package io.github.xxyy.minotopiacore.listener;

import io.github.xxyy.minotopiacore.ConfigHelper;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.helper.MTCHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AntiLogoutListener implements Listener, io.github.xxyy.minotopiacore.misc.AntiLogoutHandler {
    private final MTC plugin;
    private final HashMap<String, Date> playersInAFight = new HashMap<>();

    public AntiLogoutListener(MTC plugin) {
        this.plugin = plugin;
    }


    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent e){
        playersInAFight.remove(e.getPlayer().getName());
    }
    
    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent e){//TODO gets called when player is kicked...
        punishIfInAnyFight(e.getPlayer());
    }
    
    @EventHandler(priority=EventPriority.NORMAL)
    public void onTp(PlayerTeleportEvent e){
        if(!isFighting(e.getPlayer().getName()) ||
                e.getCause() != TeleportCause.ENDER_PEARL) return;
        MTCHelper.sendLoc("XU-fighttp", e.getPlayer(), true);
        e.setCancelled(true);
    }
    
    @Override
    public boolean isFighting(String plrName){
        Date fightStart = playersInAFight.get(plrName);
        if(fightStart == null) return false;
        if(fightStart.before(Calendar.getInstance().getTime())){
            playersInAFight.remove(plrName);
            return false;
        }
        return true;
    }
    
    public boolean punishIfInAnyFight(Player plr){
        if(!plugin.getWorldGuardHook().isPvP(plr.getLocation())) return false;
        if(isFighting(plr.getName())){
            for(ItemStack stk : plr.getInventory().getArmorContents()){
                if(stk == null || stk.getType() == Material.AIR)
                {
                    continue;
                }
                plr.getWorld().dropItemNaturally(plr.getLocation(), stk);
            }
            plr.getInventory().setArmorContents(new ItemStack[4]);
            Bukkit.broadcastMessage(MTCHelper.locArgs("XU-fightlogout", plr.getName(), true, plr.getName()));
            return true;
        }
        return false;
    }
    
    @Override
    public void setFighting(final Player plr, final Player other, final Calendar cal){
        cal.add(Calendar.SECOND, ConfigHelper.getSecsInFight());
        if(!plr.hasPermission("mtc.ignore"))
        {
            setFightingInternal(plr, other.getName(), cal.getTime());
        }
        if(!other.hasPermission("mtc.ignore"))
        {
            setFightingInternal(other, plr.getName(), cal.getTime());
        }
    }

    @Override
    public void clearFighters() {
        this.playersInAFight.clear();
    }
    
    private void setFightingInternal(final Player plr, final String otherName, final Date dt){
        final String plrName = plr.getName();
        if(!playersInAFight.containsKey(plrName)){
//            PluginAPIInterfacer.cancelAllEssTeleports(plr); //TODO why is this commented out? Should we readd this?
            MTCHelper.sendLocArgs("XU-fightstart", plr, true, otherName);
        }
        playersInAFight.put(plrName, dt);
    }
}
