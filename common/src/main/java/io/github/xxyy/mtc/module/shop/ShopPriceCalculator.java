package io.github.xxyy.mtc.module.shop;

import com.google.common.base.Preconditions;
import io.github.xxyy.mtc.module.shop.api.ShopItemManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Calculates prices for items according to the associated shop module. Stateless class.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 28/10/15
 */
public class ShopPriceCalculator {
    private final ShopItemManager itemManager;

    public ShopPriceCalculator(ShopItemManager itemManager) {
        this.itemManager = itemManager;
    }

    /**
     * Sums the prices of all items in a player's inventory, excluding armor slots.
     *
     * @param plr  the player whose inventory to survey
     * @param type the transaction type for which to calculate the price
     * @return the sum of item prices
     */
    public double sumInventoryPrices(Player plr, TransactionType type) {
        Preconditions.checkNotNull(plr, "plr");
        return sumPrices(Arrays.asList(plr.getInventory().getContents()), type) /*+
                sumPrices(Arrays.asList(plr.getInventory().getArmorContents()), type)*/; //probably more intuitive
    }

    /**
     * Sums the prices of all items in the collection for a given transaction type.
     *
     * @param stacks a collection of item stacks
     * @param type   the transaction type for which to calculate the price
     * @return the sum of item prices
     */
    public double sumPrices(Collection<ItemStack> stacks, TransactionType type) {
        Preconditions.checkNotNull(stacks, "stacks");
        Preconditions.checkNotNull(type, "type");

        return stacks.stream()
                .map(itemManager::getItem)
                .filter(Objects::nonNull)
                .mapToDouble(type::getValue)
                .sum();
    }

    /**
     * Calculates the price for a specified amount of items in respect to a given transaction type.
     *
     * @param item   the item to survey
     * @param amount the amount to calculate the price for
     * @param type   the transaction type for which to calculate the price
     * @return the calculated price for given amount
     */
    public double calculatePrice(ShopItem item, int amount, TransactionType type) {
        Preconditions.checkNotNull(item, "item");
        Preconditions.checkNotNull(type, "type");
        Preconditions.checkArgument(type.isTradable(item), "%s is not tradable with %s", item, type);
        return type.getValue(item) * amount;
    }

    /**
     * Calculates the price of an item stack in respect to a given transaction type. If the item stack is not tradable
     * for given transaction type, a value of zero is returned. This method respects the stack's amount.
     *
     * @param itemStack the stack to calculate the price for
     * @param type      the transaction type for which to calculate the price
     * @return the calculated price for given stack
     */
    public double calculatePrice(ItemStack itemStack, TransactionType type) {
        Preconditions.checkNotNull(itemStack, "itemStack");
        Preconditions.checkNotNull(type, "type");
        ShopItem item = itemManager.getItem(itemStack);
        if (!type.isTradable(item)) {
            return 0D;
        } else {
            return calculatePrice(item, itemStack.getAmount(), type);
        }
    }
}
