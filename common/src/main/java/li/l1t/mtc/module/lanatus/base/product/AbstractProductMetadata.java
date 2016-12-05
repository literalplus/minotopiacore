/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
