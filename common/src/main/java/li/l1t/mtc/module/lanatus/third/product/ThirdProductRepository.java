/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.third.product;

import li.l1t.lanatus.api.LanatusRepository;
import li.l1t.lanatus.api.product.Product;

import java.util.Optional;

/**
 * A repository for additional metadata attached to third products.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-05
 */
public interface ThirdProductRepository extends LanatusRepository {
    Optional<ThirdProduct> getByProduct(Product product);
}
