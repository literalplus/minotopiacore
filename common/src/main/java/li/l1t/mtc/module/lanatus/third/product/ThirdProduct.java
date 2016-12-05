/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.third.product;

import li.l1t.mtc.module.lanatus.base.product.ProductMetadata;

import java.util.List;

/**
 * Stores additional metadata for Lanatus third party products.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-25-11
 */
public interface ThirdProduct extends ProductMetadata {
    List<String> getCommands();
}
