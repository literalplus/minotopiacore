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

package li.l1t.mtc.module.shop.item;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.module.shop.api.ShopItem;
import org.apache.logging.log4j.Logger;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Abstract base class for shop items, modelling all properties and providing basic serialisation and deserialisation
 * for them.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-21
 */
public abstract class AbstractShopItem implements ShopItem {
    private static final Logger LOGGER = LogManager.getLogger(AbstractShopItem.class);
    private static final String ALIASES_PATH = "aliases";
    private static final String BUY_COST_PATH = "buy";
    private static final String SELL_WORTH_PATH = "sell";
    private static final String DISCOUNTED_PRICE_PATH = "discounted_price";
    private static final String MATERIAL_PATH = "material";
    private final Material material;
    private final List<String> aliases;
    private double buyCost;
    private double sellWorth;
    private double discountedPrice;
    private transient int discountPercentage; //does not get serialized - internal cache value

    public AbstractShopItem(Material material, List<String> aliases,
                            double sellWorth, double buyCost, double discountedPrice) {
        this.material = Preconditions.checkNotNull(material, "material");
        this.aliases = Preconditions.checkNotNull(aliases, "aliases");
        setSellWorth(sellWorth);
        setBuyCost(buyCost);
        setDiscountedPrice(discountedPrice);
    }

    public AbstractShopItem(Map<String, Object> serializedMap) {
        Preconditions.checkNotNull(serializedMap, "serializedMap");
        this.material = find(String.class, MATERIAL_PATH, serializedMap)
                .map(Material::matchMaterial)
                .flatMap(Optional::ofNullable)
               .orElseThrow(() -> new IllegalArgumentException(
                        "invalid or missing material in " + this + ": " + serializedMap
                ));
        this.aliases = findStringList(ALIASES_PATH, serializedMap).orElseGet(ArrayList::new);
        setBuyCost(find(Double.class, BUY_COST_PATH, serializedMap).orElse(NOT_BUYABLE));
        setSellWorth(find(Double.class, SELL_WORTH_PATH, serializedMap).orElse(NOT_SELLABLE));
        setDiscountedPrice(find(Double.class, DISCOUNTED_PRICE_PATH, serializedMap).orElse(NOT_DISCOUNTABLE));
    }

    @SuppressWarnings("unchecked")
    protected <T> Optional<T> find(Class<? extends T> type, String key, Map<String, Object> serializedMap) {
        Object value = serializedMap.get(key);
        if (value == null) {
            return Optional.empty();
        } else {
            if (type.isAssignableFrom(value.getClass())) {
                return Optional.of((T) value);
            } else {
                LOGGER.warn("Invalid value '{}' for key '{}' in item {} (expected {}) - assuming default.",
                        value, key, this, type);
                return Optional.empty();
            }
        }
    }

    protected <T> Optional<List<String>> findStringList(String key, Map<String, Object> serializedMap) {
        return find(List.class, key, serializedMap)
                .map(list -> ((List<?>) list).stream().map(String::valueOf).collect(Collectors.toList()));
    }

    @Override
    public String getDisplayName() {
        if (aliases.isEmpty()) {
            return getSerializationName();
        } else {
            return aliases.get(0);
        }
    }

    @Override
    public abstract ItemStack toItemStack(int amount);

    @Override
    public abstract boolean matches(ItemStack stack);

    @Override
    public boolean canBeSold() {
        return sellWorth != NOT_SELLABLE;
    }

    @Override
    public boolean canBeBought() {
        return buyCost != NOT_BUYABLE;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public List<String> getAliases() {
        return ImmutableList.copyOf(aliases);
    }

    @Override
    public void addAlias(String alias) {
        Preconditions.checkNotNull(alias, "alias");
        Preconditions.checkState(aliases != null, "null aliases collection for this item?!");

        aliases.add(alias);
    }

    @Override
    public void removeAlias(String toRemove) {
        Preconditions.checkNotNull(toRemove, "toRemove");
        Preconditions.checkState(aliases != null, "null aliases collection for this item?!");
        aliases.removeIf(toRemove::equalsIgnoreCase);
    }

    @Override
    public String removeAlias(int removeIndex) throws IndexOutOfBoundsException {
        Preconditions.checkState(aliases != null, "null aliases collection for this item?!");
        return aliases.remove(removeIndex);
    }

    @Override
    public void setDisplayName(String newDisplayName) {
        Preconditions.checkNotNull(newDisplayName, "newDisplayName");
        Preconditions.checkState(aliases != null, "null aliases collection for this item?!");
        aliases.add(0, newDisplayName);
    }

    @Override
    public double getBuyCost() {
        return buyCost;
    }

    @Override
    public void setBuyCost(double buyCost) {
        if (buyCost != NOT_BUYABLE) {
            Preconditions.checkArgument(Double.isFinite(buyCost) && buyCost > sellWorth,
                    "buyCost must be a positive finite number greater than the sell worth");
        }
        this.buyCost = buyCost;
    }

    @Override
    public double getSellWorth() {
        return sellWorth;
    }

    @Override
    public void setSellWorth(double sellWorth) {
        if (sellWorth != NOT_SELLABLE) {
            Preconditions.checkArgument((buyCost == NOT_BUYABLE || sellWorth < buyCost) &&
                            (Double.isFinite(sellWorth) && sellWorth > 0),
                    "sellWorth must be a finite positive number less than the buy cost");
            Preconditions.checkArgument(!isDiscountable() || sellWorth < discountedPrice,
                    "sellWorth must be less than the discounted price");
        }
        this.sellWorth = sellWorth;
    }

    @Override
    public double getDiscountedPrice() {
        return discountedPrice;
    }

    @Override
    public void setDiscountedPrice(double discountedPrice) {
        if (discountedPrice != NOT_DISCOUNTABLE) {
            Preconditions.checkArgument(discountedPrice < buyCost, "discounted price must be less than buy cost (item must be buyable)");
            Preconditions.checkArgument(discountedPrice > sellWorth, "discounted price must be greater than sell worth");
        }
        this.discountedPrice = discountedPrice;
        calculateDiscountPercentage();
    }

    @Override
    public int getDiscountPercentage() {
        return discountPercentage; //cache, updated with discounted price
    }

    @Override
    public boolean isDiscountable() {
        return discountedPrice != NOT_DISCOUNTABLE && canBeBought();
    }

    @Override
    public ConfigurationSection serializeToSection(ConfigurationSection parent) {
        return parent.createSection(
                getSerializationName(), serialize()
        );
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(MATERIAL_PATH, material.name());
        result.put(ALIASES_PATH, aliases);
        result.put(BUY_COST_PATH, buyCost);
        result.put(SELL_WORTH_PATH, sellWorth);
        result.put(DISCOUNTED_PRICE_PATH, discountedPrice);
        return result;
    }

    private void calculateDiscountPercentage() {
        if (!isDiscountable()) {
            this.discountPercentage = 0;
        } else {
            double difference = buyCost - discountedPrice;
            double diffPercentageFactor = difference / buyCost;
            this.discountPercentage = (int) Math.round(diffPercentageFactor * 100D);
        }
    }

    @Override
    public String toString() {
        return "AbstractShopItem{" +
                "material=" + material +
                ", aliases=" + aliases +
                ", buyCost=" + buyCost +
                ", sellWorth=" + sellWorth +
                ", discountedPrice=" + discountedPrice +
                '}';
    }
}
