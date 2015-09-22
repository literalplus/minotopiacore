package io.github.xxyy.mtc.module.bounty;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class DeathListener implements Listener {

    @NotNull
    private final BountyModule module;

    public DeathListener(@NotNull BountyModule module) {
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) { //look up killer by last damage event if not got yet
            EntityDamageEvent causeEDE = victim.getLastDamageCause();
            if (!(causeEDE instanceof EntityDamageByEntityEvent)) {
                return;
            }
            EntityDamageByEntityEvent cause = (EntityDamageByEntityEvent) causeEDE;
            killer = determineDamagerPlayer(cause);

        }
        if (killer == null) { //no player killer could be found, aborting
            return;
        }
    }

    @Nullable
    private Player determineDamagerPlayer(@NotNull EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        if (damager instanceof Player) {
            return (Player) damager;
        } else if (damager instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) damager).getShooter();
            if (!(shooter instanceof Player)) {
                return null;
            }
            return (Player) shooter;
        } else {
            return null;
        }
    }
}
