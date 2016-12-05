/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.pex.product;

import li.l1t.mtc.module.lanatus.base.product.AbstractProductMetadata;

import java.util.List;
import java.util.UUID;

/**
 * Represents a product's Lanatus-PEx specific metadata.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-25-11
 */
public class SqlPexProduct extends AbstractProductMetadata implements PexProduct {
    private final List<String> commands;
    private String initialRank;
    private String targetRank;

    public SqlPexProduct(UUID productId, List<String> commands, String initialRank, String targetRank) {
        super(productId);
        this.commands = commands;
        this.initialRank = initialRank;
        this.targetRank = targetRank;
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

    @Override
    public List<String> getCommands() {
        return commands;
    }
}
