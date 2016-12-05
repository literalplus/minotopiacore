/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.base.product;

import li.l1t.lanatus.sql.common.AbstractJdbcEntityCreator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
        return Arrays.asList(source.split(", ?"));
    }
}
