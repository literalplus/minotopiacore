/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
