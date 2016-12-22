/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.shop.ui.inventory;

import li.l1t.common.util.inventory.ItemStackFactory;
import li.l1t.mtc.module.shop.api.ShopItem;
import li.l1t.mtc.module.shop.api.ShopItemManager;
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

    public static ItemStack createInfoStack(ShopItem item, ShopItemManager itemManager) {
        ItemStackFactory factory = new ItemStackFactory(item.toItemStack(1))
                .displayName("§lInfo: §f" + item.getDisplayName());
        appendBuyCostToLore(item, itemManager, factory);
        appendSellWorthToLore(item, factory);
        return factory.produce();
    }

    private static void appendBuyCostToLore(ShopItem item, ShopItemManager itemManager, ItemStackFactory factory) {
        if (item.canBeBought()) {
            String lore = "§eStückpreis: §7";
            if (itemManager.getDiscountManager().isDiscounted(item)) {
                lore += "§m" + item.getBuyCost() + "§6 ";
            }
            factory.lore(lore + itemManager.getBuyCost(item));
        } else {
            factory.lore("§cKann nicht gekauft werden");
        }
    }

    private static void appendSellWorthToLore(ShopItem item, ItemStackFactory factory) {
        if (item.canBeSold()) {
            factory.lore("§eVerkaufspreis: §7" + item.getSellWorth());
        } else {
            factory.lore("§cKann nicht verkauft werden");
        }
    }
}
