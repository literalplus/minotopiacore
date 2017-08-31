/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
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

package li.l1t.mtc.module.nub.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import li.l1t.mtc.api.PlayerGameManager;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.nub.api.ProtectionService;
import li.l1t.mtc.module.nub.service.SimpleProtectionService;
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
    private final PlayerGameManager gameManager;

    @InjectMe
    public NubProtectListener(SimpleProtectionService service, PlayerGameManager gameManager) {
        this.service = service;
        this.gameManager = gameManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onGeneralDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (service.hasProtection(player) && !gameManager.isInGame(player.getUniqueId())) {
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
