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

import li.l1t.common.sql.sane.AbstractSqlConnected;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.Category;

/**
 * Creates and deletes category-product associations.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-22-11
 */
public class JdbcCategoryProductWriter extends AbstractSqlConnected {
    protected JdbcCategoryProductWriter(SaneSql saneSql) {
        super(saneSql);
    }

    public void createAssociation(Category category, Product product) {
        sql().updateRaw(
                "INSERT IGNORE INTO " + SqlCategoryRepository.PRODUCT_MAPPING_TABLE_NAME + " " +
                        "SET category_id=?, product_id=?",
                category.getUniqueId().toString(), product.getUniqueId().toString()
        );
    }

    public void removeAssociation(Category category, Product product) {
        sql().updateRaw(
                "DELETE FROM "+SqlCategoryRepository.PRODUCT_MAPPING_TABLE_NAME+" " +
                        "WHERE category_id=? AND product_id=?",
                category.getUniqueId().toString(), product.getUniqueId().toString()
        );
    }
}
