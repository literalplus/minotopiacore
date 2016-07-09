/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import io.github.xxyy.mtc.module.shop.api.ShopItemManager;

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

    /**
     * Magic sell worth value that indicates that the item cannot be sold.
     *
     * @see #canBeSold()
     */
    public static final double NOT_SELLABLE = 0D;

    /**
     * Magic sell worth value that indicates that the item cannot be bought.
     *
     * @see #canBeBought()
     */
    public static final double NOT_BUYABLE = 0D;

    /**
     * Magic discounted price value that indicated that the item cannot be discounted.
     *
     * @see #isDiscountable()
     */
    public static final double NOT_DISCOUNTABLE = 0D;

    /**
     * Magic data value that indicates that this item matches all data values that do not have specific items defined.
     */
    public static final short WILDCARD_DATA_VALUE = -1;

    private final ShopItemManager manager;
    private final Material material;
    private final short dataValue;
    private final List<String> aliases;
    private double buyCost;
    private double sellWorth;
    private double discountedPrice;
    private int discountPercentage; //does not get serialized - internal cache value

    public ShopItem(ShopItemManager manager, double buyCost, double sellWorth, Material material,
                    short dataValue, List<String> aliases, double discountedPrice) {
        Preconditions.checkNotNull(material, "material");
        Preconditions.checkNotNull(aliases, "aliases");
        Preconditions.checkArgument(buyCost >= 0, "buyCost must be greater than or equal to 0");
        Preconditions.checkArgument(sellWorth >= 0, "sellWorth must be greater than or equal to 0");
        Preconditions.checkArgument(discountedPrice >= 0, "discountedPrice must be greater than or equal to 0");
        Preconditions.checkArgument(dataValue >= -1, "dataValue must be greater than or equal to -1");

        this.manager = manager;
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
     * @throws NumberFormatException if the data value is not a short
     * @throws ClassCastException    if anything else is not parsable
     */
    public static ShopItem deserialize(ConfigurationSection section, ShopItemManager manager)
            throws NumberFormatException, ClassCastException {
        Preconditions.checkNotNull(section, "section");

        String[] arr = section.getName().split(":");
        String materialName = arr[0];
        short dataValue = arr.length > 1 ? Short.parseShort(arr[1]) : WILDCARD_DATA_VALUE;

        List<String> aliases = section.getStringList(ALIASES_PATH);
        double cost = section.getDouble(BUY_COST_PATH);
        double worth = section.getDouble(SELL_WORTH_PATH);
        //explicit default value -> not specified means no discount
        double discountedPrice = section.getDouble(DISCOUNTED_PRICE_PATH, NOT_DISCOUNTABLE);

        Material material = Material.getMaterial(materialName);
        Preconditions.checkNotNull(material, "No such material '%s'", materialName);

        return new ShopItem(manager, cost, worth, material, dataValue, aliases, discountedPrice);
    }

    /**
     * @return the human-readable display name of this item to be used in user output
     */
    public String getDisplayName() {
        if (aliases.isEmpty()) {
            String readableMaterialName = WordUtils.capitalizeFully(material.name().replace('_', ' '));
            return dataValue == WILDCARD_DATA_VALUE ? readableMaterialName : (readableMaterialName + ":" + dataValue);
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
        ItemStack stack = new ItemStack(material, amount);

        if (dataValue != WILDCARD_DATA_VALUE) {
            stack.setDurability(dataValue);
        }

        return stack;
    }

    /**
     * Checks if an item stack matches this shop item. Note that this might not be consistent with the item returned by
     * the item manager for that stack since wildcard items match all data values and don't care about overriding
     * specific values here. Use with care.
     *
     * @param stack the stack to check
     * @return whether this item matches given stack
     * @deprecated not consistent with {@link ShopItemManager}
     */
    @SuppressWarnings({"deprecation", "SimplifiableIfStatement"})
    @Deprecated
    public boolean matches(ItemStack stack) {
        Preconditions.checkNotNull(stack, "stack");

        if (dataValue != WILDCARD_DATA_VALUE &&
                stack.getDurability() != dataValue) {
            return false;
        }

        return material.equals(stack.getType());
    }

    /**
     * @return whether players are allowed to sell this item to the shop
     */
    public boolean canBeSold() {
        return sellWorth != NOT_SELLABLE;
    }

    /**
     * @return whether players are allowed to buy this item from the shop
     */
    public boolean canBeBought() {
        return buyCost != NOT_BUYABLE;
    }

    /**
     * @return this item's material definition
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Gets the item's data value. The data value is {@link ItemStack#getDurability()}.
     *
     * @return the item's data value, {@link #WILDCARD_DATA_VALUE} means any
     */
    public short getDataValue() {
        return dataValue;
    }

    /**
     * @return the immutable list of this item's alternative names ("aliases")
     */
    public List<String> getAliases() {
        return ImmutableList.copyOf(aliases);
    }

    /**
     * Adds an alias to this item, silently failing if such an alias already exists, ignoring case.
     *
     * @param alias the alias to add
     */
    public void addAlias(String alias) { //TODO: case-insensitive aliases
        Preconditions.checkNotNull(alias, "alias");
        Preconditions.checkState(aliases != null, "null aliases collection for this item?!");

        aliases.add(alias);
    }

    /**
     * Removes an alias from this item, ignoring case.
     *
     * @param toRemove the alias to remove
     */
    public void removeAlias(String toRemove) {
        Preconditions.checkNotNull(toRemove, "toRemove");
        Preconditions.checkState(aliases != null, "null aliases collection for this item?!");

        String toRemoveLower = toRemove.toLowerCase();
        aliases.removeIf(toRemoveLower::equalsIgnoreCase);
    }

    /**
     * Removes an alias from this item by index.
     *
     * @param removeIndex the index to remove from
     * @return the removed alias
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= getAliases().size())
     */
    public String removeAlias(int removeIndex) throws IndexOutOfBoundsException {
        Preconditions.checkState(aliases != null, "null aliases collection for this item?!");

        return aliases.remove(removeIndex);
    }

    /**
     * Sets this item's display name and keeps the old display name as an alias.
     *
     * @param newDisplayName the new display name
     * @implNote This implementation just adds the new display name as first alias
     */
    public void setDisplayName(String newDisplayName) {
        Preconditions.checkNotNull(newDisplayName, "newDisplayName");
        Preconditions.checkState(aliases != null, "null aliases collection for this item?!");

        aliases.add(0, newDisplayName);
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
     * @throws IllegalArgumentException if the new buy cost is not a positive finite number greater than the sell worth
     */
    public void setBuyCost(double buyCost) {
        if (buyCost != NOT_BUYABLE) {
            Preconditions.checkArgument(Double.isFinite(buyCost) && buyCost > sellWorth,
                    "buyCost must be a positive finite number greater than the sell worth");
        }

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
     * @throws IllegalArgumentException if the new buy cost is not a finite positive number less than the buy cost
     */
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

    /**
     * Gets the discounted price of this item, meaning its buy cost when discounted. If the item cannot be
     * discounted, this returns a value of {@link #NOT_DISCOUNTABLE}.
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
        if (discountedPrice != NOT_DISCOUNTABLE) {
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
        return discountedPrice != NOT_DISCOUNTABLE && canBeBought();
    }

    /**
     * @return a unique name for this item which can be used to save it in serialization
     */
    public String getSerializationName() {
        return dataValue == WILDCARD_DATA_VALUE ? material.name() : (material.name() + ":" + dataValue);
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
        } else {
            result.set(DISCOUNTED_PRICE_PATH, null); //remove if it was set before
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
