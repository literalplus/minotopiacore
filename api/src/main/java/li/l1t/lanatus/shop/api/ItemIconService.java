/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.lanatus.shop.api;

import li.l1t.lanatus.api.product.Product;
import org.bukkit.inventory.ItemStack;

/**
 * A service that creates item stack icons from string specifications.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-16-11
 */
public interface ItemIconService {
    ItemStack createIconStack(Product product);

    ItemStack createIconStack(Category category);

    /**
     * Creates an informational stack with a provided title and description, which are translated to display name and
     * lore. The item will be one that indicates information. Newlines in the description will be translated.
     *
     * @param title       the title of the information
     * @param description the contents of the information
     * @return the info stack
     */
    ItemStack createInfoStack(String title, String description);
}
