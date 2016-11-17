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
import li.l1t.lanatus.shop.api.Category;
import li.l1t.lanatus.sql.common.AbstractJdbcFetcher;
import li.l1t.lanatus.sql.common.JdbcEntityCreator;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Fetches categories from an underlying SQL database.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-17-11
 */
class JdbcCategoryFetcher extends AbstractJdbcFetcher<Category> {
    public JdbcCategoryFetcher(JdbcEntityCreator<? extends Category> creator, SaneSql saneSql) {
        super(creator, saneSql);
    }

    public Collection<Category> findAllCategories() {
        ImmutableList.Builder<Category> resultBuilder = ImmutableList.builder();
        try(QueryResult result = selectAll()) {
            while(result.rs().next()) {
                resultBuilder.add(creator.createFromCurrentRow(result.rs()));
            }
        } catch (SQLException e) {
            throw DatabaseException.wrap(e);
        }
        return resultBuilder.build();
    }

    private QueryResult selectAll() {
        return select("");
    }

    @Override
    protected String buildSelect(String whereClause) {
        return "SELECT id, displayname, icon, description FROM " + SqlCategoryRepository.TABLE_NAME + " " +
                whereClause + " " +
                "ORDER BY sort DESC";
    }
}
