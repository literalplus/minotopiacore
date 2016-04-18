/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.inventory;

import io.github.xxyy.common.util.inventory.ItemStackFactory;
import io.github.xxyy.mtc.module.shop.ShopItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Static helper class to provide generic utility methods for the Shop inventory interface.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-04-17
 */
public class ShopInventoryHelper {
    private ShopInventoryHelper() {

    }

    public static ItemStack createInfoStack(ShopItem item, boolean realItem) {
        ItemStackFactory factory;
        if (realItem) {
            factory = new ItemStackFactory(item.toItemStack(1));
        } else {
            factory = new ItemStackFactory(Material.SKULL_ITEM)
                    .skullOwner("MHF_Exclamation")
                    .displayName("§lInfo: §f" + item.getDisplayName());
        }

        if (item.canBeBought()) {
            factory.lore("§eStückpreis: §7" + item.getBuyCost());
        } else {
            factory.lore("§cKann nicht verkauft werden");
        }

        if (item.canBeSold()) {
            factory.lore("§eVerkaufspreis: §7" + item.getSellWorth());
        } else {
            factory.lore("§cKann nicht gekauft werden");
        }

        return factory.produce();
    }
}
