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

package li.l1t.mtc.module.truefalse;

import li.l1t.common.misc.XyLocation;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Listens for events related to the TrueFalse game.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-12-07
 */
class TrueFalseWandListener implements Listener {
    private TrueFalseModule module;

    public TrueFalseWandListener(TrueFalseModule module) {
        this.module = module;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent evt) {
        ItemStack item = evt.getPlayer().getItemInHand();
        if (!module.hasBoundarySession(evt.getPlayer().getUniqueId()) ||
                (evt.getAction() != Action.RIGHT_CLICK_BLOCK && evt.getAction() != Action.LEFT_CLICK_BLOCK) ||
                item == null || item.getType() != TrueFalseModule.MAGIC_WAND_MATERIAL) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (item.hasItemMeta() && meta.hasDisplayName() &&
                meta.getDisplayName().equals(TrueFalseModule.MAGIC_WAND_NAME)) {
            if (evt.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                module.setSecondBoundary(new XyLocation(evt.getClickedBlock().getLocation()));
                evt.getPlayer().sendMessage("§aZweiter Eckpunkt gesetzt!");
                evt.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                module.endBoundarySession(evt.getPlayer().getUniqueId());
            } else {
                module.setFirstBoundary(new XyLocation(evt.getClickedBlock().getLocation()));
                evt.getPlayer().sendMessage("§aErster Eckpunkt gesetzt!");
            }

            evt.setCancelled(true);
        }
    }
}
