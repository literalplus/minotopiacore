package io.github.xxyy.mtc.module.shop;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Represents a type of transaction a user can commence with the shop.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 28/10/15
 */
public enum TransactionType {
    SELL {
        @Override
        public double getValue(@NotNull ShopItem item) {
            return item.getManager().getSellWorth(item);
        }

        @Override
        public boolean isTradable(ShopItem item) {
            return item != null && item.canBeSold();
        }
    },
    BUY {
        @Override
        public double getValue(@NotNull ShopItem item) {
            return item.getManager().getBuyCost(item);
        }

        @Override
        public boolean isTradable(ShopItem item) {
            return item != null && item.canBeBought();
        }
    };

    /**
     * Calculates the current value of given item in respect to this transaction type.
     *
     * @param item the item, may not be null
     * @return the value of given item for this transaction type
     */
    public abstract double getValue(@Nonnull ShopItem item);

    /**
     * Checks whether given item is currently tradable for this transaction type. Null items are not tradable.
     *
     * @param item the item, may be null
     * @return whether given item is tradable for this transaction type
     */
    public abstract boolean isTradable(ShopItem item);
}
