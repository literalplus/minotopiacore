package io.github.xxyy.mtc.module.shop.api;

/**
 * Stores the result of a (partial) shop transaction. If the transaction is revokable, allows it to be revoked.
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
     * @return a message describing an error that occurred during the transaction, or null if no error occurred
     */
    String getTransactionError();

    /**
     * Returns whether the initial transaction was successful. If the initial transaction was successful, the
     * transaction can be revoked exactly once. If it was not, the transaction cannot be revoked, since it has
     * already been reset to the previous state.
     *
     * @return whether the initial transaction was successful
     */
    boolean isSuccessful();
}
