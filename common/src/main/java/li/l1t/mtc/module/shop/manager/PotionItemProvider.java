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
import li.l1t.common.util.PotionHelper;
import li.l1t.mtc.module.shop.item.PotionShopItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Provides caching and factory methods for potion shop items.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-22
 */
public class PotionItemProvider implements ItemProvider<PotionShopItem> {
    private final Map<String, PotionShopItem> itemCache = new HashMap<>();

    @Override
    public Optional<PotionShopItem> findCached(ItemStack stack) {
        return findCached(effectFromStack(stack));
    }

    private PotionData effectFromStack(ItemStack stack) {
        return ((PotionMeta) stack.getItemMeta()).getBasePotionData();
    }

    private Optional<PotionShopItem> findCached(PotionData effect) {
        return findCached(PotionHelper.stringFromData(effect));
    }

    private Optional<PotionShopItem> findCached(String effectSpec) {
        return Optional.ofNullable(itemCache.get(effectSpec));
    }

    @Override
    public Optional<PotionShopItem> findCachedByParameters(Material type, String... parameters) {
        Preconditions.checkNotNull(parameters, "parameters");
        if (parameters.length == 0) {
            return Optional.empty();
        } else if (parameters.length == 1) {
            return findCached(normalizeDataSpec(parameters[0]));
        } else {
            return findCached(normalizeDataSpec(parameters[0] + ":" + parameters[1]));
        }
    }

    private String normalizeDataSpec(String spec) {
        return PotionHelper.stringFromData(PotionHelper.dataFromString(spec));
    }

    @Override
    public void cache(PotionShopItem item) {
        itemCache.put(PotionHelper.stringFromData(item.getPotionData()), item);
    }

    @Override
    public void forget(PotionShopItem item) {
        itemCache.values().removeIf(item::equals);
    }

    @Override
    public PotionShopItem createInstance(ItemStack stack, String... parameters) {
        return PotionShopItem.fromItemStack(stack, parameters);
    }

    @Override
    public void clear() {
        itemCache.clear();
    }
}
