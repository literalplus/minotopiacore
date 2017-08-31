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
