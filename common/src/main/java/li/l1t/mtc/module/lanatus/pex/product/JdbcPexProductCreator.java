/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.pex.product;

import li.l1t.lanatus.sql.common.AbstractJdbcEntityCreator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Creates instances of pex product metadata from JDBC ResultSet objects.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-25-11
 */
class JdbcPexProductCreator extends AbstractJdbcEntityCreator<PexProduct> {
    @Override
    public PexProduct createFromCurrentRow(ResultSet rs) throws SQLException {
        return new SqlPexProduct(
                uuid(rs, "product_id"),
                commaSeparated(rs, "commands"), rs.getString("sourcerank"), rs.getString("targetrank")
        );
    }

    private List<String> commaSeparated(ResultSet rs, String column) throws SQLException {
        String source = rs.getString(column);
        return Arrays.asList(source.split(", ?"));
    }
}
