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

package li.l1t.mtc.module.lanatus.base.product;

import li.l1t.common.exception.DatabaseException;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.common.sql.sane.result.QueryResult;
import li.l1t.common.sql.sane.util.AbstractJdbcFetcher;
import li.l1t.common.sql.sane.util.JdbcEntityCreator;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/**
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-05
 */
public abstract class JdbcProductMetadataFetcher<T extends ProductMetadata> extends AbstractJdbcFetcher<T> {
    public JdbcProductMetadataFetcher(JdbcEntityCreator<? extends T> creator, SaneSql saneSql) {
        super(creator, saneSql);
    }

    public Optional<T> findByProduct(UUID productId) {
        try (QueryResult result = selectByProductId(productId)) {
            if(result.rs().next()) {
                return Optional.of(creator.createFromCurrentRow(result.rs()));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw DatabaseException.wrap(e);
        }
    }

    private QueryResult selectByProductId(UUID productId) {
        return select("WHERE product_id = ?", productId.toString());
    }
}
