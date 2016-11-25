/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.pex.product;

import java.util.UUID;

/**
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-25-11
 */
public class SqlPexProduct implements PexProduct {
    private final UUID productId;
    private String initialRank;
    private String targetRank;

    public SqlPexProduct(UUID productId, String initialRank, String targetRank) {
        this.productId = productId;
        this.initialRank = initialRank;
        this.targetRank = targetRank;
    }

    @Override
    public UUID getProductId() {
        return productId;
    }

    @Override
    public String getInitialRank() {
        return initialRank;
    }

    public void setInitialRank(String initialRank) {
        this.initialRank = initialRank;
    }

    @Override
    public String getTargetRank() {
        return targetRank;
    }

    public void setTargetRank(String targetRank) {
        this.targetRank = targetRank;
    }
}
