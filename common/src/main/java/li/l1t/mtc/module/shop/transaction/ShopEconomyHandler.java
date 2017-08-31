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

package li.l1t.mtc.module.shop.transaction;

import li.l1t.mtc.hook.VaultHook;
import li.l1t.mtc.module.shop.ShopPriceCalculator;
import li.l1t.mtc.module.shop.TransactionType;
import li.l1t.mtc.module.shop.api.ShopItem;
import li.l1t.mtc.module.shop.api.ShopItemManager;
import li.l1t.mtc.module.shop.api.TransactionInfo;
import li.l1t.mtc.module.shop.impl.FailedTransactionInfo;
import li.l1t.mtc.module.shop.impl.RevokableTransactionInfo;
import li.l1t.mtc.module.shop.ui.util.ShopStringAdaptor;
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
