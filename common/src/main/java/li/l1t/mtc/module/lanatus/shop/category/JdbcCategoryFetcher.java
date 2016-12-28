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
import li.l1t.common.sql.sane.util.JdbcEntityCreator;
import li.l1t.lanatus.shop.api.Category;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

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
        try (QueryResult result = selectAll()) {
            while (result.rs().next()) {
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

    public Optional<Category> findSingle(UUID categoryId) {
        try (QueryResult result = selectById(categoryId)) {
            if (result.rs().next()) {
                return Optional.of(creator.createFromCurrentRow(result.rs()));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw DatabaseException.wrap(e);
        }
    }

    private QueryResult selectById(UUID categoryId) {
        return select("WHERE id = ?", categoryId.toString());
    }

    @Override
    protected String buildSelect(String whereClause) {
        return "SELECT id, displayname, icon, description FROM " + SqlCategoryRepository.TABLE_NAME + " " +
                whereClause + " " +
                "ORDER BY sort DESC";
    }
}
