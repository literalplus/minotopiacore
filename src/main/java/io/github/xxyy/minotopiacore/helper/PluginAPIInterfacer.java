package io.github.xxyy.minotopiacore.helper;

import io.github.xxyy.common.util.CommandHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class PluginAPIInterfacer
{

    private PluginAPIInterfacer()
    {
    }
    private static Boolean isWorldGuardAvailable = null;//Using Boolean here to be able to use null to indicate
    private static Boolean isEssentialsAvailable = null;//availability hasn't been checked.
    
    public static void cancelAllEssTeleports(Player plr){
        if(!PluginAPIInterfacer.isEssAvailable()) return;
        final com.earth2me.essentials.IEssentials essPlug = (com.earth2me.essentials.IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");
        essPlug.getUser(plr).requestTeleport(null, false);//cancel plr's teleport requests.
        essPlug.getUser(plr).getTeleport().cancel(false);
    }
    
    public static boolean isEssAvailable()
    {
        if (PluginAPIInterfacer.isEssentialsAvailable == null)
        {
            try
            {
                Class.forName("com.earth2me.essentials.IEssentials");
                PluginAPIInterfacer.isEssentialsAvailable = true;
            } catch (ClassNotFoundException e)
            {
                CommandHelper.sendMessageToOpsAndConsole("§e[MTC][WARNING] Tried to query Essentials but it was not present.");
                PluginAPIInterfacer.isEssentialsAvailable = false;
            }
        }
        return PluginAPIInterfacer.isEssentialsAvailable;
    }
    
    public static boolean isPvPEnabledAt(Location loc)
    {
        if (!PluginAPIInterfacer.isWGAvailable()) return true;
        return com.sk89q.worldguard.bukkit.WGBukkit
                .getRegionManager(loc.getWorld()).getApplicableRegions(loc).allows(com.sk89q.worldguard.protection.flags.DefaultFlag.PVP);
    }
    
    public static boolean isWGAvailable()
    {
        if (PluginAPIInterfacer.isWorldGuardAvailable == null)
        {
            try
            {
                Class.forName("com.sk89q.worldguard.bukkit.WGBukkit");
                PluginAPIInterfacer.isWorldGuardAvailable = true;
            } catch (ClassNotFoundException e)
            {
                CommandHelper.sendMessageToOpsAndConsole("§e[MTC][WARNING] Tried to query WorldGuard but it was not present.");
                PluginAPIInterfacer.isWorldGuardAvailable = false;
            }
        }
        return PluginAPIInterfacer.isWorldGuardAvailable;
    }
}
