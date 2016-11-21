/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.shop.category;

import li.l1t.lanatus.shop.api.Category;
import li.l1t.lanatus.sql.common.AbstractJdbcEntityCreator;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Creates category instances from JDBC resuolt sets.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-17-11
 */
class JdbcCategoryCreator extends AbstractJdbcEntityCreator<Category> {
    @Override
    public Category createFromCurrentRow(ResultSet rs) throws SQLException {
        return new SqlCategory(uuid(rs, "id"), rs.getString("icon"), rs.getString("displayName"));
    }
}
