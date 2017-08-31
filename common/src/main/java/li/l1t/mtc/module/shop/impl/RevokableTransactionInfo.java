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
