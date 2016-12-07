/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk.repository;

import li.l1t.mtc.module.lanatus.base.product.JdbcProductMetadataCreator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * Creates available perk metadata from JDBC result sets.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
class JdbcAvailablePerkCreator extends JdbcProductMetadataCreator<AvailablePerk> {
    @Override
    public AvailablePerk createFromCurrentRow(ResultSet rs) throws SQLException {
        return new AvailablePerk(
                productId(rs), nullableTimestamp(rs, "validuntil"),
                uuid(rs, "player_id")
        );
    }

    private Instant nullableTimestamp(ResultSet rs, String column) throws SQLException {
        Timestamp value = rs.getTimestamp(column);
        return value == null ? null : value.toInstant();
    }
}
