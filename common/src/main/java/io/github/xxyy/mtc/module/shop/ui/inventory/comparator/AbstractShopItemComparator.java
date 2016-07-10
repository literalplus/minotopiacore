/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.inventory.comparator;

/**
 * Abstract base class for shop item comparators. Implementations must respect
 * {@link #isAscending()}. There is {@link #applyOrder(int) a convenience method} provided to
 * easily apply order to normal results.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-07-10
 */
public abstract class AbstractShopItemComparator implements ShopItemComparator {
    private boolean ascending = true;

    /**
     * Creates a new abstract comparator.
     *
     * @param ascending whether the comparator sorts in ascending order
     */
    public AbstractShopItemComparator(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public boolean isAscending() {
        return ascending;
    }

    /**
     * Applies this comparator's {@link #isAscending() sorting order} to a comparator result.
     *
     * @param result the ascending result to apply to
     * @return the adapted result
     */
    protected int applyOrder(int result) {
        if (isAscending()) {
            return result;
        } else {
            return result * -1;
        }
    }
}
