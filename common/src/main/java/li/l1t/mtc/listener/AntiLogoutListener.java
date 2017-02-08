/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.listener;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import li.l1t.mtc.ConfigHelper;
import li.l1t.mtc.MTC;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.helper.MTCHelper;
import li.l1t.mtc.misc.AntiLogoutHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AntiLogoutListener implements Listener, AntiLogoutHandler {
    public static final SimpleDateFormat SIMPLE_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private final MTC plugin;
    private final Map<UUID, Instant> playersInAFight = new HashMap<>();

    public AntiLogoutListener(MTC plugin) {
        this.plugin = plugin;
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    //kick event could be cancelled before, event priority monitor does not set ignoreCancelled to true
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
        Instant fightExpiry = playersInAFight.get(uuid);
        if (fightExpiry == null) {
            return false;
        }
        if (fightExpiry.isBefore(Instant.now())) {
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
            plr.getInventory().clear();
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
    public void setFighting(Player victim, Player culprit) {
        Instant fightExpiry = Instant.now().plusSeconds(ConfigHelper.getSecsInFight());
        if (!victim.hasPermission("mtc.ignore") && !culprit.hasPermission("mtc.ignore")) {
            setFightingUntil(victim, culprit, fightExpiry);
            setFightingUntil(culprit, victim, fightExpiry);
        }
    }

    @Override
    public void clearFighters() {
        this.playersInAFight.clear();
    }

    private void setFightingUntil(Player target, Player enemy, Instant fightExpiry) {
        if (!playersInAFight.containsKey(enemy.getUniqueId())) {
            MTCHelper.sendLocArgs("XU-fightstart", target, true, enemy.getName());
        }
        playersInAFight.put(target.getUniqueId(), fightExpiry);
        if (target.isFlying() && target.getGameMode() != GameMode.CREATIVE) {
            target.setFlying(false);
            target.setFlySpeed(1F);
            target.setAllowFlight(false);
            MessageType.WARNING.sendTo(target, "Dein Flugmodus wurde beendet, da du jetzt in einem Kampf bist.");
        }
    }
}
