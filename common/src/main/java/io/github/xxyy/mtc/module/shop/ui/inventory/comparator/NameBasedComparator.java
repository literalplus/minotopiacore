/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.inventory.comparator;

import io.github.xxyy.mtc.module.shop.ShopItem;

/**
 * Sorts shop items based on their display name.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-07-10
 */
public class NameBasedComparator extends AbstractShopItemComparator {
    public static NameBasedComparator ASCENDING = new NameBasedComparator(true);
    public static NameBasedComparator DESCENDING = new NameBasedComparator(false);

    private NameBasedComparator(boolean ascending) {
        super(ascending);
    }

    @Override
    public int compare(ShopItem o1, ShopItem o2) {
        return applyOrder(
                o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName())
        );
    }

    @Override
    public String getDisplayName() {
        return "Nach Name (alphabetisch)";
    }

    @Override
    public ShopItemComparator reversed() {
        return isAscending() ? DESCENDING : ASCENDING;
    }
}
