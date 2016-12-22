/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.shop.api;

import li.l1t.mtc.api.misc.Cache;
import li.l1t.mtc.module.shop.manager.DiscountManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Manages shop items for a shop module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 29/10/15
 */
public interface ShopItemManager extends Cache {
    /**
     * Attempts to get an item by its name. This respects any aliases which may have been set.
     * Furthermore, this allows for items to be queried by their material name. Spaces are replaced
     * by underscores and the whole string is converted to upper case to match material name
     * declarations.
     *
     * @param input the string to check for, may be any of alias, material name or type id, optionally followed by
     *              :&lt;data value&gt;
     * @return the found item or null if there is no such item
     */
    Optional<? extends ShopItem> getItem(String input);

    /**
     * Attempts to get an item managed by this manager matching given input string. If the input
     * string is {@code "hand"}, the player's item in hand is requested. Otherwise, the same syntax
     * as in {@link #getItem(String)} is used.
     *
     * @param plr   the player requesting the item
     * @param input the input string
     * @return the found item or null if there is no such item
     */
    Optional<? extends ShopItem> getItem(Player plr, String input);

    /**
     * Attempts to get an item managed by this manager matching given item stack. Returns null if
     * the stack is null, the stack is of {@link Material#AIR} or {@link
     * #isTradeProhibited(ItemStack) trading of that stack is prohibited}.
     *
     * @param stack the stack to find the item for
     * @return the found item for that stack, or {@code null} otherwise.
     */
    Optional<? extends ShopItem> getItem(ItemStack stack);

    /**
     * Creates a new shop item with its identity taken from given item stack and all other values at their defaults.
     *
     * @param stack      the stack to use as identity source for the item
     * @param parameters the additional parameters to provide to the factory
     * @return the newly created item
     * @throws IllegalArgumentException if an item with that identity already exists
     */
    ShopItem createItem(ItemStack stack, String... parameters);

    /**
     * Registers an item with this manager's cache. Note that it is imperative to unregister an item before changing it
     * since
     *
     * @param item the item to register
     */
    <T extends ShopItem> void registerItem(T item);

    /**
     * Unregisters an item from this manager's cache.
     *
     * @param item the item to unregister
     */
    <T extends ShopItem> void unregisterItem(T item);

    /**
     * Checks if trading given item stack is prohibited due to general restrictions not relating to
     * the shop item itself.
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
     * Gets this manager's discount manager. The discount manager is responsible for selecting items
     * for discount and calculation discounted prices.
     *
     * @return the discount manager
     */
    DiscountManager getDiscountManager();

    /**
     * Calculates the actual buy cost for a shop item managed by this manager. This takes into
     * account additional factors imposed by this manager such as discounts.
     *
     * @param shopItem the shop item to calculate the final cost for
     * @return the actual buy cost for that item
     */
    double getBuyCost(ShopItem shopItem);

    /**
     * Calculates the actual sell worth for a shop item managed by this manager. This takes into
     * account additional factors imposed by this manager.
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
