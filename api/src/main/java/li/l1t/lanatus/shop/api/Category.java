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

package li.l1t.lanatus.shop.api;

import li.l1t.common.misc.Identifiable;

/**
 * Represents a category of products that are shown in the Lanatus shop. Note that this concept is exclusive to the shop
 * and has no counterpart in Lanatus. This does not relate to modules.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-16-11
 */
public interface Category extends Identifiable {
    /**
     * @return the string specifying the icon of this category in inventories
     */
    String getIconName();

    void setIconName(String iconName);

    /**
     * @return the human-readable display name used by this category
     */
    String getDisplayName();

    void setDisplayName(String displayName);

    /***
     * @return the human-readable description for this category
     */
    String getDescription();

    void setDescription(String description);
}
