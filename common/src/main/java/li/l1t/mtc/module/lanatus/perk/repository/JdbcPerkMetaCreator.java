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

/**
 * Creates perk metadata instances from JDBC result sets.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
class JdbcPerkMetaCreator extends JdbcProductMetadataCreator<PerkMeta> {
    @Override
    public PerkMeta createFromCurrentRow(ResultSet rs) throws SQLException {
        return new PerkMeta(
                productId(rs),
                rs.getString("type"), rs.getString("data")
        );
    }
}
