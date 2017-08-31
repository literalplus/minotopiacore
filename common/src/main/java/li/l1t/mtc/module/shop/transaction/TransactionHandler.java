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

import li.l1t.mtc.module.shop.TransactionType;
import li.l1t.mtc.module.shop.api.ShopItem;
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
