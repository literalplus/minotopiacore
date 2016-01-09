/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.fulltag.dist;

import io.github.xxyy.mtc.module.fulltag.model.FullData;

/**
 * Signals that a full item scheduled for distribution could not be distributed.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 10/09/15
 */
public class FullDistributionException extends Exception {
    private final FullData fullData;

    public FullDistributionException(FullData data, String message) {
        this(data, message, null);
    }

    public FullDistributionException(FullData data, String message, Throwable cause) {
        super(message, cause);
        this.fullData = data;
    }

    /**
     * @return the full data that failed distribution
     */
    public FullData getFullData() {
        return fullData;
    }
}
