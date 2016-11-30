/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
                product -> itemIconService.createIconStack(product, event.getPlayer().getUniqueId())
        );
    }
}
