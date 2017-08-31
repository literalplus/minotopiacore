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
