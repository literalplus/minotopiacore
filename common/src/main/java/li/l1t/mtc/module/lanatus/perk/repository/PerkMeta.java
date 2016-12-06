/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk.repository;

import li.l1t.mtc.module.lanatus.base.product.AbstractProductMetadata;

import java.util.UUID;

/**
 * Represents metadata of a perk.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public class PerkMeta extends AbstractProductMetadata {
    private final String type;
    private final String data;

    public PerkMeta(UUID productId, String type, String data) {
        super(productId);
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }
}
