/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.module.nub.api.ProtectionService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Listens for events and prevents N.u.b. protected players from taking damage through them.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-09
 */
public class NubProtectListener implements Listener {
    private static final Object DUMMY = new Object();
    private final Cache<UUID, Object> ownStatusCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.SECONDS).build();
    private final ProtectionService service;

    public NubProtectListener(ProtectionService service) {
        this.service = service;
    }

    @EventHandler(ignoreCancelled = true)
    public void onGeneralDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (service.hasProtection(player)) {
                event.setCancelled(true);
                showOwnProtectionStatus(player);
                attemptShowForeignProtectionStatus(event, player);
            }
        }
    }

    private void attemptShowForeignProtectionStatus(EntityDamageEvent event, Player victim) {
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent specificEvent = (EntityDamageByEntityEvent) event;
            if (specificEvent.getDamager() instanceof Player) {
                showForeignProtectionStatus(specificEvent.getDamager(), victim);
            }
        }
    }

    private void showForeignProtectionStatus(Entity damager, Player victim) {
        MessageType.WARNING.sendTo(damager,
                "%s ist durch N.u.b. geschützt. Du kannst diese Person nicht schlagen.",
                victim.getName());
    }

    private void showOwnProtectionStatus(Player player) {
        if (!ownStatusCache.asMap().containsKey(player.getUniqueId())) {
            service.showOwnProtectionStatusTo(player);
            ownStatusCache.put(player.getUniqueId(), DUMMY);
        }
    }
}
