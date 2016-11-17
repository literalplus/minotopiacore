/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.shop.category;

import com.google.common.base.Preconditions;
import li.l1t.common.collections.cache.GuavaMapCache;
import li.l1t.common.collections.cache.MapCache;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.Category;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Handles caching of categories and related information.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-17-11
 */

class CategoryCache {
    private final Duration writeExpiryDuration;
    private final MapCache<UUID, Collection<Product>> categoryProductCache;
    private Collection<Category> allCategoriesCache = null;
    private Instant allCategoriesLastUpdatedTime = Instant.MIN;

    CategoryCache(Duration writeExpiryDuration) {
        this.writeExpiryDuration = writeExpiryDuration;
        categoryProductCache = new GuavaMapCache<>(writeExpiryDuration.getSeconds(), TimeUnit.SECONDS);
    }

    public Collection<Product> getOrComputeProductsOf(Category category, Function<Category, Collection<Product>> function) {
        Optional<Collection<Product>> cached = findCachedProducts(category);
        if(cached.isPresent()) {
            return cached.get();
        } else {
            Collection<Product> computed = function.apply(category);
            cacheProducts(category, computed);
            return computed;
        }
    }

    public void cacheProducts(Category category, Collection<Product> products) {
        Preconditions.checkNotNull("category", category);
        Preconditions.checkNotNull("products", products);
        categoryProductCache.cache(category.getUniqueId(), products);
    }

    public Optional<Collection<Product>> findCachedProducts(Category category) {
        Preconditions.checkNotNull("category", category);
        return categoryProductCache.get(category.getUniqueId());
    }

    public Collection<Category> getOrComputeAllCategories(Supplier<Collection<Category>> supplier) {
        Optional<Collection<Category>> cached = findAllCategoriesCached();
        if (cached.isPresent()) {
            return cached.get();
        } else {
            Collection<Category> computed = supplier.get();
            cacheAllCategories(computed);
            return computed;
        }
    }

    public Optional<Collection<Category>> findAllCategoriesCached() {
        if (allCategoriesCache == null) {
            return Optional.empty();
        } else if (allCategoriesLastUpdatedTime.plus(writeExpiryDuration).isAfter(Instant.now())) {
            allCategoriesCache = null;
            return Optional.empty();
        } else {
            return Optional.of(allCategoriesCache);
        }
    }

    public void cacheAllCategories(Collection<Category> allCategories) {
        this.allCategoriesCache = allCategories;
    }

    public void clearCache() {
        this.allCategoriesCache = null;
        this.categoryProductCache.clear();
    }
}
