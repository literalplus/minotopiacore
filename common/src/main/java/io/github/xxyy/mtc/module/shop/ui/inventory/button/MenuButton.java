/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.inventory.button;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import io.github.xxyy.mtc.module.shop.ui.inventory.ShopMenu;

/**
 * Represents a button in an inventory menu, shown to the user as an item stack.
 *
 * @param <M> the type of menu this button may be used in
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-04-17
 */
public interface MenuButton<M extends ShopMenu> {
    /**
     * @param menu the menu requesting to display this button
     * @return the item stack representing this button, or null if this button is hidden
     */
    ItemStack getItemStack(M menu);

    /**
     * Handles a click on this button.
     *
     * @param evt  the Bukkit event that caused the click
     * @param menu the menu associated with the click
     */
    void handleMenuClick(InventoryClickEvent evt, M menu);
}
