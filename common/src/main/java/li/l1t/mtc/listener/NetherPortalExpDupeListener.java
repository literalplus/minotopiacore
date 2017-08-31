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

package li.l1t.mtc.listener;

import li.l1t.mtc.MTC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.projectiles.ProjectileSource;

/**
 * This class listens for players throwing exp bottles at nether portals and actively prevents that
 * behaviour, since it allows them to duplicate said exp.
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
        if (evt.getEntityType() == EntityType.EXPERIENCE_ORB || evt.getEntityType() == EntityType.THROWN_EXP_BOTTLE) {
            evt.getEntity().remove();
            evt.setCancelled(true);
            if (evt.getEntity() instanceof Projectile) {
                sendBlockedMessage(((Projectile) evt.getEntity()).getShooter());
            }
        }
    }

    private void sendBlockedMessage(ProjectileSource receiver) {
        if (receiver instanceof CommandSender) {
            ((CommandSender) receiver).sendMessage(plugin.getChatPrefix() + " §cSorry, du kannst keine " +
                    "Erfahrungsfläschchen auf Portale werfen (Bugusing!)");
        }
    }
}
