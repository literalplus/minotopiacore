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
