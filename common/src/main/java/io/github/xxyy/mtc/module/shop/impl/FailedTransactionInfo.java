package io.github.xxyy.mtc.module.shop.impl;

import io.github.xxyy.mtc.module.shop.api.TransactionInfo;

/**
 * Stores information about a failed transaction.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-10-31
 */
public class FailedTransactionInfo implements TransactionInfo {
    private final String transactionError;

    public FailedTransactionInfo(String transactionError) {
        this.transactionError = transactionError;
    }


    @Override
    public void revoke() throws IllegalStateException {
        throw new IllegalStateException("Cannot revoke a failed transaction!");
    }

    @Override
    public boolean isRevokable() {
        return false;
    }

    @Override
    public boolean isSuccessful() {
        return false;
    }

    @Override
    public String getTransactionError() {
        return transactionError;
    }
}
