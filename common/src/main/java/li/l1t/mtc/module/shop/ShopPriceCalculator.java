/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.shop;

import com.google.common.base.Preconditions;
import li.l1t.mtc.module.shop.api.ShopItemManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.ToDoubleFunction;

import static li.l1t.common.util.PredicateHelper.not;

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
        return sumInventoryPrices(plr.getInventory(), type) /*+
                sumPrices(Arrays.asList(plr.getInventory().getArmorContents()), type)*/; //probably more intuitive
    }

    /**
     * Sums the prices of all items in an inventory.
     *
     * @param inventory the inventory to survey
     * @param type      the transaction type for which to calculate the price
     * @return the sum of item prices
     */
    public double sumInventoryPrices(Inventory inventory, TransactionType type) {
        Preconditions.checkNotNull(inventory, "inventory");
        return sumPrices(Arrays.asList(inventory.getContents()), type);
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
                .filter(not(itemManager::isTradeProhibited))
                .mapToDouble(toItemWorthFunction(type))
                .sum();
    }

    @Nonnull
    private ToDoubleFunction<ItemStack> toItemWorthFunction(TransactionType type) {
        return stack -> {
            ShopItem item = itemManager.getItem(stack);
            if (item == null || !type.isTradable(item)) {
                return 0D;
            } else {
                return calculatePrice(item, stack.getAmount(), type);
            }
        };
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
     * Calculates the price of an item stack in respect to a given transaction type. If the item
     * stack is not tradable for given transaction type, a value of zero is returned. This method
     * respects the stack's amount.
     *
     * @param itemStack the stack to calculate the price for
     * @param type      the transaction type for which to calculate the price
     * @return the calculated price for given stack
     */
    public double calculatePrice(ItemStack itemStack, TransactionType type) {
        Preconditions.checkNotNull(itemStack, "itemStack");
        Preconditions.checkNotNull(type, "type");
        if (itemManager.isTradeProhibited(itemStack)) {
            return 0D;
        }
        ShopItem item = itemManager.getItem(itemStack);
        if (!type.isTradable(item)) {
            return 0D;
        } else {
            return calculatePrice(item, itemStack.getAmount(), type);
        }
    }
}
