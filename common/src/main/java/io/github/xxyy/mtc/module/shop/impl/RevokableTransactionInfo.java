package io.github.xxyy.mtc.module.shop.impl;

import com.google.common.base.Preconditions;
import io.github.xxyy.mtc.module.shop.api.TransactionInfo;

/**
 * A simple implementation of a revokable transaction.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-10-31
 */
public class RevokableTransactionInfo implements TransactionInfo {
    private final Runnable revokeAction;
    private boolean revokable = true;

    public RevokableTransactionInfo(Runnable revokeAction) {
        this.revokeAction = revokeAction;
    }

    @Override
    public void revoke() throws IllegalStateException {
        Preconditions.checkState(isRevokable(), "Already revoked!");
        revokeAction.run();
        revokable = false;
    }

    @Override
    public boolean isRevokable() {
        return revokable;
    }

    @Override
    public String getTransactionError() {
        return null;
    }

    @Override
    public boolean isSuccessful() {
        return true;
    }
}
