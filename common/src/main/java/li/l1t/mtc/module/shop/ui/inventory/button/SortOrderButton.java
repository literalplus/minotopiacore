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

import li.l1t.common.util.inventory.ItemStackFactory;
import li.l1t.mtc.module.shop.ui.inventory.ShopListMenu;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a button that changes the order items are sorted in the shop list menu - ascending or
 * descending.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-07-10
 */
public class SortOrderButton implements MenuButton<ShopListMenu> {
    public static final SortOrderButton INSTANCE = new SortOrderButton();
    private final ItemStackFactory iconFactory = new ItemStackFactory(Material.PAINTING)
            .displayName("§eSortierrichtung:");

    private SortOrderButton() {

    }

    @Override
    public ItemStack getItemStack(ShopListMenu menu) {
        return new ItemStackFactory(iconFactory.produce())
                .lore(menu.getItemComparator().isAscending() ? "aufsteigend" : "absteigend")
                .produce();
    }

    @Override
    public void handleMenuClick(InventoryClickEvent evt, ShopListMenu menu) {
        menu.setItemComparator(menu.getItemComparator().reversed());
    }
}
