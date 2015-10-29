package io.github.xxyy.mtc.module.shop.api;

import io.github.xxyy.mtc.misc.CacheHelper;
import io.github.xxyy.mtc.module.shop.ShopItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Manages shop items for a shop module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 29/10/15
 */
public interface ShopItemManager extends CacheHelper.Cache {
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
     * Attempts to get an item managed by this configuration matching given item stack.
     *
     * @param stack the stack to find the item for
     * @return the found item for that stack, or {@code null} otherwise.
     */
    ShopItem getItem(ItemStack stack);

    /**
     * Attempts to get an item managed by this configuration. The special data value {@code -1} represents a catch-all
     * wildcard item that matches all data values of that material that do not have a specific item attached to them.
     *
     * @param material  the material to find the item for
     * @param dataValue the data value to find the item for
     * @return the found item for the specific data value, if found, the wildcard item, if found, or {@code null} otherwise.
     */
    ShopItem getItem(Material material, byte dataValue);

    Map<String, ShopItem> getItemAliases();
}
