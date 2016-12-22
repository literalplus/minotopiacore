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
import org.bukkit.entity.Player;

/**
 * Handles part of a transaction process.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-01
 */
public interface TransactionHandler {
    /**
     * Attempts to execute a transaction with this handler. Transactions are reversible.
     *
     * @param plr    the player initiating the transaction
     * @param item   the item involved in the transaction
     * @param amount the amount requested to be included in the transaction
     * @param type   the type of the transaction
     * @return a {@link TransactionInfo} with information about the transaction
     */
    TransactionInfo execute(Player plr, ShopItem item, int amount, TransactionType type);
}
