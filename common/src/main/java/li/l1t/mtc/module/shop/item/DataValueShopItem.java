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
import li.l1t.common.exception.UserException;
import li.l1t.mtc.module.shop.api.ShopItemManager;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents an item which can be bought in the MTC admin shop.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2015-01-19
 */
@SerializableAs("mtc-shop-item-with-data-value")
public class DataValueShopItem extends AbstractShopItem {
    /**
     * Magic data value that indicates that this item matches all data values that do not have
     * specific items defined.
     */
    public static final short WILDCARD_DATA_VALUE = -1;
    private static final String DATA_VALUE_PATH = "data-value";
    private final short dataValue;

    public DataValueShopItem(double buyCost, double sellWorth, Material material,
                             short dataValue, List<String> aliases, double discountedPrice) {
        super(material, aliases, sellWorth, buyCost, discountedPrice);
        Preconditions.checkArgument(dataValue >= -1, "dataValue must be greater than or equal to -1");
        this.dataValue = dataValue;
    }

    public DataValueShopItem(Map<String, Object> input) {
        super(input);
        this.dataValue = find(Integer.class, DATA_VALUE_PATH, input).map(Integer::shortValue).orElse(WILDCARD_DATA_VALUE);
    }

    /**
     * Attempts to deserialize an item from a configuration section. This method is fail-fast and
     * does not support custom validation, so expect exceptions for data which has not been created
     * by {@link #serializeToSection(ConfigurationSection)}. Because of this, the format is not
     * specified more in depth.
     *
     * @param section the section to read from
     * @param manager the manager managing the shiny new item
     * @return an item with the serialized properties
     * @throws NumberFormatException if the data value is not a short
     * @throws ClassCastException    if anything else is not parsable
     * @deprecated Use {@link #DataValueShopItem(Map)}
     */
    @Deprecated
    public static DataValueShopItem deserialize(ConfigurationSection section, ShopItemManager manager)
            throws NumberFormatException, ClassCastException { //TODO: remove legacy conversion code
        Map<String, Object> map = section.getValues(false);
        String[] split = section.getName().split(":", 2);
        map.put("material", Material.matchMaterial(split[0]).name());
        if(split.length > 1 && StringUtils.isNumeric(split[1])) {
            map.put(DATA_VALUE_PATH, Integer.parseInt(split[1]));
        }
        return new DataValueShopItem(map);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = super.serialize();
        result.put(DATA_VALUE_PATH, dataValue);
        return result;
    }

    public static DataValueShopItem fromItemStack(ItemStack stack, String... parameters) {
        Preconditions.checkNotNull(stack, "stack");
        Preconditions.checkNotNull(parameters, "parameters");
        Material material = stack.getType();
        short dataValue = stack.getDurability();
        for(String parameter : parameters) {
            if(parameter.equalsIgnoreCase("wildcard")) {
                dataValue = WILDCARD_DATA_VALUE;
            } else{
                throw new UserException("Unbekannter Parameter für DataValueShopItem: "+parameter);
            }
        }
        return new DataValueShopItem(
                NOT_BUYABLE, NOT_SELLABLE, material, dataValue, new ArrayList<>(), NOT_DISCOUNTABLE
        );
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack toItemStack(int amount) {
        ItemStack stack = new ItemStack(getMaterial(), amount);

        if (dataValue != WILDCARD_DATA_VALUE) {
            stack.setDurability(dataValue);
        }

        return stack;
    }

    @Override
    @SuppressWarnings({"deprecation", "SimplifiableIfStatement"})
    @Deprecated
    public boolean matches(ItemStack stack) {
        Preconditions.checkNotNull(stack, "stack");

        if (dataValue != WILDCARD_DATA_VALUE &&
                stack.getDurability() != dataValue) {
            return false;
        }

        return getMaterial().equals(stack.getType());
    }

    /**
     * Gets the item's data value. The data value is {@link ItemStack#getDurability()}.
     *
     * @return the item's data value, {@link #WILDCARD_DATA_VALUE} means any
     */
    public short getDataValue() {
        return dataValue;
    }

    public boolean isWildcard() {
        return getDataValue() == WILDCARD_DATA_VALUE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DataValueShopItem)) {
            return false;
        }

        DataValueShopItem shopItem = (DataValueShopItem) o;

        return dataValue == shopItem.dataValue && getMaterial() == shopItem.getMaterial();
    }

    @Override
    public int hashCode() {
        int result = getMaterial().hashCode();
        result = 31 * result + (int) dataValue;
        return result;
    }

    @Override
    public String toString() {
        return "DataValueShopItem{" +
                "[" + super.toString() +
                "] dataValue=" + dataValue +
                '}';
    }

    @Override
    public String getSerializationName() {
        return dataValue == DataValueShopItem.WILDCARD_DATA_VALUE ? getMaterial().name() : (getMaterial().name() + ":" + dataValue);
    }
}
