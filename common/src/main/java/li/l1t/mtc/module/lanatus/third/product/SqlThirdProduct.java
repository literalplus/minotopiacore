/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.third.product;

import li.l1t.mtc.module.lanatus.base.product.AbstractProductMetadata;

import java.util.List;
import java.util.UUID;

/**
 * Represents a product's Lanatus Third specific metadata.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-05
 */
public class SqlThirdProduct extends AbstractProductMetadata implements ThirdProduct {
    private final List<String> commands;

    public SqlThirdProduct(UUID productId, List<String> commands) {
        super(productId);
        this.commands = commands;
    }

    @Override
    public List<String> getCommands() {
        return commands;
    }

}