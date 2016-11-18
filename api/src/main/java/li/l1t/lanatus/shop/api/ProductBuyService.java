/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.lanatus.shop.api;

import li.l1t.lanatus.api.product.Product;
import org.bukkit.entity.Player;

/**
 * A service that helps with buying products.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-16-11
 */
public interface ProductBuyService {
    /**
     * Attempts to purchase given product with given player's account, notifying them of the status of the purchase once
     * complete.
     *
     * @param player  the player whose account to use
     * @param product the product to purchase
     */
    void attemptPurchase(Player player, Product product);

    /**
     * @param player th player whose melons count to retrieve
     */
    int findCurrentMelonsCount(Player player);

}
