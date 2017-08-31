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

import li.l1t.common.localisation.LangHelper;
import li.l1t.mtc.MTC;
import li.l1t.mtc.api.MTCPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;
import java.util.Set;

public final class EnderPearlProjectileLaunchListener implements Listener {

    private final MTCPlugin plugin;

    public EnderPearlProjectileLaunchListener(MTCPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileLaunchEP(ProjectileLaunchEvent e) {
        if (e.getEntity().getType() != EntityType.ENDER_PEARL) {
            return;
        }
        ProjectileSource projectileSource = e.getEntity().getShooter();
        if (!(projectileSource instanceof Player)) {
            return;
        }

        Player shooter = (Player) projectileSource;

        if (!shooter.hasPermission("mtc.enderpearl.use")) {
            e.setCancelled(true);
            shooter.sendMessage(MTC.chatPrefix + LangHelper.localiseString("XU-epcancelled", shooter.getName(), plugin.getName()));
            return;
        }
        List<Block> lineOfSight = shooter.getLineOfSight((Set<Material>) null, 100);
        for (Block lineOfSightItem : lineOfSight) {
            if (lineOfSightItem.getType() == Material.BEDROCK) {
                e.setCancelled(true);
                shooter.sendMessage(MTC.chatPrefix + LangHelper.localiseString("XU-epbedrock", shooter.getName(), plugin.getName()));
                return;
            }
        }
    }
}
