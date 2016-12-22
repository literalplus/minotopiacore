/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.shop.manager;

import li.l1t.mtc.module.shop.api.ShopItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * Provides caching and factory methods for a single type of shop item.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-22
 */
public interface ItemProvider<T extends ShopItem> {
    Optional<? extends ShopItem> findCached(ItemStack stack);

    Optional<? extends ShopItem> findCachedByParameters(Material type, String... parameters);

    void cache(T item);

    void forget(T item);

    void clear();

    T cast(ShopItem item);

    T createInstance(ItemStack stack, String... parameters);
}
