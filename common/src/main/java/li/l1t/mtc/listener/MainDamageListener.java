/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2018 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package li.l1t.mtc.listener;

import li.l1t.mtc.ConfigHelper;
import li.l1t.mtc.MTC;
import li.l1t.mtc.clan.ClanHelper;
import li.l1t.mtc.clan.ClanMemberInfo;
import li.l1t.mtc.helper.MTCHelper;
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

import java.math.RoundingMode;
import java.text.DecimalFormat;


public final class MainDamageListener implements Listener {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    static {
        MainDamageListener.DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_UP);
    }

    private final MTC plugin;

    public MainDamageListener(MTC plugin) {
        this.plugin = plugin;
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    // event should be cancelled before by i.e. WorldGuard
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) {
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
        plugin.getLogoutHandler().setFighting(plr, plrDamager); //ANTILOGOUT
        MTCHelper.sendLocArgs("XU-hit", plrDamager, true, clanPrefix,
                plr.getName(), MainDamageListener.DECIMAL_FORMAT.format(plr.getHealth() / 2.0F), "❤");
    }
}
