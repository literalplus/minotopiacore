/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.shop.impl;

import com.google.common.base.Preconditions;
import li.l1t.mtc.module.shop.api.TransactionInfo;

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
