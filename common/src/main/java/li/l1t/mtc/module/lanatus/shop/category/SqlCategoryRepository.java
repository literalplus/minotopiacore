/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.shop.category;

import li.l1t.common.exception.DatabaseException;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.common.sql.sane.SqlConnected;
import li.l1t.lanatus.api.LanatusClient;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.Category;
import li.l1t.lanatus.shop.api.CategoryRepository;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.lanatus.base.MTCLanatusClient;

import java.time.Duration;
import java.util.Collection;
import java.util.UUID;

/**
 * A category repository backed by a SQL database. Caches all requests.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-17-11
 */
public class SqlCategoryRepository implements CategoryRepository, SqlConnected {
    public static final String TABLE_NAME = "mt_main.lanatus_category";
    public static final String PRODUCT_MAPPING_TABLE_NAME = "mt_main.lanatus_category_product";
    private final LanatusClient client;
    private final SaneSql sql;
    private final JdbcCategoryFetcher categoryFetcher = new JdbcCategoryFetcher(new JdbcCategoryCreator(), sql());
    private final JdbcCategoryProductFetcher categoryProductFetcher = new JdbcCategoryProductFetcher(sql(), client().products());
    private final CategoryCache categoryCache = new CategoryCache(Duration.ofMinutes(5));

    @InjectMe
    public SqlCategoryRepository(MTCLanatusClient client, SaneSql sql) {
        this.client = client;
        this.sql = sql;
    }

    @Override
    public Collection<Category> findAll() throws DatabaseException {
        return categoryCache.getOrComputeAllCategories(categoryFetcher::findAllCategories);
    }

    @Override
    public Collection<Product> findProductsOf(Category category) throws DatabaseException {
        return categoryCache.getOrComputeProductsOf(category, categoryProductFetcher::findAssociatedProducts);
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

    @Override
    public SaneSql sql() {
        return sql;
    }
}
