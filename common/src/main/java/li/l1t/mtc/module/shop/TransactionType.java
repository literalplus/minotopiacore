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

package li.l1t.mtc.module.shop;


import li.l1t.mtc.module.shop.api.ShopItem;
import li.l1t.mtc.module.shop.api.ShopItemManager;

/**
 * Represents a type of transaction a user can commence with the shop.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 28/10/15
 */
public enum TransactionType {
    SELL {
        @Override
        public double getValue(ShopItem item, ShopItemManager itemManager) {
            return itemManager.getSellWorth(item);
        }

        @Override
        public boolean isTradable(ShopItem item) {
            return item != null && item.canBeSold();
        }
    },
    BUY {
        @Override
        public double getValue(ShopItem item, ShopItemManager itemManager) {
            return itemManager.getBuyCost(item);
        }

        @Override
        public boolean isTradable(ShopItem item) {
            return item != null && item.canBeBought();
        }
    };

    /**
     * Calculates the current value of given item in respect to this transaction type.
     *
     * @param item        the item, may not be null
     * @param itemManager the manager to get the price from
     * @return the value of given item for this transaction type
     */
    public abstract double getValue(ShopItem item, ShopItemManager itemManager);

    /**
     * Checks whether given item is currently tradable for this transaction type. Null items are not
     * tradable.
     *
     * @param item the item, may be null
     * @return whether given item is tradable for this transaction type
     */
    public abstract boolean isTradable(ShopItem item);
}
