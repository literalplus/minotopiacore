/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * Represents an item which can be bought in the MTC admin shop.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 19/01/15
 */
public class ShopItem {
    public static final String ALIASES_PATH = "aliases";
    public static final String BUY_COST_PATH = "buy";
    public static final String SELL_WORTH_PATH = "sell";

    private final Material material;
    private final byte dataValue;
    private final String serialisationName;
    private final List<String> aliases;
    private int buyCost;
    private int sellWorth;

    protected ShopItem(int buyCost, int sellWorth, Material material, byte dataValue, List<String> aliases) {
        Validate.notNull(material, "material");
        Validate.notNull(aliases, "aliases");
        Validate.isTrue(buyCost > 0, "buyCost must be greater than 0");
        Validate.isTrue(sellWorth > 0, "sellWorth must be greater than 0");
        this.buyCost = buyCost;
        this.sellWorth = sellWorth;
        this.material = material;
        this.dataValue = dataValue;
        this.aliases = aliases;

        this.serialisationName = dataValue >= 0 ? (material.name() + ":" + dataValue) : material.name();
    }

    /**
     * @return this item's material definition
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * @return the item's data value, -1 means any
     */
    public byte getDataValue() {
        return dataValue;
    }

    /**
     * @return an immutable view of this item's alternative names ("aliases")
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * @return the amount of virtual money that players have to pay in order to be sold this item
     */
    public int getBuyCost() {
        return buyCost;
    }

    /**
     * Sets the amount of virtual money that players have to pay in order to be sold this item.
     *
     * @param buyCost the cost of this item
     */
    public void setBuyCost(int buyCost) {
        this.buyCost = buyCost;
    }

    /**
     * @return the amount of virtual money that players get upon selling this item
     */
    public int getSellWorth() {
        return sellWorth;
    }

    /**
     * Sets the amount of virtual money that players get upon selling this item.
     *
     * @param sellWorth the worth of this item
     */
    public void setSellWorth(int sellWorth) {
        this.sellWorth = sellWorth;
    }

    /**
     * Serializes this item to a configuration section such that it can be reconstructed to an equivalent state using
     * {@link #deserialize(ConfigurationSection)}. The serialized data will be written to a child section of the passed
     * parent section named exactly {@link #getSerializationName()}.
     *
     * @param parent the configuration section to write the data to
     * @return a section representing this item
     */
    public ConfigurationSection serializeToSection(ConfigurationSection parent) {
        ConfigurationSection result = parent.createSection(getSerializationName());
        result.set(ALIASES_PATH, aliases);
        result.set(BUY_COST_PATH, buyCost);
        result.set(SELL_WORTH_PATH, sellWorth);
        return result;
    }

    /**
     * @return a name which uniquely represents this item which can be used to save it in serialization
     */
    public String getSerializationName() {
        return serialisationName;
    }

    /**
     * Attempts to deserialize an item from a configuration section. This method is fail-fast and does not support
     * custom validation, so expect exceptions for data which has not been return exactly like that by
     * {@link #serializeToSection(ConfigurationSection)}. Because of this, the format is not specified more in depth.
     *
     * @param section the section to read from
     * @return an item with the serialized properties
     * @throws NumberFormatException if the data value is not a byte
     * @throws ClassCastException    if anything else is not parsable
     */
    public static ShopItem deserialize(ConfigurationSection section) throws NumberFormatException, ClassCastException {
        String[] arr = section.getName().split(":");
        String materialName = arr[0];
        byte dataValue = arr.length > 1 ? Byte.parseByte(arr[1]) : -1;

        List<String> aliases = section.getStringList(ALIASES_PATH);
        int cost = section.getInt(BUY_COST_PATH);
        int worth = section.getInt(SELL_WORTH_PATH);

        return new ShopItem(cost, worth, Material.getMaterial(materialName), dataValue, aliases);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ShopItem)) {
            return false;
        }

        ShopItem shopItem = (ShopItem) o;

        return dataValue == shopItem.dataValue && material == shopItem.material;
    }

    @Override
    public int hashCode() {
        int result = material.hashCode();
        result = 31 * result + (int) dataValue;
        return result;
    }

    @Override
    public String toString() {
        return "ShopItem{" +
                "material=" + material +
                ", dataValue=" + dataValue +
                ", aliases=" + aliases +
                ", buyCost=" + buyCost +
                ", sellWorth=" + sellWorth +
                '}'; //IntelliJ says that this is "at least as efficient or more efficient" than StringBuilder
    }


}
