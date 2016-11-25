/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.lanatus.shop.api.event;

import li.l1t.lanatus.api.purchase.Purchase;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called every time a purchase is attempted through Lanatus Shop.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-25-11
 */
public class PostPurchaseEvent extends PurchaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Purchase purchase;

    public PostPurchaseEvent(Player who, Purchase purchase) {
        super(who, purchase.getProduct());
        this.purchase = purchase;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
