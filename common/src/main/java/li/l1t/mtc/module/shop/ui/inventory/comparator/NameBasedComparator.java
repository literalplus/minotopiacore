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
