package io.github.xxyy.mtc.module.shop.transaction;

import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.TransactionType;
import io.github.xxyy.mtc.module.shop.api.TransactionInfo;
import io.github.xxyy.mtc.module.shop.ui.text.ShopTextOutput;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Controls shop transactions, managing both sides in terms of economy and items.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 29/10/15
 */
public class ShopTransactionExecutor {
    private final ShopTextOutput output;
    private final TransactionHandler economyHandler;
    private final TransactionHandler inventoryHandler;

    public ShopTransactionExecutor(ShopModule module) {
        this.output = new ShopTextOutput(module);
        this.economyHandler = new ShopEconomyHandler(module.getItemManager(), module.getPlugin().getVaultHook());
        this.inventoryHandler = new ShopInventoryHandler();
    }

    /**
     * Attempts to execute a complete transaction. If any part of the transaction fails, anything already done will be
     * reversed and a nice error message will be sent to the player.
     *
     * @param plr    the player initiating the transaction
     * @param item   the item involved in the transaction
     * @param amount the amount of the item requested to be transferred
     * @param type   the type of the transaction
     * @return whether the transaction was successful
     */
    public boolean attemptTransaction(Player plr, ShopItem item, int amount, TransactionType type) {
        if (attemptTransactionInternal(plr, item, amount, type)) {
            output.sendTransactionSuccess(plr, item, amount, type);
            return true;
        }

        return false;
    }

    /**
     * Attempts to execute a complete transaction. If any part of the transaction fails, anything already done will be
     * reversed. No messages will be printed except in case of failure.
     *
     * @param plr    the player initiating the transaction
     * @param item   the item involved in the transaction
     * @param amount the amount of the item requested to be transferred
     * @param type   the type of the transaction
     * @return whether the transaction was successful
     */
    public boolean attemptTransactionSilent(Player plr, ShopItem item, int amount, TransactionType type) {
        return attemptTransactionInternal(plr, item, amount, type);
    }

    private boolean attemptTransactionInternal(Player plr, ShopItem item, int amount, TransactionType type) {
        /*
        For simplicity reasons, the action that takes the player's payment is always called first. So if they cannot
        deliver, the action will be canceled right away. Us not being able to deliver is far less probable.
         */

        TransactionHandler[] handlers;
        switch (type) {
            case BUY:
                handlers = new TransactionHandler[]{economyHandler, inventoryHandler};
                break;
            case SELL:
                handlers = new TransactionHandler[]{inventoryHandler, economyHandler};
                break;
            default:
                throw new AssertionError("invalid type: " + type);
        }

        return callHandlers(plr, item, amount, type, handlers);
    }

    private boolean callHandlers(Player plr, ShopItem item, int amount, TransactionType type,
                                 TransactionHandler... handlers) {
        Set<TransactionInfo> succeededSteps = new HashSet<>(handlers.length);
        for (TransactionHandler handler : handlers) {
            TransactionInfo info = handler.execute(plr, item, amount, type);
            if (!info.isSuccessful()) {
                output.sendTransactionFailure(plr, type, info.getTransactionError());
                succeededSteps.forEach(TransactionInfo::revoke);
                return false;
            } else {
                succeededSteps.add(info);
            }
        }
        return true;
    }
}
