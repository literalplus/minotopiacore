/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.third.product;

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
public class SqlThirdProductRepository implements ThirdProductRepository {
    public static final String TABLE_NAME = "mt_main.lanatus_third_product";
    private final OptionalCache<UUID, ThirdProduct> productToMetadataCache = new OptionalGuavaCache<>();
    private final LanatusClient lanatus;
    private final JdbcThirdProductFetcher fetcher;

    @InjectMe
    public SqlThirdProductRepository(MTCLanatusClient lanatus, SaneSql sql) {
        this.lanatus = lanatus;
        fetcher = new JdbcThirdProductFetcher(new JdbcThirdProductCreator(), sql);
    }

    @Override
    public Optional<ThirdProduct> getByProduct(Product product) {
        return productToMetadataCache.getOrCompute(product.getUniqueId(), fetcher::findByProduct);
    }

    @Override
    public void clearCache() {
        productToMetadataCache.clear();
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
