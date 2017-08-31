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

package li.l1t.mtc.module.shop.manager;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
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
}
