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

import li.l1t.common.util.inventory.ItemStackFactory;
import li.l1t.lanatus.api.account.LanatusAccount;
import li.l1t.lanatus.api.product.Product;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * A service that creates item stack icons from string specifications.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-16-11
 */
public interface ItemIconService {
    ItemStack createIconStack(Product product, UUID playerId);

    ItemStackFactory createRawIconStack(Product product, boolean hasPosition);

    ItemStack createNotAffordableStack(Product product, LanatusAccount account);

    ItemStack createIconStack(Category category);

    ItemStack createPurchaseHelpStack();

    /**
     * Creates an informational stack with a provided title and description, which are translated to display name and
     * lore. The item will be one that indicates information.
     *
     * @param title            the title of the information
     * @param descriptionLines the lines of the contents of the information
     * @return the info stack
     */
    ItemStack createInfoStack(String title, String... descriptionLines);
}
