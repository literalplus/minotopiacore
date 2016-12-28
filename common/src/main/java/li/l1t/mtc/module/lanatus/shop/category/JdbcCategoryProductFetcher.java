/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.shop.category;

import com.google.common.collect.ImmutableList;
import li.l1t.common.exception.DatabaseException;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.common.sql.sane.result.QueryResult;
import li.l1t.common.sql.sane.util.AbstractJdbcFetcher;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.api.product.ProductRepository;
import li.l1t.lanatus.shop.api.Category;

import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

/**
 * Fetches the list of products in a category from an underlying JDBC database.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-17-11
 */
class JdbcCategoryProductFetcher extends AbstractJdbcFetcher<UUID> {
    private final ProductRepository productRepository;

    JdbcCategoryProductFetcher(SaneSql saneSql, ProductRepository productRepository) {
        super(new JdbcProductIdCreator(), saneSql);
        this.productRepository = productRepository;
    }

    public Collection<Product> findAssociatedProducts(Category category) {
        ImmutableList.Builder<Product> products = ImmutableList.builder();
        try(QueryResult result = selectByCategory(category)) {
            while(result.rs().next()) {
                UUID productId = creator.createFromCurrentRow(result.rs());
                Product product = productRepository.findById(productId);
                products.add(product);
            }
        } catch (SQLException e) {
            throw DatabaseException.wrap(e);
        }
        return products.build();
    }

    private QueryResult selectByCategory(Category category) {
        return select("category_id = ?", category.getUniqueId().toString());
    }

    @Override
    protected String buildSelect(String whereClause) {
        return "SELECT product_id FROM " + SqlCategoryRepository.PRODUCT_MAPPING_TABLE_NAME + " " +
                "WHERE " + whereClause;
    }
}
