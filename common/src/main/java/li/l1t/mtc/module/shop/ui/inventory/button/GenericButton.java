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

package li.l1t.mtc.module.shop.ui.inventory.button;

import li.l1t.mtc.module.shop.ui.inventory.ShopMenu;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A generic button implementation with behaviour and icon defined at construction time.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-04-17
 */
public class GenericButton implements MenuButton<ShopMenu> {
    private final ItemStack itemStack;
    private final ClickHandler clickHandler;

    /**
     * Constructs a new generic button with a material and a click handler. An item stack with
     * amount 1 will be created as icon.
     *
     * @param material     the material of the icon stack
     * @param clickHandler the click handler, or null for no click action
     */
    public GenericButton(Material material, ClickHandler clickHandler) {
        this(new ItemStack(material), clickHandler);
    }

    /**
     * Constructs a new generic button with an icon stack and a click handler.
     *
     * @param itemStack    the icon stack
     * @param clickHandler the click handler, or null for no click action
     */
    public GenericButton(ItemStack itemStack, ClickHandler clickHandler) {
        this.itemStack = itemStack;
        this.clickHandler = clickHandler;
    }

    @Override
    public ItemStack getItemStack(ShopMenu menu) {
        return itemStack;
    }

    @Override
    public void handleMenuClick(InventoryClickEvent evt, ShopMenu menu) {
        if (clickHandler != null) {
            clickHandler.handleMenuClick(evt, menu);
        }
    }

    /**
     * Interface for a generic click handler handling generic click events on a generic {@link
     * GenericButton}.
     */
    public interface ClickHandler {
        /**
         * Handles a click on a button.
         *
         * @param evt  the Bukkit event that caused the click
         * @param menu the menu associated with the click
         */
        void handleMenuClick(InventoryClickEvent evt, ShopMenu menu);
    }
}
