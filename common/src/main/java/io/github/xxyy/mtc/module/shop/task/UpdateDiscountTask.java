/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.task;

import io.github.xxyy.common.util.task.NonAsyncBukkitRunnable;
import io.github.xxyy.mtc.logging.LogManager;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
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
