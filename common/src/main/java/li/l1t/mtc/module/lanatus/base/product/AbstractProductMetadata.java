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

package li.l1t.mtc.module.lanatus.base.product;

import com.google.common.base.Preconditions;
import li.l1t.mtc.module.lanatus.third.product.SqlThirdProduct;

import java.util.UUID;

/**
 * Abstract base class for product metadata implementations.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-05
 */
public class AbstractProductMetadata implements ProductMetadata {
    protected final UUID productId;

    public AbstractProductMetadata(UUID productId) {
        this.productId = Preconditions.checkNotNull(productId, "productId");
    }

    @Override
    public UUID getProductId() {
        return productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SqlThirdProduct)) return false;

        SqlThirdProduct that = (SqlThirdProduct) o;

        return productId.equals(that.productId);
    }

    @Override
    public int hashCode() {
        return productId.hashCode();
    }
}
