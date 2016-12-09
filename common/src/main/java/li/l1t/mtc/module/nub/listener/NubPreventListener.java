/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub.listener;

import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.module.nub.api.ProtectionService;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;

/**
 * Listens for events and prevents N.u.b. protected players from dealing damage.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-09
 */
public class NubPreventListener implements Listener {
    private final ProtectionService protectionService;

    public NubPreventListener(ProtectionService protectionService) {
        this.protectionService = protectionService;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        notifyAndCancelIfProtectedPlayer(event.getDamager(), event);
    }

    private void notifyAndCancelIfProtectedPlayer(Object entity, Cancellable event) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (protectionService.hasProtection(player)) {
                event.setCancelled(true);
                MessageType.WARNING.sendTo(player, "Du kannst keine Spieler schlagen. §6/nub");
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();
        notifyAndCancelIfProtectedPlayer(shooter, event);
    }
}
