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

import li.l1t.mtc.module.shop.api.ShopItem;

/**
 * Sorts shop items based on the underlying material id and data value.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-07-10
 */
public class IdBasedComparator extends AbstractShopItemComparator {
    public static final IdBasedComparator ASCENDING = new IdBasedComparator(true);
    public static final IdBasedComparator DESCENDING = new IdBasedComparator(false);

    private IdBasedComparator(boolean ascending) {
        super(ascending);
    }

    @Override
    public int compare(ShopItem o1, ShopItem o2) {
        int result;
        result = Integer.compare(o1.getMaterial().ordinal(), o2.getMaterial().ordinal());
        if (result == 0 && o1.getDisplayName() != null) {
            result = o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName());
        }
        return applyOrder(result);
    }

    @Override
    public String getDisplayName() {
        return "Nach Item-ID";
    }

    @Override
    public ShopItemComparator reversed() {
        return isAscending() ? DESCENDING : ASCENDING;
    }
}
