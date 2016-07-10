/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.inventory.comparator;

import io.github.xxyy.mtc.module.shop.ShopItem;

import java.util.Comparator;

/**
 * Compares shop items.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-07-10
 */
public interface ShopItemComparator extends Comparator<ShopItem> {
    /**
     * @return a human-readable name for this comparator
     */
    String getDisplayName();

    /**
     * @return whether this comparator sorts in ascending order (descending otherwise)
     */
    boolean isAscending();

    @Override
    ShopItemComparator reversed();
}
