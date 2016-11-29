/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.pex.product;

import li.l1t.common.collections.cache.OptionalCache;
import li.l1t.common.collections.cache.OptionalGuavaCache;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.lanatus.api.LanatusClient;
import li.l1t.lanatus.api.product.Product;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.lanatus.base.MTCLanatusClient;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for PEx product mettadata.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-25-11
 */
public class SqlPexProductRepository implements PexProductRepository {
    public static final String TABLE_NAME = "mt_main.lanatus_pex_product";
    private final OptionalCache<UUID, PexProduct> productToPexProductCache = new OptionalGuavaCache<>();
    private final LanatusClient lanatus;
    private final JdbcPexProductFetcher fetcher;

    @InjectMe
    public SqlPexProductRepository(MTCLanatusClient lanatus, SaneSql sql) {
        this.lanatus = lanatus;
        fetcher = new JdbcPexProductFetcher(new JdbcPexProductCreator(), sql);
    }

    @Override
    public Optional<PexProduct> getByProduct(Product product) {
        return productToPexProductCache.getOrCompute(product.getUniqueId(), fetcher::findByProduct);
    }

    @Override
    public void clearCache() {
        productToPexProductCache.clear();
    }

    @Override
    public void clearCachesFor(UUID playerId) {
        //no-op
    }

    @Override
    public LanatusClient client() {
        return lanatus;
    }
}
