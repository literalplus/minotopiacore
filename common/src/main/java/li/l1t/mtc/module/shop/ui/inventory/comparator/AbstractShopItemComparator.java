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

package li.l1t.mtc.module.shop.ui.inventory.comparator;

/**
 * Abstract base class for shop item comparators. Implementations must respect {@link
 * #isAscending()}. There is {@link #applyOrder(int) a convenience method} provided to easily apply
 * order to normal results.
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
