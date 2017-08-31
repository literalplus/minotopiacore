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

package li.l1t.mtc.module.shop.api;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Represents an abstract item that may be offered for sale (or bought from players) by the Shop module. Natively
 * supports being saved to a YAML configuration. Implementations must provide the {@link ConfigurationSerializable}
 * deserialisation constructor with a Map&lt;String, Object&gt; constructor.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-16
 */
public interface ShopItem extends ConfigurationSerializable {
    /**
     * Magic sell worth value that indicates that the item cannot be sold.
     *
     * @see #canBeSold()
     */
    double NOT_SELLABLE = 0D;
    /**
     * Magic sell worth value that indicates that the item cannot be bought.
     *
     * @see #canBeBought()
     */
    double NOT_BUYABLE = 0D;
    /**
     * Magic discounted price value that indicated that the item cannot be discounted.
     *
     * @see #isDiscountable()
     */
    double NOT_DISCOUNTABLE = 0D;

    /**
     * @return a unique name for this item which can be used to save it in serialization
     */
    String getSerializationName();

    /**
     * @return this item's material definition
     */
    Material getMaterial();

    /**
     * @return the immutable list of this item's alternative names ("aliases")
     */
    List<String> getAliases();

    /**
     * @return the human-readable display name of this item to be used in user output
     */
    String getDisplayName();

    /**
     * Sets this item's display name and keeps the old display name as an alias.
     *
     * @param newDisplayName the new display name
     */
    void setDisplayName(String newDisplayName);

    /**
     * Creates an item stack from this item.
     *
     * @param amount the amount of items in the stack
     * @return an item stack for this item
     */
    ItemStack toItemStack(int amount);

    /**
     * Checks if an item stack matches this shop item. Note that this might not be consistent with
     * the item returned by the item manager for that stack since wildcard items match all data
     * values and don't care about overriding specific values here. Use with care.
     *
     * @param stack the stack to check
     * @return whether this item matches given stack
     * @deprecated not consistent with {@link ShopItemManager}
     */
    @Deprecated
    boolean matches(ItemStack stack);

    /**
     * Adds an alias to this item, silently failing if such an alias already exists, ignoring case.
     *
     * @param alias the alias to add
     */
    void addAlias(String alias);

    /**
     * Removes an alias from this item, ignoring case.
     *
     * @param toRemove the alias to remove
     */
    void removeAlias(String toRemove);

    /**
     * Removes an alias from this item by index.
     *
     * @param removeIndex the index to remove from
     * @return the removed alias
     * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= getAliases().size())
     */
    String removeAlias(int removeIndex) throws IndexOutOfBoundsException;

    /**
     * Gets the amount of virtual money that players have to pay in order to be sold this item. Note
     * that this is just a base value and item managers may impose additional factors onto this
     * value.
     *
     * @return this item's buy cost
     */
    double getBuyCost();

    /**
     * @return whether players are allowed to buy this item from the shop
     */
    boolean canBeBought();

    /**
     * Sets the amount of virtual money that players have to pay in order to be sold this item. A
     * value of zero disables purchase of this item. Note that this is the base cost and actual
     * prices may differ due to additional factors imposed by the item manager, such as discounts.
     *
     * @param buyCost the new buy cost to set
     * @throws IllegalArgumentException if the new buy cost is not a positive finite number greater than the sell worth
     */
    void setBuyCost(double buyCost);

    /**
     * Gets the amount of virtual money that players get upon selling this item to the shop. Note
     * that this is just a base value and item managers may impose additional factors onto this
     * value.
     *
     * @return this item's sell worth
     */
    double getSellWorth();

    /**
     * @return whether players are allowed to sell this item to the shop
     */
    boolean canBeSold();

    /**
     * Sets the amount of virtual money that players get upon selling this item. A value of zero
     * indicates that this item cannot be sold by players. Note that this is the base cost and
     * actual prices may differ due to additional factors imposed by the item manager, such as
     * discounts.
     *
     * @param sellWorth the new sell worth to set
     * @throws IllegalArgumentException if the new buy cost is not a finite positive number less than the buy cost
     */
    void setSellWorth(double sellWorth);

    /**
     * Gets the discounted price of this item, meaning its buy cost when discounted. If the item
     * cannot be discounted, this returns a value of {@link #NOT_DISCOUNTABLE}.
     *
     * @return the discounted price
     */
    double getDiscountedPrice();

    /**
     * Returns whether this item can be discounted to a buy cost of {@link #getDiscountedPrice()}.
     * Note that if an item cannot be bought, it cannot be discounted either.
     *
     * @return whether this item can be discounted
     */
    boolean isDiscountable();

    /**
     * Gets the percentage by which this item is discounted if discounted, or zero otherwise.
     *
     * @return the discount percentage
     */
    int getDiscountPercentage();

    /**
     * Sets the discounted price for this item, that is, this item's buy cost when discounted. Note
     * that discounts are managed by the item manager and this does not have any impact whatsoever
     * on the buy cost returned by this item itself.
     *
     * @param discountedPrice the item's buy cost when discounted
     * @throws IllegalArgumentException if the discounted price is not between the buy cost and the sell worth
     */
    void setDiscountedPrice(double discountedPrice);

    /**
     * Serializes this item to a configuration section such that it can be reconstructed to an equivalent state.
     * The serialized data will be written to a child section of the passed parent section named exactly {@link
     * #getSerializationName()}.
     *
     * @param parent the configuration section to write the data to
     * @return a section representing this item
     * @deprecated incompatible with Bukkit's native configuration serialization
     */
    @Deprecated
    ConfigurationSection serializeToSection(ConfigurationSection parent);
}
