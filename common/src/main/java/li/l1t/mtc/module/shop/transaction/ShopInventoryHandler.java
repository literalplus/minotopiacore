/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.shop.transaction;

import li.l1t.mtc.module.shop.api.ShopItem;
import li.l1t.mtc.module.shop.TransactionType;
import li.l1t.mtc.module.shop.api.TransactionInfo;
import li.l1t.mtc.module.shop.impl.FailedTransactionInfo;
import li.l1t.mtc.module.shop.impl.RevokableTransactionInfo;
import li.l1t.mtc.module.shop.ui.util.ShopStringAdaptor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Handles interaction with players' inventories in transactions.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-01
 */
public class ShopInventoryHandler implements TransactionHandler {

    public TransactionInfo execute(Player plr, ShopItem item, int amount, TransactionType type) {
        switch (type) {
            case BUY:
                return executeBuy(plr, item, amount);
            case SELL:
                return executeSell(plr, item, amount);
            default:
                throw new AssertionError("invalid type: " + type);
        }
    }

    private TransactionInfo executeBuy(Player plr, ShopItem item, int amount) {
        ItemStack stack = item.toItemStack(amount);
        Map<Integer, ItemStack> unstored = plr.getInventory().addItem(stack);
        if (!unstored.isEmpty()) { //We did not store everything
            int alreadyStored = amount - unstored.values().stream() //Find out how many we already stored, since the
                    .mapToInt(ItemStack::getAmount)                 // amount may exceed the max stack size and Bukkit
                    .sum();                                         // stores partially too
            if (alreadyStored > 0) { //if we already stored something
                ItemStack toRemove = stack.clone();
                toRemove.setAmount(alreadyStored);
                plr.getInventory().removeItem(toRemove); //remove that many
            } //back at the start now
            return new FailedTransactionInfo("Du brauchst für " +
                    ShopStringAdaptor.getAdjustedDisplayName(item, amount) +
                    (int) Math.ceil(((double) amount) / ((double) stack.getMaxStackSize())) + " freie Slots in deinem Inventar!");
        }
        return new RevokableTransactionInfo(() -> plr.getInventory().removeItem(stack));
    }

    private TransactionInfo executeSell(Player plr, ShopItem item, int amount) {
        ItemStack stack = item.toItemStack(amount);
        if (!plr.getInventory().containsAtLeast(stack, amount)) {
            return new FailedTransactionInfo("Du hast nicht " +
                    ShopStringAdaptor.getAdjustedDisplayName(item, amount) +
                    " in deinem Inventar!");
        }
        Map<Integer, ItemStack> unretrieved = plr.getInventory().removeItem(stack);
        if (!unretrieved.isEmpty()) {
            int alreadyRetrieved = amount - unretrieved.values().stream()
                    .mapToInt(ItemStack::getAmount)
                    .sum();
            if (alreadyRetrieved > 0) {
                ItemStack toReturn = stack.clone();
                toReturn.setAmount(alreadyRetrieved);
                plr.getInventory().removeItem(toReturn);
            }
            return new FailedTransactionInfo("Du hast nicht " +
                    ShopStringAdaptor.getAdjustedDisplayName(item, amount) +
                    " in deinem Inventar?!");
        }
        return new RevokableTransactionInfo(() -> plr.getInventory().addItem(stack));
    }
}
