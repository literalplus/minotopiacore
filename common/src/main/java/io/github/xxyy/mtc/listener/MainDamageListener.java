/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.listener;

import org.bukkit.DyeColor;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import io.github.xxyy.mtc.ConfigHelper;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.clan.ClanHelper;
import io.github.xxyy.mtc.clan.ClanMemberInfo;
import io.github.xxyy.mtc.helper.MTCHelper;
import io.github.xxyy.mtc.misc.PeaceInfo;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;


public final class MainDamageListener implements Listener {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    static {
        MainDamageListener.DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_UP);
    }

    private final MTC plugin;

    public MainDamageListener(MTC plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    // event should be cancelled before by i.e. WorldGuard
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.isCancelled() || e.getEntityType() != EntityType.PLAYER) {
            return;
        }
        Player plrDamager;
        boolean message = true;
        Player plr = (Player) e.getEntity();

        if (plugin.getGameManager().isInGame(plr.getUniqueId())) {
            return;
        }

        plr.removePotionEffect(PotionEffectType.INVISIBILITY);
        switch (e.getDamager().getType()) {
            case PLAYER:
                plrDamager = (Player) e.getDamager();
                break;
            case ARROW:
            case SNOWBALL:
            case SPLASH_POTION:
                ProjectileSource source = ((Projectile) e.getDamager()).getShooter();
                if (source == null || !(source instanceof Player)) {
                    return;
                }
                plrDamager = (Player) source;
                break;
            case WOLF:
                Wolf wolf = (Wolf) e.getDamager();
                AnimalTamer tmr = wolf.getOwner();
                if (!(tmr instanceof Player)) {
                    return;
                }
                plrDamager = (Player) tmr;
                message = false;
                break;
            default:
                return;
        }
        // INVISIBILITY
        plrDamager.removePotionEffect(PotionEffectType.INVISIBILITY);

        // PEACE
        if (PeaceInfo.isInPeaceWith(plrDamager.getName(), plr.getName())) {// this happens if the players are in peace
            if (message) {
                MTCHelper.sendLocArgs("XU-peacehit2", plrDamager, true,
                        plr.getName(), MainDamageListener.DECIMAL_FORMAT.format(plr.getHealth() / 2.0F), "❤");
            }
            MainDamageListener.cancelAndStopWolves(e); // e.setCancelled(true);
            return;
        }

        // CLAN
        String clanPrefix = "";
        if (ConfigHelper.isClanEnabled()) {
            ClanMemberInfo cmiVictim = ClanHelper.getMemberInfoByPlayerName(plr.getName());
            ClanMemberInfo cmiDamager = ClanHelper.getMemberInfoByPlayerName(plrDamager.getName());
            if (cmiVictim.clanId > 0 && cmiDamager.clanId > 0 && (cmiVictim.clanId == cmiDamager.clanId)) {
                MainDamageListener.cancelAndStopWolves(e); // e.setCancelled(true);
                if (message) {
                    MTCHelper.sendLoc("XC-clanhit", plrDamager, true);
                }
                return;
            }
            clanPrefix = ClanHelper.getPrefix(plr.getName()) +
                    ((cmiVictim.clanId < 0) ? "" : ClanHelper.getStarsByRank(cmiVictim.getRank()));
        }

        //this happens if the players can hit each other
        plugin.getLogoutHandler().setFighting(plr, plrDamager, Calendar.getInstance()); //ANTILOGOUT
        MTCHelper.sendLocArgs("XU-hit", plrDamager, true, clanPrefix,
                plr.getName(), MainDamageListener.DECIMAL_FORMAT.format(plr.getHealth() / 2.0F), "❤");
    }

    private static void cancelAndStopWolves(EntityDamageByEntityEvent e) {
        e.setCancelled(true);
        if (e.getDamager().getType() == EntityType.WOLF) {
            Wolf wolf = (Wolf) e.getDamager();
            wolf.setCollarColor(DyeColor.ORANGE);
            AnimalTamer prevOwner = wolf.getOwner();
            wolf.setOwner((Player) e.getEntity());
            wolf.setOwner(prevOwner);
        }
    }
}
