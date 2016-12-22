/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.shop.manager;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import li.l1t.mtc.module.shop.api.ShopItem;
import li.l1t.mtc.module.shop.item.DataValueShopItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Provides caching and factory methods for data value shop items.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-22
 */
public class DataValueItemProvider implements ItemProvider<DataValueShopItem> {
    private final Table<Material, Short, DataValueShopItem> itemCache = HashBasedTable.create();
    private final Map<Material, DataValueShopItem> wildcardCache = new HashMap<>();

    @Override
    public Optional<DataValueShopItem> findCached(ItemStack stack) {
        return findCached(stack.getType(), stack.getDurability());
    }

    private Optional<DataValueShopItem> findCached(Material type, short dataValue) {
        return Optional.ofNullable(itemCache.get(type, dataValue))
                .map(Optional::of)
                .orElseGet(() -> Optional.ofNullable(wildcardCache.get(type)));
    }

    @Override
    public Optional<DataValueShopItem> findCachedByParameters(Material type, String... parameters) {
        Preconditions.checkNotNull(parameters, "parameters");
        if(parameters.length == 0) {
            return Optional.ofNullable(wildcardCache.get(type));
        } else {
            try {
                short dataValue = Short.parseShort(parameters[0]);
                return findCached(type, dataValue);
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
    }

    @Override
    public void cache(DataValueShopItem item) {
        if(item.isWildcard()) {
            wildcardCache.put(item.getMaterial(), item);
        } else {
            itemCache.put(item.getMaterial(), item.getDataValue(), item);
        }
    }

    @Override
    public void forget(DataValueShopItem item) {
        wildcardCache.remove(item.getMaterial(), item);
        itemCache.remove(item.getMaterial(), item.getDataValue());
    }

    @Override
    public DataValueShopItem createInstance(ItemStack stack, String... parameters) {
        return DataValueShopItem.fromItemStack(stack, parameters);
    }

    @Override
    public void clear() {
        wildcardCache.clear();
        itemCache.clear();
    }

    @Override
    public DataValueShopItem cast(ShopItem item) {
        Preconditions.checkArgument(item instanceof DataValueShopItem, "expected DataValueShopItem, got", item.getClass(), item);
        return (DataValueShopItem) item;
    }
}
