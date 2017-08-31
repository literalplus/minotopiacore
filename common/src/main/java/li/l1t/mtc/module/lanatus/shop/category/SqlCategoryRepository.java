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

package li.l1t.mtc.module.lanatus.shop.category;

import li.l1t.common.exception.DatabaseException;
import li.l1t.common.sql.sane.AbstractSqlConnected;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.lanatus.api.LanatusClient;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.Category;
import li.l1t.lanatus.shop.api.CategoryRepository;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.lanatus.base.MTCLanatusClient;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * A category repository backed by a SQL database. Caches all requests.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-17-11
 */
public class SqlCategoryRepository extends AbstractSqlConnected implements CategoryRepository {
    public static final String TABLE_NAME = "mt_main.lanatus_category";
    public static final String PRODUCT_MAPPING_TABLE_NAME = "mt_main.lanatus_category_product";
    private final LanatusClient client;
    private final JdbcCategoryWriter categoryWriter;
    private final JdbcCategoryFetcher categoryFetcher;
    private final JdbcCategoryProductWriter categoryProductWriter;
    private final JdbcCategoryProductFetcher categoryProductFetcher;
    private final CategoryCache categoryCache = new CategoryCache(Duration.ofMinutes(5));

    @InjectMe
    public SqlCategoryRepository(MTCLanatusClient client, SaneSql sql) {
        super(sql);
        this.client = client;
        categoryProductFetcher = new JdbcCategoryProductFetcher(sql(), client().products());
        categoryFetcher = new JdbcCategoryFetcher(new JdbcCategoryCreator(), sql());
        categoryWriter = new JdbcCategoryWriter(sql());
        categoryProductWriter = new JdbcCategoryProductWriter(sql());
    }

    @Override
    public Collection<Category> findAll() throws DatabaseException {
        return categoryCache.getOrComputeAllCategories(categoryFetcher::findAllCategories);
    }

    @Override
    public Optional<Category> findSingle(UUID categoryId) {
        return categoryCache.findCachedCategory(categoryId)
                .map(Optional::of)
                .orElseGet(() -> categoryFetcher.findSingle(categoryId));
    }

    @Override
    public Collection<Product> findProductsOf(Category category) throws DatabaseException {
        return categoryCache.getOrComputeProductsOf(category, categoryProductFetcher::findAssociatedProducts);
    }

    @Override
    public void save(Category category) throws DatabaseException {
        categoryWriter.save(category);
    }

    @Override
    public void associate(Category category, Product product) throws DatabaseException {
        categoryProductWriter.createAssociation(category, product);
        categoryCache.clearProductCacheFor(category);
    }

    @Override
    public void dissociate(Category category, Product product) {
        categoryProductWriter.removeAssociation(category, product);
        categoryCache.clearProductCacheFor(category);
    }

    @Override
    public void clearCache() {
        categoryCache.clearCache();
    }

    @Override
    public void clearCachesFor(UUID playerId) {
        //no-op
    }

    @Override
    public LanatusClient client() {
        return client;
    }
}
