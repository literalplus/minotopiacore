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

package li.l1t.mtc.module.shop.api;

/**
 * Stores the result of a (partial) shop transaction. If the transaction is revokable, allows it to
 * be revoked.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 31/10/15
 */
public interface TransactionInfo {
    /**
     * Revokes this transaction.
     *
     * @throws IllegalStateException if this transaction has already been revoked
     */
    void revoke() throws IllegalStateException;

    /**
     * @return whether this transaction can be revoked
     */
    boolean isRevokable();

    /**
     * @return a message describing an error that occurred during the transaction, or null if no
     * error occurred
     */
    String getTransactionError();

    /**
     * Returns whether the initial transaction was successful. If the initial transaction was
     * successful, the transaction can be revoked exactly once. If it was not, the transaction
     * cannot be revoked, since it has already been reset to the previous state.
     *
     * @return whether the initial transaction was successful
     */
    boolean isSuccessful();
}
