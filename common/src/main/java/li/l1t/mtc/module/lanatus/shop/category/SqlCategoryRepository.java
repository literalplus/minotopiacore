/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
    private final JdbcCategoryFetcher categoryFetcher;
    private final JdbcCategoryProductFetcher categoryProductFetcher;
    private final CategoryCache categoryCache = new CategoryCache(Duration.ofMinutes(5));
    private final JdbcCategoryWriter categoryWriter;

    @InjectMe
    public SqlCategoryRepository(MTCLanatusClient client, SaneSql sql) {
        super(sql);
        this.client = client;
        categoryProductFetcher = new JdbcCategoryProductFetcher(sql(), client().products());
        categoryFetcher = new JdbcCategoryFetcher(new JdbcCategoryCreator(), sql());
        categoryWriter = new JdbcCategoryWriter(sql());
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
