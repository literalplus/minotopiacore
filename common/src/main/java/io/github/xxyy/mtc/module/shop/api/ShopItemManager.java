package io.github.xxyy.mtc.module.shop.api;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.xxyy.mtc.api.misc.Cache;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.manager.DiscountManager;

import java.util.List;
import java.util.Map;

/**
 * Manages shop items for a shop module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 29/10/15
 */
public interface ShopItemManager extends Cache {
    /**
     * Attempts to get an item by its name. This respects any aliases which may have been set. Furthermore, this allows
     * for items to be queried by their material name. Spaces are replaced by underscores and the whole string is
     * converted to upper case to match material name declarations.
     *
     * @param input the string to check for, may be any of alias, material name or type id, optionally
     *              followed by :&lt;data value&gt;
     * @return the found item or null if there is no such item
     */
    ShopItem getItem(String input);

    /**
     * Attempts to get an item managed by this manager matching given item stack. Returns null if the stack is null,
     * the stack is of {@link Material#AIR} or {@link #isTradeProhibited(ItemStack) trading of that stack is prohibited}.
     *
     * @param stack the stack to find the item for
     * @return the found item for that stack, or {@code null} otherwise.
     */
    ShopItem getItem(ItemStack stack);

    /**
     * Attempts to get an item managed by this manager matching given input string. If the input string is
     * {@code "hand"}, the player's item in hand is requested. Otherwise, the same syntax as in
     * {@link #getItem(String)} is used.
     *
     * @param plr   the player requesting the item
     * @param input the input string
     * @return the found item or null if there is no such item
     */
    ShopItem getItem(Player plr, String input);

    /**
     * Attempts to get an item managed by this manager. The special data value {@code -1} represents a catch-all
     * wildcard item that matches all data values of that material that do not have a specific item attached to them.
     *
     * @param material  the material to find the item for
     * @param dataValue the data value to find the item for
     * @return the found item for the specific data value, if found, the wildcard item, if found, or {@code null} otherwise.
     */
    ShopItem getItem(Material material, short dataValue);

    /**
     * Gets the wildcard item for a given material, if any. A wildcard item matches all data values and is represented
     * internally by the special -1 data value.
     *
     * @param material the material to match
     * @return the wildcard item for given material, or null if there is none
     */
    ShopItem getWildcardItem(Material material);

    /**
     * Checks if trading given item stack is prohibited due to general restrictions not relating to the shop item itself.
     *
     * @param stack the stack to check
     * @return whether trading of given item stack must be prohibited
     */
    boolean isTradeProhibited(ItemStack stack); //TODO: Take shop item and forward call

    /**
     * @return the aliases map, mapping each alias to a shop item
     */
    Map<String, ShopItem> getItemAliases();

    /**
     * Gets this manager's discount manager. The discount manager is responsible for selecting items for discount and
     * calculation discounted prices.
     *
     * @return the discount manager
     */
    DiscountManager getDiscountManager();

    /**
     * Calculates the actual buy cost for a shop item managed by this manager. This takes into account additional
     * factors imposed by this manager such as discounts.
     *
     * @param shopItem the shop item to calculate the final cost for
     * @return the actual buy cost for that item
     */
    double getBuyCost(ShopItem shopItem);

    /**
     * Calculates the actual sell worth for a shop item managed by this manager. This takes into account additional
     * factors imposed by this manager.
     *
     * @param shopItem the shop item to calculate the final worth for
     * @return the actual sell worth for that item
     */
    double getSellWorth(ShopItem shopItem);

    /**
     * @return a list of all configured shop items
     */
    List<ShopItem> getItems();
}
