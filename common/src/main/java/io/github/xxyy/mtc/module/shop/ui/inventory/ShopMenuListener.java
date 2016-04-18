/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * Listens for events related to shop inventory menus and forwards them to the corresponding instance.
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
            evt.setCancelled(((ShopMenu) holder).handleClick(evt));
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
