/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop;

import com.google.common.base.Preconditions;
import io.github.xxyy.common.util.inventory.ItemStackFactory;
import io.github.xxyy.mtc.module.shop.api.ShopItemManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

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
    public static final String DISCOUNTED_PRICE_PATH = "discounted_price";

    private final ShopItemManager manager;
    private final Material material;
    private final byte dataValue;
    private final List<String> aliases;
    private double buyCost;
    private double sellWorth;
    private double discountedPrice;
    private int discountPercentage; //does not get serialized - internal cache value

    public ShopItem(ShopItemManager manager, double buyCost, double sellWorth, Material material, byte dataValue,
                    List<String> aliases, double discountedPrice) {
        this.manager = manager;
        Preconditions.checkNotNull(material, "material");
        Preconditions.checkNotNull(aliases, "aliases");
        Preconditions.checkArgument(buyCost >= 0, "buyCost must be greater than or equal to 0");
        Preconditions.checkArgument(sellWorth >= 0, "sellWorth must be greater than or equal to 0");
        Preconditions.checkArgument(discountedPrice >= 0, "discountedPrice must be greater than or equal to 0");
        Preconditions.checkArgument(dataValue >= -1, "dataValue must be greater than or equal to -1");
        this.buyCost = buyCost;
        this.sellWorth = sellWorth;
        this.material = material;
        this.dataValue = dataValue;
        this.aliases = aliases;
        setDiscountedPrice(discountedPrice); //updates percentage cache, checks < buyCost
    }

    /**
     * Attempts to deserialize an item from a configuration section. This method is fail-fast and does not support
     * custom validation, so expect exceptions for data which has not been created by
     * {@link #serializeToSection(ConfigurationSection)}. Because of this, the format is not specified more in depth.
     *
     * @param section the section to read from
     * @param manager the manager managing the shiny new item
     * @return an item with the serialized properties
     * @throws NumberFormatException if the data value is not a byte
     * @throws ClassCastException    if anything else is not parsable
     */
    public static ShopItem deserialize(ConfigurationSection section, ShopItemManager manager)
            throws NumberFormatException, ClassCastException {
        Preconditions.checkNotNull(section, "section");
        String[] arr = section.getName().split(":");
        String materialName = arr[0];
        byte dataValue = arr.length > 1 ? Byte.parseByte(arr[1]) : -1;

        List<String> aliases = section.getStringList(ALIASES_PATH);
        double cost = section.getDouble(BUY_COST_PATH);
        double worth = section.getDouble(SELL_WORTH_PATH);
        double discountedPrice = section.getDouble(DISCOUNTED_PRICE_PATH, 0); //explicit default value -> not specified means no discount

        return new ShopItem(manager, cost, worth, Material.getMaterial(materialName), dataValue, aliases, discountedPrice);
    }

    /**
     * @return the human-readable display name of this item to be used in user output
     */
    public String getDisplayName() {
        if (aliases.isEmpty()) {
            String readableMaterialName = WordUtils.capitalizeFully(material.name().replace('_', ' '));
            return dataValue < 0 ? (readableMaterialName + ":" + dataValue) : readableMaterialName;
        } else {
            return aliases.get(0);
        }
    }

    /**
     * Creates an item stack from this item.
     *
     * @param amount the amount of items in the stack
     * @return an item stack for this item
     */
    @SuppressWarnings("deprecation")
    public ItemStack toItemStack(int amount) {
        return new ItemStackFactory(material)
                .amount(amount)
                .legacyData(dataValue)
                .produce();
    }

    /**
     * Checks if an item stack matches this shop item.
     *
     * @param stack the stack to check
     * @return whether this item matches given stack
     */
    @SuppressWarnings({"deprecation", "SimplifiableIfStatement"})
    public boolean matches(ItemStack stack) {
        Preconditions.checkNotNull(stack, "stack");
        if (dataValue != -1 &&
                stack.getData().getData() != dataValue) {
            return false;
        }

        return material.equals(stack.getType());
    }

    /**
     * @return whether players are allowed to sell this item to the shop
     */
    public boolean canBeSold() {
        return sellWorth > 0;
    }

    /**
     * @return whether players are allowed to buy this item from the shop
     */
    public boolean canBeBought() {
        return buyCost > 0;
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
     * @return the modifiable list of this item's alternative names ("aliases")
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * Gets the amount of virtual money that players have to pay in order to be sold this item. Note that this is just
     * a base value and item managers may impose additional factors onto this value.
     *
     * @return this item's buy cost
     */
    public double getBuyCost() {
        return buyCost;
    }

    /**
     * Sets the amount of virtual money that players have to pay in order to be sold this item.
     * A value of zero disables purchase of this item. Note that this is the base cost and actual prices may differ
     * due to additional factors imposed by the item manager, such as discounts.
     *
     * @param buyCost the new buy cost to set
     * @throws IllegalArgumentException if the new buy cost is not a finite number or greater than the sell worth
     */
    public void setBuyCost(double buyCost) {
        Preconditions.checkArgument(Double.isFinite(buyCost) && buyCost > sellWorth,
                "buyCost must be a finite number greater than the sell worth");
        this.buyCost = buyCost;
    }

    /**
     * Gets the amount of virtual money that players get upon selling this item to the shop. Note that this is just
     * a base value and item managers may impose additional factors onto this value.
     *
     * @return this item's sell worth
     */
    public double getSellWorth() {
        return sellWorth;
    }

    /**
     * Sets the amount of virtual money that players get upon selling this item.
     * A value of zero indicates that this item cannot be sold by players. Note that this is the base cost and actual
     * prices may differ due to additional factors imposed by the item manager, such as discounts.
     *
     * @param sellWorth the new sell worth to set
     * @throws IllegalArgumentException if the new buy cost is not a finite number or less than the buy cost
     */
    public void setSellWorth(double sellWorth) {
        Preconditions.checkArgument(Double.isFinite(sellWorth) && sellWorth < buyCost,
                "sellWorth must be a finite number greater than the buy cost");
        this.sellWorth = sellWorth;
    }

    /**
     * Gets the discounted price of this item, meaning its buy cost when discounted. If the item cannot be
     * discounted, this returns a value of zero.
     *
     * @return the discounted price
     */
    public double getDiscountedPrice() {
        return discountedPrice;
    }

    /**
     * Sets the discounted price for this item, that is, this item's buy cost when discounted. Note that discounts are
     * managed by the item manager and this does not have any impact whatsoever on the buy cost returned by this item
     * itself.
     *
     * @param discountedPrice the item's buy cost when discounted
     * @throws IllegalArgumentException if the discounted price is not between the buy cost and the sell worth
     */
    public void setDiscountedPrice(double discountedPrice) {
        if (discountedPrice != 0) {
            Preconditions.checkArgument(discountedPrice < buyCost, "discounted price must be less than buy cost (item must be buyable)");
            Preconditions.checkArgument(discountedPrice > sellWorth, "discounted price must be greater than sell worth");
        }
        this.discountedPrice = discountedPrice;
        calculateDiscountPercentage();
    }

    /**
     * Gets the percentage by which this item is discounted if discounted, or zero otherwise.
     *
     * @return the discount percentage
     */
    public int getDiscountPercentage() {
        return discountPercentage; //cache, updated with discounted price
    }

    /**
     * Returns whether this item can be discounted to a buy cost of {@link #getDiscountedPrice()}. Note that if an item
     * cannot be bought, it cannot be discounted either.
     *
     * @return whether this item can be discounted
     */
    public boolean isDiscountable() {
        return discountedPrice > 0 && canBeBought();
    }

    /**
     * @return a unique name for this item which can be used to save it in serialization
     */
    public String getSerializationName() {
        return dataValue >= 0 ? (material.name() + ":" + dataValue) : material.name();
    }

    /**
     * @return the item manager managing this item
     */
    public ShopItemManager getManager() {
        return manager;
    }

    /**
     * Serializes this item to a configuration section such that it can be reconstructed to an equivalent state using
     * {@link #deserialize(ConfigurationSection, ShopItemManager)}. The serialized data will be written to a child
     * section of the passed parent section named exactly {@link #getSerializationName()}.
     *
     * @param parent the configuration section to write the data to
     * @return a section representing this item
     */
    public ConfigurationSection serializeToSection(ConfigurationSection parent) {
        ConfigurationSection result = parent.createSection(getSerializationName());
        result.set(ALIASES_PATH, aliases);
        result.set(BUY_COST_PATH, buyCost);
        result.set(SELL_WORTH_PATH, sellWorth);
        if (isDiscountable()) { //don't save discounted price if we don't allow discounts - this must be clearly recognisable as optional
            result.set(DISCOUNTED_PRICE_PATH, discountedPrice);
        }
        return result;
    }

    private void calculateDiscountPercentage() { //calculates by what percentage the item is currently reduced and caches that
        if (!isDiscountable()) {
            discountPercentage = 0;
        }
        double difference = buyCost - discountedPrice;
        double diffPercentage = difference / buyCost;
        this.discountPercentage = (int) Math.round(diffPercentage * 100D);
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
                ", discountedPrice=" + discountedPrice +
                '}'; //IntelliJ says that this is "at least as efficient or more efficient" than StringBuilder
    }
}
