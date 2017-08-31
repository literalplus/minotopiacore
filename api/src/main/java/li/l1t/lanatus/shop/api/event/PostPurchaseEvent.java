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
