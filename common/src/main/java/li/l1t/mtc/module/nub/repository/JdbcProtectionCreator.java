/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub.repository;

import li.l1t.common.sql.sane.util.AbstractJdbcEntityCreator;
import li.l1t.mtc.module.nub.api.NubProtection;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Creates protection data from JDBC result set objects.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-08
 */
class JdbcProtectionCreator extends AbstractJdbcEntityCreator<NubProtection> {
    @Override
    public NubProtection createFromCurrentRow(ResultSet rs) throws SQLException {
        return new SqlProtection(
                uuid(rs, "player_id"), rs.getInt("minutesleft")
        );
    }
}
