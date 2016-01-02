/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.listener;

import io.github.xxyy.mtc.MTC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.projectiles.ProjectileSource;

/**
 * This class listens for players throwing exp bottles at nether portals and actively prevents that behaviour, since
 * it allows them to duplicate said exp.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 24/12/14
 */
public class NetherPortalExpDupeListener implements Listener {
    private final MTC plugin;

    public NetherPortalExpDupeListener(MTC plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityPortal(EntityPortalEvent evt) {
        if(evt.getEntityType() == EntityType.EXPERIENCE_ORB || evt.getEntityType() == EntityType.THROWN_EXP_BOTTLE) {
            evt.getEntity().remove();
            evt.setCancelled(true);
            if(evt.getEntity() instanceof Projectile) {
                sendBlockedMessage(((Projectile) evt.getEntity()).getShooter());
            }
        }
    }

    private void sendBlockedMessage(ProjectileSource receiver) {
        if(receiver instanceof CommandSender) {
            ((CommandSender) receiver).sendMessage(plugin.getChatPrefix() + " §cSorry, du kannst keine " +
                    "Erfahrungsfläschchen auf Portale werfen (Bugusing!)");
        }
    }
}
