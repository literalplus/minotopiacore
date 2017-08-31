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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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
