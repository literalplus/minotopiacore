package io.github.xxyy.minotopiacore.listener;

import io.github.xxyy.minotopiacore.ConfigHelper;
import io.github.xxyy.minotopiacore.helper.MTCHelper;
import io.github.xxyy.minotopiacore.helper.PluginAPIInterfacer;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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


public class AntiLogoutListener implements Listener
{
    
    public static final HashMap<String, Date> playersInAFight = new HashMap<>();
//    public static final List<String>
    
    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent e){
        AntiLogoutListener.playersInAFight.remove(e.getPlayer().getName());
    }
    
    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent e){//TODO gets called when player is kicked...
        AntiLogoutListener.punishIfInAnyFight(e.getPlayer());
    }
    
    @EventHandler(priority=EventPriority.NORMAL)
    public void onTp(PlayerTeleportEvent e){
        if(!AntiLogoutListener.isInAnyFight(e.getPlayer().getName()) ||
                e.getCause() != TeleportCause.ENDER_PEARL) return;
        MTCHelper.sendLoc("XU-fighttp", e.getPlayer(), true);
        e.setCancelled(true);
    }
    
    public static boolean isInAnyFight(String plrName){
        Date fightStart = AntiLogoutListener.playersInAFight.get(plrName);
        if(fightStart == null) return false;
        if(fightStart.before(Calendar.getInstance().getTime())){
            AntiLogoutListener.playersInAFight.remove(plrName);
            return false;
        }
        return true;
    }
    
    public static boolean punishIfInAnyFight(Player plr){
        if(!PluginAPIInterfacer.isPvPEnabledAt(plr.getLocation())) return false;
        if(AntiLogoutListener.isInAnyFight(plr.getName())){
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
    
    public static void setFighting(final Player plr, final Player other, final Calendar cal){
        cal.add(Calendar.SECOND, ConfigHelper.getSecsInFight());
        if(!plr.hasPermission("mtc.ignore"))
        {
            AntiLogoutListener.setFightingInternal(plr, other.getName(), cal.getTime());
        }
        if(!other.hasPermission("mtc.ignore"))
        {
            AntiLogoutListener.setFightingInternal(other, plr.getName(), cal.getTime());
        }
    }
    
    private static void setFightingInternal(final Player plr, final String otherName, final Date dt){
        final String plrName = plr.getName();
        if(!AntiLogoutListener.playersInAFight.containsKey(plrName)){
//            PluginAPIInterfacer.cancelAllEssTeleports(plr);
            MTCHelper.sendLocArgs("XU-fightstart", plr, true, otherName);
        }
        AntiLogoutListener.playersInAFight.put(plrName, dt);
    }
}
