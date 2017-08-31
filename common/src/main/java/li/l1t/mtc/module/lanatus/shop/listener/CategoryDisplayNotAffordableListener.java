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

package li.l1t.mtc.module.lanatus.shop.listener;

import li.l1t.lanatus.shop.api.ItemIconService;
import li.l1t.lanatus.shop.api.event.CategoryDisplayEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Listens for product list events and changes the icon stacks of not affordable products.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-30-11
 */
public class CategoryDisplayNotAffordableListener implements Listener {
    private final ItemIconService itemIconService;

    public CategoryDisplayNotAffordableListener(ItemIconService itemIconService) {
        this.itemIconService = itemIconService;
    }

    @EventHandler
    public void onCategoryDisplay(CategoryDisplayEvent event) {
        int melonsCount = event.getAccount().getMelonsCount();
        event.remapIf(
                product -> product.getMelonsCost() > melonsCount,
                product -> itemIconService.createNotAffordableStack(product, event.getAccount())
        );
    }
}
