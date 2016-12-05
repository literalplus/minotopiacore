/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.base.product;

import java.util.UUID;

/**
 * Represents additional metadata for Lanatus products.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-05
 */
public interface ProductMetadata {
    /**
     * @return the unique id of the product this metadata is associated with
     */
    UUID getProductId();
}
