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
