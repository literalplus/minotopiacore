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
import li.l1t.mtc.module.shop.ui.inventory.comparator.IdBasedComparator;
import li.l1t.mtc.module.shop.ui.inventory.comparator.NameBasedComparator;
import li.l1t.mtc.module.shop.ui.inventory.comparator.ShopItemComparator;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * A button for changing how items in a list menu are sorted.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-07-10
 */
public class SortTypeButton implements MenuButton<ShopListMenu> {
    public static final SortTypeButton INSTANCE = new SortTypeButton();
    private final ItemStackFactory iconFactory = new ItemStackFactory(Material.SIGN)
            .displayName("§eSortierung:");
    private static final List<ShopItemComparator> ascComparators =
            Arrays.asList(IdBasedComparator.ASCENDING, NameBasedComparator.ASCENDING);
    private static final List<ShopItemComparator> descComparators =
            Arrays.asList(IdBasedComparator.DESCENDING, NameBasedComparator.DESCENDING);

    private SortTypeButton() {

    }

    @Override
    public ItemStack getItemStack(ShopListMenu menu) {
        return new ItemStackFactory(iconFactory.produce())
                .lore(menu.getItemComparator().getDisplayName())
                .produce();
    }

    @Override
    public void handleMenuClick(InventoryClickEvent evt, ShopListMenu menu) {
        ShopItemComparator comparator = menu.getItemComparator();
        List<ShopItemComparator> comparators =
                comparator.isAscending() ? ascComparators : descComparators;
        int index = comparators.indexOf(comparator) + 1; //handles -1
        if (index >= comparators.size()) {
            index = 0;
        }
        menu.setItemComparator(comparators.get(index));
    }
}
