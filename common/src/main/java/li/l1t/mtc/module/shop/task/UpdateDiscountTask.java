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

package li.l1t.mtc.module.shop.task;

import li.l1t.common.util.task.NonAsyncBukkitRunnable;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.module.shop.ShopModule;
import li.l1t.mtc.module.shop.api.ShopItem;
import org.apache.logging.log4j.Logger;


/**
 * A task that periodically selects a new item to discount using the item manager.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-29
 */
public class UpdateDiscountTask extends NonAsyncBukkitRunnable {
    private static final Logger LOGGER = LogManager.getLogger(UpdateDiscountTask.class);
    private final ShopModule module;

    public UpdateDiscountTask(ShopModule module) {
        this.module = module;
    }

    @Override
    public void run() {
        ShopItem discountedItem = module.getItemManager().getDiscountManager()
                .selectDiscountedItem(module.getItemManager());

        if (discountedItem != null) {
            module.getTextOutput().announceDiscount(discountedItem);
        } else {
            LOGGER.info("No shop items available for discount."); //That doesn't interfere with functionality
        }
    }
}
