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

package li.l1t.mtc.module.shop.ui.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * Listens for events related to shop inventory menus and forwards them to the corresponding
 * instance.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-04-17
 */
public class ShopMenuListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent evt) {
        if (evt.getClickedInventory() == null) {
            return;
        }

        InventoryHolder holder = evt.getClickedInventory().getHolder();
        if (holder != null && holder instanceof ShopMenu) {
            try {
                evt.setCancelled(((ShopMenu) holder).handleClick(evt));
            } catch (Exception e) {
                evt.setCancelled(true);
                e.printStackTrace();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClickMonitor(InventoryClickEvent evt) {
        if (evt.getClickedInventory() == null) {
            return;
        }

        InventoryHolder holder = evt.getClickedInventory().getHolder();
        if (holder != null && holder instanceof ShopMenu) {
            ((ShopMenu) holder).handleClickMonitor(evt);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClose(InventoryCloseEvent evt) {
        if (evt.getInventory() == null) {
            return;
        }

        InventoryHolder holder = evt.getInventory().getHolder();
        if (holder != null && holder instanceof ShopMenu) {
            ((ShopMenu) holder).handleClose(evt);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryHotbarSwap(InventoryClickEvent evt) {
        if (evt.getClickedInventory() == null) {
            return;
        }

        InventoryHolder holder = evt.getView().getTopInventory().getHolder();
        if (holder != null && holder instanceof ShopMenu) {
            switch (evt.getAction()) {
                case HOTBAR_MOVE_AND_READD:
                case HOTBAR_SWAP:
                case MOVE_TO_OTHER_INVENTORY:
                    evt.setCancelled(!((ShopMenu) holder).permitsHotbarSwap(evt));
            }
        }
    }
}
