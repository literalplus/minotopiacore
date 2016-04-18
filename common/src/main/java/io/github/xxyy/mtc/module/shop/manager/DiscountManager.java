/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.manager;

import com.google.common.base.Preconditions;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.api.ShopItemManager;
import org.apache.commons.lang.math.RandomUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages discounts on shop items.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
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
     * Selects a new discounted item. More specifically, semi-randomly selects a shop item from given manager that can
     * be discounted and sets it as the discounted item. If no items are discountable, null is set as discounted item.
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
        return discountableItems.get(RandomUtils.nextInt(discountableItems.size()));
    }

    /**
     * Calculates an item's buy cost, taking discounts into account. To be more specific, this returns the item's buy
     * cost if it is not discounted and its discounted price if it is.
     *
     * @param item the item to calculate the buy cost for
     * @return the calculated buy cost
     */
    public double getBuyCost(ShopItem item) {
        Preconditions.checkNotNull(item, "item");
        return isDiscounted(item) ? item.getDiscountedPrice() : item.getBuyCost();
    }
}
