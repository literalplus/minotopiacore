package io.github.xxyy.mtc.module.shop;

import org.bukkit.entity.Player;

/**
 * Controls shop transactions, managing both sides in terms of economy and items.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 29/10/15
 */
public class ShopTransactionExecutor {
    private final ShopModule module;

    public ShopTransactionExecutor(ShopModule module) {
        this.module = module;
    }

    public boolean attemptTransaction(Player plr, ShopItem item, int amount, TransactionType type) {
        //TODO
        //check eco account
        //branch off into type-specific methods
        return false;
    }
}
