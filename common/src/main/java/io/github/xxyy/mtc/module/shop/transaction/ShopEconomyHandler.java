package io.github.xxyy.mtc.module.shop.transaction;

import io.github.xxyy.mtc.hook.VaultHook;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopPriceCalculator;
import io.github.xxyy.mtc.module.shop.TransactionType;
import io.github.xxyy.mtc.module.shop.api.ShopItemManager;
import io.github.xxyy.mtc.module.shop.api.TransactionInfo;
import io.github.xxyy.mtc.module.shop.impl.FailedTransactionInfo;
import io.github.xxyy.mtc.module.shop.impl.RevokableTransactionInfo;
import io.github.xxyy.mtc.module.shop.ui.util.ShopStringAdaptor;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

/**
 * Handles interfacing with the economy plugin.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 31/10/15
 */
public class ShopEconomyHandler implements TransactionHandler {
    private final ShopPriceCalculator calculator;
    private final VaultHook vaultHook;

    public ShopEconomyHandler(ShopItemManager manager, VaultHook vaultHook) {
        this.vaultHook = vaultHook;
        calculator = new ShopPriceCalculator(manager);
    }

    public TransactionInfo execute(Player plr, ShopItem item, int amount, TransactionType type) {
        if (!vaultHook.assureHasAccount(plr)) {
            return null;
        }

        double price = calculator.calculatePrice(item, amount, type);
        switch (type) {
            case BUY:
                if (!vaultHook.canAfford(plr, price)) {
                    return new FailedTransactionInfo("Du hast nicht genug Geld, um " +
                            ShopStringAdaptor.getAdjustedDisplayName(item, amount) +
                            " für " +
                            ShopStringAdaptor.getCurrencyString(price) + " zu kaufen. Du hast " +
                            ShopStringAdaptor.getCurrencyString(vaultHook.getBalance(plr)) + ".");
                }
                EconomyResponse responseBuy = vaultHook.withdrawPlayer(plr, price);
                if (!responseBuy.transactionSuccess()) {
                    return new FailedTransactionInfo("Fehler bei der Überweisung: " + responseBuy.errorMessage);
                }
                return new RevokableTransactionInfo(() -> vaultHook.depositPlayer(plr, price));
            case SELL:
                EconomyResponse responseSell = vaultHook.depositPlayer(plr, price);
                if (!responseSell.transactionSuccess()) {
                    return new FailedTransactionInfo("Fehler bei der Überweisung: " + responseSell.errorMessage);
                }
                return new RevokableTransactionInfo(() -> vaultHook.withdrawPlayer(plr, price));
            default:
                throw new AssertionError("Unknown type " + type);
        }
    }
}
