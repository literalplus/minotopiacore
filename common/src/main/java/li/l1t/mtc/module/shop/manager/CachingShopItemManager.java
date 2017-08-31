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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.shop.api.ShopItem;
import li.l1t.mtc.module.shop.api.ShopItemManager;
import li.l1t.mtc.module.shop.item.DataValueShopItem;
import li.l1t.mtc.module.shop.item.PotionShopItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

import static li.l1t.common.util.PredicateHelper.not;

/**
 * An implementation of a shop item manager that caches lookups for data value and potion shop items.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-22
 */
public class CachingShopItemManager implements ShopItemManager {
    private final Map<String, ShopItem> aliasesMap = new HashMap<>();
    private final Set<ShopItem> registeredItems = new HashSet<>();
    private final DataValueItemProvider dataValueProvider = new DataValueItemProvider();
    private final PotionItemProvider potionItemProvider = new PotionItemProvider();
    private final DiscountManager discountManager;

    @InjectMe
    public CachingShopItemManager(DiscountManager discountManager) {
        this.discountManager = Preconditions.checkNotNull(discountManager, "discountManager");
    }

    private ItemProvider<?> getProvider(Material material) {
        switch (material) {
            case POTION:
            case LINGERING_POTION:
            case SPLASH_POTION:
            case TIPPED_ARROW:
                return potionItemProvider;
            default:
                return dataValueProvider;
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends ShopItem> ItemProvider<T> getProvider(T item) {
        if (item instanceof DataValueShopItem) {
            return (ItemProvider<T>) dataValueProvider;
        } else if (item instanceof PotionShopItem) {
            return (ItemProvider<T>) potionItemProvider;
        } else {
            throw new UnsupportedOperationException(item.getClass().toString());
        }
    }

    @Override
    public Optional<? extends ShopItem> getItem(String input) {
        Preconditions.checkNotNull(input, "input");
        Optional<? extends ShopItem> itemByAlias = findItemByAlias(input.toLowerCase(Locale.GERMAN));
        if (itemByAlias.isPresent()) {
            return itemByAlias;
        } else {
            return findItemByInput(input);
        }
    }

    private Optional<? extends ShopItem> findItemByAlias(String alias) {
        return Optional.ofNullable(aliasesMap.get(alias));
    }

    private Optional<? extends ShopItem> findItemByInput(String input) {
        String[] rawParameters = input.split(":");
        String materialName = rawParameters[0];
        String[] parameters = Arrays.copyOfRange(rawParameters, 1, rawParameters.length);
        return findItemByMaterialAndParameters(materialName, parameters);
    }

    private Optional<? extends ShopItem> findItemByMaterialAndParameters(String materialName, String... parameters) {
        return Optional.ofNullable(Material.matchMaterial(materialName))
                .flatMap(material -> getProvider(material).findCachedByParameters(material, parameters));

    }

    @Override
    public Optional<? extends ShopItem> getItem(ItemStack stack) {
        return Optional.ofNullable(stack)
                .filter(not(this::isTradeProhibited))
                .filter(stk -> stk.getType() != Material.AIR)
                .flatMap(stk -> getProvider(stack.getType()).findCached(stack));
    }

    @Override
    public Optional<? extends ShopItem> getItem(Player player, String input) {
        if (input.equalsIgnoreCase("hand")) {
            return getItem(player.getInventory().getItemInMainHand());
        } else {
            return getItem(input);
        }
    }

    @Override
    public ShopItem createItem(ItemStack stack, String... parameters) {
        ShopItem item = getProvider(stack.getType()).createInstance(stack, parameters);
        registerItem(item);
        return item;
    }


    @Override
    public <T extends ShopItem> void registerItem(T item) {
        Preconditions.checkNotNull(item, "item");
        getProvider(item).cache(item);
        registeredItems.add(item);
        aliasesMap.putAll(
                item.getAliases().stream()
                        .collect(Collectors.toMap(String::toLowerCase, any -> item))
        );
    }

    @Override
    public <T extends ShopItem> void unregisterItem(T item) {
        Preconditions.checkNotNull(item, "item");
        getProvider(item).forget(item);
        registeredItems.remove(item);
        aliasesMap.values().removeIf(item::equals);
    }

    @Override
    public boolean isTradeProhibited(ItemStack stack) {
        return stack == null || !stack.getEnchantments().isEmpty();
    }

    @Override
    public Map<String, ShopItem> getItemAliases() {
        return ImmutableMap.copyOf(aliasesMap);
    }

    @Override
    public DiscountManager getDiscountManager() {
        return discountManager;
    }

    @Override
    public double getBuyCost(ShopItem shopItem) {
        return discountManager.getBuyCost(shopItem);
    }

    @Override
    public double getSellWorth(ShopItem shopItem) {
        Preconditions.checkNotNull(shopItem, "shopItem");
        return shopItem.getSellWorth();
    }

    @Override
    public List<ShopItem> getItems() {
        return ImmutableList.copyOf(registeredItems);
    }
}
