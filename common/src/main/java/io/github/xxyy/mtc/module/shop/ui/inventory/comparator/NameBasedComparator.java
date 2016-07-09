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
 * Sorts shop items based on their display name.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-07-10
 */
public class NameBasedComparator implements Comparator<ShopItem> {
    @Override
    public int compare(ShopItem o1, ShopItem o2) {
        return o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName());
    }
}
