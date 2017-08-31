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

package li.l1t.lanatus.shop.api;

import li.l1t.common.exception.DatabaseException;
import li.l1t.lanatus.api.LanatusRepository;
import li.l1t.lanatus.api.product.Product;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

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
     * Finds a single category by its unique id
     *
     * @param categoryId the unique id
     * @return an optional containing the category with given id, or an empty optional if there is none
     */
    Optional<Category> findSingle(UUID categoryId);

    /**
     * @param category the category to find the products for
     * @return the collection of products associated with given category, or an empty collection if none
     */
    Collection<Product> findProductsOf(Category category) throws DatabaseException;

    /**
     * Saves given entity's status to the database. Note that this does not respect remote changes and blindly
     * overwrites everything. If the category does not yet exist in the database, it is created.
     *
     * @param category the category to save
     * @throws DatabaseException if a database error occurs
     */
    void save(Category category) throws DatabaseException;

    /**
     * @param category the category to operate on
     * @param product  the product to associate with the category
     * @throws DatabaseException if a database error occurs
     */
    void associate(Category category, Product product) throws DatabaseException;

    /**
     * @param category the category to operate on
     * @param product  the product to dissociate from the category
     * @throws DatabaseException if a database error occurs
     */
    void dissociate(Category category, Product product);
}
