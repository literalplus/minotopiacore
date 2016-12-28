/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.pex.product;

import li.l1t.common.sql.sane.SaneSql;
import li.l1t.common.sql.sane.util.JdbcEntityCreator;
import li.l1t.mtc.module.lanatus.base.product.JdbcProductMetadataFetcher;

/**
 * Fetches PEx product metadata from an underlying SQL database.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-25-11
 */
class JdbcPexProductFetcher extends JdbcProductMetadataFetcher<PexProduct> {
    public JdbcPexProductFetcher(JdbcEntityCreator<? extends PexProduct> creator, SaneSql saneSql) {
        super(creator, saneSql);
    }

    @Override
    protected String buildSelect(String whereClause) {
        return "SELECT product_id, sourcerank, targetrank, commands FROM " + SqlPexProductRepository.TABLE_NAME + " " + whereClause;
    }
}
