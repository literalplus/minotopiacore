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
import li.l1t.lanatus.shop.api.Category;

/**
 * Writes categories to a JDBC data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-22-11
 */
class JdbcCategoryWriter extends AbstractSqlConnected {
    JdbcCategoryWriter(SaneSql saneSql) {
        super(saneSql);
    }

    public void save(Category category) {
        insertOfUpdateCategory(category);
    }

    private void insertOfUpdateCategory(Category category) {
        sql().updateRaw(
                buildUpdate(),
                category.getUniqueId().toString(),
                category.getDisplayName(), category.getIconName(),
                category.getDescription(), category.getDisplayName(),
                category.getIconName(), category.getDescription()
        );
    }

    private String buildUpdate() {
        return "INSERT INTO " + SqlCategoryRepository.TABLE_NAME + " SET " +
                "id=?, displayName=?, icon=?, description=? " +
                "ON DUPLICATE KEY UPDATE displayName=?, icon=?, description=?";
    }
}
