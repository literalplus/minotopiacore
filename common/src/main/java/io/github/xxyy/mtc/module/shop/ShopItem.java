/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop;

import com.google.common.base.Preconditions;
import io.github.xxyy.common.util.inventory.ItemStackFactory;
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
    public static final String SALE_REDUCTION_PERCENT = "sale_reduction_percent";

    private final Material material;
    private final byte dataValue;
    private final List<String> aliases;
    private double buyCost;
    private double sellWorth;
    private double saleReductionPercent = .2;
    private transient boolean onSale = false;

    public ShopItem(double buyCost, double sellWorth, Material material, byte dataValue, List<String> aliases, double saleReductionPercent) {
        Preconditions.checkNotNull(material, "material");
        Preconditions.checkNotNull(aliases, "aliases");
        Preconditions.checkArgument(buyCost >= 0, "buyCost must be greater than or equal to 0");
        Preconditions.checkArgument(sellWorth >= 0, "sellWorth must be greater than or equal to 0");
        Preconditions.checkArgument(dataValue >= -1, "dataValue must be greater than or equal to -1");
        this.buyCost = buyCost;
        this.sellWorth = sellWorth;
        this.material = material;
        this.dataValue = dataValue;
        this.aliases = aliases;
        this.saleReductionPercent = saleReductionPercent;
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
        Preconditions.checkNotNull(section, "section");
        String[] arr = section.getName().split(":");
        String materialName = arr[0];
        byte dataValue = arr.length > 1 ? Byte.parseByte(arr[1]) : -1;

        List<String> aliases = section.getStringList(ALIASES_PATH);
        double cost = section.getDouble(BUY_COST_PATH);
        double worth = section.getDouble(SELL_WORTH_PATH);
        double saleBuyReductionPercent = section.getDouble(SALE_REDUCTION_PERCENT);

        return new ShopItem(cost, worth, Material.getMaterial(materialName), dataValue, aliases, saleBuyReductionPercent);
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
    @SuppressWarnings("deprecation")
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
     * @return the amount of virtual money that players have to pay in order to be sold this item
     */
    public double getBuyCost() {
        return buyCost;
    }

    public double getBuyCostSale() {
        return buyCost * (1 - saleReductionPercent);
    }

    /**
     * Returns the final buycost, taking current sale state into account.
     * @return the amount of virtual money that players have to pay in order to be sold this item
     */
    public double getBuyCostFinal() {
        return onSale ? getBuyCostSale() : getBuyCost();
    }

    /**
     * Sets the amount of virtual money that players have to pay in order to be sold this item.
     * A negative or a value of {@code 0} indicates that this item is not buyable.
     *
     * @param buyCost the cost of this item
     * @return Whether the input value was valid and the property got updated
     */
    public boolean setBuyCost(double buyCost) {
        if (!Double.isFinite(buyCost) || buyCost < sellWorth) {
            return false;
        }
        this.buyCost = buyCost;
        return true;
    }

    /**
     * @return the amount of virtual money that players get upon selling this item to the shop
     */
    public double getSellWorth() {
        return sellWorth;
    }

    /**
     * Sets the amount of virtual money that players get upon selling this item.
     * A negative or a value of {@code 0} indicates that this item is not sellable.
     *
     * @param sellWorth the worth of this item
     * @return Whether the input value was valid and the property got updated
     */
    public boolean setSellWorth(double sellWorth) {
        if (!Double.isFinite(sellWorth) || sellWorth > getBuyCostSale()) {
            return false;
        }
        this.sellWorth = sellWorth;
        return true;
    }

    public double getSaleReductionPercent() {
        return saleReductionPercent;
    }

    /**
     * Sets the sale reduction percent.
     * <p>
     * The buyCost of an item currently on sale is: {@code buyCost * (1 - saleReductionPercent)}
     * A value of {@code 0} indicates this item will never be on sale.
     *
     * @param saleReductionPercent the sale reduction, between 0 (inclusive) and 1 (exclusive)
     * @return Whether the input value was valid and the property got updated
     */
    public boolean setSaleReductionPercent(double saleReductionPercent) {
        if(saleReductionPercent < 0 || saleReductionPercent >= 1) {
            return false;
        }
        this.saleReductionPercent = saleReductionPercent;
        return true;
    }

    public boolean isOnSale() {
        return onSale;
    }

    boolean setOnSale(boolean onSale) {
        if (!canBeBought() || !canBeOnSale()) {
            return false;
        }
        this.onSale = onSale;
        return true;
    }

    public boolean canBeOnSale() {
        return saleReductionPercent != 0;
    }

    /**
     * @return a name which uniquely represents this item which can be used to save it in serialization
     */
    public String getSerializationName() {
        return dataValue >= 0 ? (material.name() + ":" + dataValue) : material.name();
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
        result.set(SALE_REDUCTION_PERCENT, saleReductionPercent);
        return result;
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
                ", saleReductionPercent=" + saleReductionPercent +
                '}'; //IntelliJ says that this is "at least as efficient or more efficient" than StringBuilder
    }


}
