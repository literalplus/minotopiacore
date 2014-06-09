package io.github.xxyy.minotopiacore.listener;

import io.github.xxyy.minotopiacore.ConfigHelper;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.helper.MTCHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MagicSnowballHitListener implements Listener {
    protected class RunnableMagicSnowballTimeout implements Runnable {
        protected String plrName;

        protected RunnableMagicSnowballTimeout(String plrName) {
            MagicSnowballHitListener.this.deniedPlayers.add(plrName);
            this.plrName = plrName;
        }

        @Override
        public void run() {
            MagicSnowballHitListener.this.deniedPlayers.remove(this.plrName);
        }

    }

    protected List<String> deniedPlayers = new ArrayList<>();

    @EventHandler(priority = EventPriority.HIGH)
// this HAS to ignore if the damage was already cancelled by ClanDamage.. and MUST therefore be called afterwards...
    public void onDmg(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) return;
        if (!e.getEntityType().equals(EntityType.PLAYER) || !e.getDamager().getType().equals(EntityType.SNOWBALL))
            return;
        Player plr = (Player) e.getEntity();
        Snowball ball = (Snowball) e.getDamager();
        ProjectileSource source = ball.getShooter();//le = LivingEntity
        if (!(source instanceof Player)) return; //Snow golems
        Player plrShooter = (Player) source;
        Location hitLoc = plr.getLocation();
        e.setDamage(0);

        if (!plrShooter.hasPermission("mtc.magicsnowball.hit")) return;

        if (this.deniedPlayers.contains(plrShooter.getName())) {
            MTCHelper.sendLoc("XU-snowballwait", plrShooter, true);
            return;
        }
        plr.removePotionEffect(PotionEffectType.INVISIBILITY);
        if (!plrShooter.hasPermission("mtc.magicsnowball.always") && plr.hasPermission("mtc.magicsnowball.chance")) {
            int rand = (new Random()).nextInt(4);
            if (rand == 0) {
                MTCHelper.sendLocArgs("XU-snowballchance", plrShooter, true, plr.getName());
                return;
            }
        }

        plr.addPotionEffects(ConfigHelper.getSnowballEffects());
        plr.playSound(hitLoc, Sound.WITHER_SPAWN, 1.0F, 2.0F);
        hitLoc.getWorld().createExplosion(hitLoc.getX(), hitLoc.getY(), hitLoc.getZ(), 1.0F, false, false);

        MTCHelper.sendLocArgs("XU-snowballtarget", plr, true, plrShooter.getName());
        Bukkit.getScheduler().runTaskLater(MTC.instance(), new RunnableMagicSnowballTimeout(plrShooter.getName()), ConfigHelper.getSnowballTimeoutTicks());
    }
}
