/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk.repository;

import li.l1t.common.sql.sane.SaneSql;
import li.l1t.lanatus.sql.common.JdbcEntityCreator;
import li.l1t.mtc.module.lanatus.base.product.JdbcProductMetadataFetcher;

/**
 * Fetches perk metadata from a JDBC data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
class JdbcPerkMetaFetcher extends JdbcProductMetadataFetcher<PerkMeta> {
    JdbcPerkMetaFetcher(JdbcEntityCreator<? extends PerkMeta> creator, SaneSql saneSql) {
        super(creator, saneSql);
    }

    @Override
    protected String buildSelect(String whereClause) {
        return "SELECT product_id, type, data FROM " + PerkRepository.TABLE_NAME + " " + whereClause;
    }
}
