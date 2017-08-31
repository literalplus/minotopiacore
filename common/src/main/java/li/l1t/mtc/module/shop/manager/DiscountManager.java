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

package li.l1t.mtc.module.shop.manager;

import com.google.common.base.Preconditions;
import li.l1t.mtc.module.shop.api.ShopItem;
import li.l1t.mtc.module.shop.api.ShopItemManager;
import org.apache.commons.lang.math.RandomUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages discounts on shop items.
 *
 * @author <a href="https://l1t.li/">xxyy</a>
 * @since 2016-01-28
 */
public class DiscountManager {
    @Nullable
    private ShopItem discountedItem;

    /**
     * Checks whether an item is discounted currently.
     *
     * @param item the item to check
     * @return whether given item is currently discounted
     */
    public boolean isDiscounted(ShopItem item) {
        Preconditions.checkNotNull(item, "item");
        return item.equals(discountedItem);
    }

    /**
     * Gets the single currently discounted item, or null if no item is currently discounted.
     *
     * @return the discounted item, or null if none
     */
    @Nullable
    public ShopItem getDiscountedItem() {
        return discountedItem;
    }

    /**
     * @return whether there is currently a discounted item
     */
    public boolean hasDiscount() {
        return getDiscountedItem() != null;
    }

    /**
     * Selects a new discounted item. More specifically, semi-randomly selects a shop item from
     * given manager that can be discounted and sets it as the discounted item. If no items are
     * discountable, null is set as discounted item.
     *
     * @param shopItemManager the shop item manager providing the items to select from
     * @return the new discounted item
     */
    public ShopItem selectDiscountedItem(ShopItemManager shopItemManager) {
        List<ShopItem> discountableItems = shopItemManager.getItems().stream()
                .filter(ShopItem::isDiscountable)
                .collect(Collectors.toList());

        if (discountableItems.isEmpty()) {
            return null;
        }
        ShopItem item = discountableItems.get(RandomUtils.nextInt(discountableItems.size()));
        discountedItem = item;
        return item;
    }

    /**
     * Calculates an item's buy cost, taking discounts into account. To be more specific, this
     * returns the item's buy cost if it is not discounted and its discounted price if it is.
     *
     * @param item the item to calculate the buy cost for
     * @return the calculated buy cost
     */
    public double getBuyCost(ShopItem item) {
        Preconditions.checkNotNull(item, "item");
        return isDiscounted(item) ? item.getDiscountedPrice() : item.getBuyCost();
    }
}
