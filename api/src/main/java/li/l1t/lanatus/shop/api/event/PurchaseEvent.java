/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.lanatus.shop.api.event;

import li.l1t.lanatus.api.product.Product;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

/**
 * Abstract base class for purchase events.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-25-11
 */
public abstract class PurchaseEvent extends PlayerEvent {
    protected final Product product;

    protected PurchaseEvent(Player who, Product product) {
        super(who);
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }
}
