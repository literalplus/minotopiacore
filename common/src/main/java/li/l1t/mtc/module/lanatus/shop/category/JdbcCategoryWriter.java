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
import org.jetbrains.annotations.NotNull;

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
        updateCategory(category);
    }

    private void updateCategory(Category category) {
        sql().updateRaw(
                buildUpdate(),
                category.getDisplayName(), category.getIconName(),
                category.getDescription(), category.getUniqueId().toString()
        );
    }

    @NotNull
    private String buildUpdate() {
        return "UPDATE " + SqlCategoryRepository.TABLE_NAME + " SET " +
                "displayName=?, icon=?, description=? " +
                "WHERE id=?";
    }
}
