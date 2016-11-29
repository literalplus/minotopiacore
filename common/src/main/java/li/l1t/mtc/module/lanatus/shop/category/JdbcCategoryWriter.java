/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
