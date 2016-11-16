/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.lanatus.shop.api.metrics;

import li.l1t.lanatus.api.product.Product;
import org.bukkit.entity.Player;

/**
 * Receives events for purchases and stores the information for statistical analysis. Need not store all events.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-16-11
 */
public interface PurchaseRecorder {
    void handlePurchase(Player player, Product product);
}
