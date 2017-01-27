/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub.listener;

import li.l1t.mtc.api.PlayerGameManager;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.nub.api.ProtectionService;
import li.l1t.mtc.module.nub.service.SimpleProtectionService;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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
    private final PlayerGameManager gameManager;

    @InjectMe
    public NubPreventListener(SimpleProtectionService protectionService, PlayerGameManager gameManager) {
        this.protectionService = protectionService;
        this.gameManager = gameManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player playerDamager = (Player) event.getDamager();
            if (isProtectedAndNotInGame(playerDamager) && isDamageToAnotherPlayer(event)) {
                event.setCancelled(true);
                MessageType.WARNING.sendTo(playerDamager, "Du kannst keine Spieler schlagen. §6/nub");
            }
        }
    }

    private boolean isProtectedAndNotInGame(Player playerDamager) {
        return protectionService.hasProtection(playerDamager) && !gameManager.isInGame(playerDamager.getUniqueId());
    }

    private boolean isDamageToAnotherPlayer(EntityDamageByEntityEvent event) {
        return event.getEntityType() == EntityType.PLAYER;
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();
        if (shooter instanceof Player) {
            Player playerShooter = (Player) shooter;
            if (isProtectedAndNotInGame(playerShooter)) {
                event.setCancelled(true);
                MessageType.WARNING.sendTo(playerShooter, "Du kannst keine Projektile abfeuern. §6/nub");
            }
        }
    }
}
