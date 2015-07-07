/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.listener;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
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

import io.github.xxyy.mtc.ConfigHelper;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.helper.MTCHelper;
import io.github.xxyy.mtc.misc.AntiLogoutHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AntiLogoutListener implements Listener, AntiLogoutHandler {
    public static final SimpleDateFormat SIMPLE_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private final MTC plugin;
    private final Map<UUID, Date> playersInAFight = new HashMap<>();

    public AntiLogoutListener(MTC plugin) {
        this.plugin = plugin;
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true) //kick event could be cancelled before, event priority monitor does not set ignoreCancelled to true
    public void onPlayerKick(PlayerKickEvent e) {
        playersInAFight.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent e) {//TODO gets called when player is kicked...
        punishIfInAnyFight(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTp(PlayerTeleportEvent e) {
        if (!isFighting(e.getPlayer().getUniqueId()) ||
                e.getCause() != TeleportCause.ENDER_PEARL) {
            return;
        }
        MTCHelper.sendLoc("XU-fighttp", e.getPlayer(), true);
        e.setCancelled(true);
    }

    @Override
    public boolean isFighting(UUID uuid) {
        Date fightStart = playersInAFight.get(uuid);
        if (fightStart == null) {
            return false;
        }
        if (fightStart.before(Calendar.getInstance().getTime())) {
            playersInAFight.remove(uuid);
            return false;
        }
        return true;
    }

    public boolean punishIfInAnyFight(Player plr) {
        if (!plugin.getWorldGuardHook().isPvP(plr.getLocation())) {
            return false;
        }
        if (isFighting(plr.getUniqueId())) {
            for (ItemStack stk : plr.getInventory()) {
                if (stk == null || stk.getType() == Material.AIR) {
                    continue;
                }
                plr.getWorld().dropItemNaturally(plr.getLocation(), stk);
            }
            for (ItemStack stk : plr.getInventory().getArmorContents()) {
                if (stk == null || stk.getType() == Material.AIR) {
                    continue;
                }
                plr.getWorld().dropItemNaturally(plr.getLocation(), stk);
            }
            plr.getInventory().clear();
            // plr.getInventory().setArmorContents(new ItemStack[4]);
            Bukkit.broadcastMessage(MTCHelper.locArgs("XU-fightlogout", plr.getName(), true, plr.getName()));
            if (MTC.isUseHologram()) {
                Hologram h = HologramsAPI.createHologram(plugin, plr.getLocation().add(0d, 1.5d, 0d));
                h.appendTextLine("§a" + plr.getName()); //filoghost pls into builder pattern ;-;
                h.appendTextLine("§cIm Kampf geloggt");
                h.appendTextLine(String.format("[%s]", SIMPLE_TIME_FORMAT.format(new Date())));

                plugin.getServer().getScheduler().runTaskLater(plugin, h::delete, ConfigHelper.getHologramTimeout() * 20);
            }
            return true;
        }
        return false;
    }

    @Override
    public void setFighting(final Player damaged, final Player damager, final Calendar cal) {
        cal.add(Calendar.SECOND, ConfigHelper.getSecsInFight());
        if (!damaged.hasPermission("mtc.ignore") && !damager.hasPermission("mtc.ignore")) {
            setFightingInternal(damaged, damager, cal.getTime());
            setFightingInternal(damager, damaged, cal.getTime());
        }
    }

    @Override
    public void clearFighters() {
        this.playersInAFight.clear();
    }

    private void setFightingInternal(final Player plr, final Player other, final Date dt) {
        if (!playersInAFight.containsKey(other.getUniqueId())) {
//            PluginAPIInterfacer.cancelAllEssTeleports(plr); //teleports already handled by #onTp(PlayerTeleportEvent)
            MTCHelper.sendLocArgs("XU-fightstart", plr, true, other.getName());
        }
        playersInAFight.put(plr.getUniqueId(), dt);
    }
}
