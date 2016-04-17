/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.inventory.button;

import io.github.xxyy.common.util.inventory.ItemStackFactory;
import io.github.xxyy.mtc.module.shop.ui.inventory.ShopListMenu;
import io.github.xxyy.mtc.module.shop.ui.inventory.ShopMenu;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A singleton button directing players back to a {@link io.github.xxyy.mtc.module.shop.ui.inventory.ShopListMenu} on
 * click, represented by a special player head with a left arrow.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-04-17
 */
public class BackToListButton implements MenuButton {
    public static final BackToListButton INSTANCE = new BackToListButton();

    private BackToListButton() {

    }

    @Override
    public ItemStack getItemStack(ShopMenu menu) {
        return new ItemStackFactory(Material.SKULL_ITEM)
                .skullOwner("MHF_ArrowLeft")
                .displayName("§b<< Zurück zum Hauptmenü")
                .produce();
    }

    @Override
    public void handleMenuClick(InventoryClickEvent evt, ShopMenu menu) {
        ShopListMenu.openMenu(menu.getPlayer(), menu.getModule());
    }
}
