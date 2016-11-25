/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.lanatus.shop.api.event;

import li.l1t.lanatus.api.builder.PurchaseBuilder;
import li.l1t.lanatus.api.product.Product;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called every time a purchase is attempted through Lanatus Shop.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-25-11
 */
public class PrePurchaseEvent extends PurchaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final PurchaseBuilder purchaseBuilder;
    private boolean cancelled;

    public PrePurchaseEvent(Player who, PurchaseBuilder purchaseBuilder, Product product) {
        super(who, product);
        this.purchaseBuilder = purchaseBuilder;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public PurchaseBuilder getPurchaseBuilder() {
        return purchaseBuilder;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
