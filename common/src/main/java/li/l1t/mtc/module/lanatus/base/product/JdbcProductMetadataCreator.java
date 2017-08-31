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


import li.l1t.common.sql.sane.util.AbstractJdbcEntityCreator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-05
 */
public abstract class JdbcProductMetadataCreator<T extends ProductMetadata> extends AbstractJdbcEntityCreator<T> {
    protected UUID productId(ResultSet rs) throws SQLException {
        return uuid(rs, "product_id");
    }

    protected List<String> commaSeparated(ResultSet rs, String column) throws SQLException {
        String source = rs.getString(column);
        return Arrays.stream(source.split("(?<!\\\\), ?"))
                .map(str -> str.replaceAll("\\\\,", ","))
                .collect(Collectors.toList());
    }
}
