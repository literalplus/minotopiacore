/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.lanatus.shop.api;

import li.l1t.common.exception.DatabaseException;
import li.l1t.lanatus.api.LanatusRepository;
import li.l1t.lanatus.api.product.Product;

import java.util.Collection;

/**
 * A repository for categories. May heavily cache all calls.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-16-11
 */
public interface CategoryRepository extends LanatusRepository {
    /**
     * @return a collection of all known categories, or an empty collection if there are none
     */
    Collection<Category> findAll() throws DatabaseException;

    /**
     * @param category the category to find the products for
     * @return the collection of products associated with given category, or an empty collection if none
     */
    Collection<Product> findProductsOf(Category category) throws DatabaseException;
}
