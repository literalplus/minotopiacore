/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.inventory.button;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import io.github.xxyy.common.util.inventory.ItemStackFactory;
import io.github.xxyy.mtc.module.shop.ui.inventory.ShopListMenu;
import io.github.xxyy.mtc.module.shop.ui.inventory.comparator.IdBasedComparator;
import io.github.xxyy.mtc.module.shop.ui.inventory.comparator.NameBasedComparator;
import io.github.xxyy.mtc.module.shop.ui.inventory.comparator.ShopItemComparator;

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
