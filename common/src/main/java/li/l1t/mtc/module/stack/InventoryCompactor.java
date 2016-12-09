/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.stack;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

/**
 * Compacts similar stacks in an inventory to bigger sizes. This class is not thread-safe at all.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-28
 */
public class InventoryCompactor {
    private final int maxOversizedStackSize;
    private final Predicate<ItemStack> oversizablePredicate;
    private ItemStack[] items;
    private int firstUncheckedItem = 0;
    private int currentMaxStackSize;
    private ItemStack currentItem;

    public InventoryCompactor(int maxOversizedStackSize, Predicate<ItemStack> oversizedPredicate) {
        this.maxOversizedStackSize = sanitizeMaxStackSize(maxOversizedStackSize);
        this.oversizablePredicate = oversizedPredicate;
    }

    private int sanitizeMaxStackSize(int maxOversizedStackSize) {
        return maxOversizedStackSize > 64 ? 64 : maxOversizedStackSize;
    }

    public ItemStack[] compact(ItemStack[] items) {
        this.items = items;
        compactAll();
        this.items = null;
        return items;
    }

    public void compact(Inventory inventory) {
        inventory.setContents(compact(inventory.getContents()));
    }

    private void compactAll() {
        for (int currentIndex = 0; currentIndex < items.length; currentIndex++) {
            ItemStack currentItem = items[currentIndex];
            firstUncheckedItem = currentIndex + 1;
            attemptCompactionIfAllowed(currentItem);
        }
    }

    private void attemptCompactionIfAllowed(ItemStack stack) {
        if (isEligibleForCompaction(stack)) {
            setCurrentItem(stack);
            mergeCurrentWithSimilarStacks();
        }
    }

    private void setCurrentItem(ItemStack stack) {
        currentItem = stack;
        currentMaxStackSize = getMaxStackSizeFor(currentItem);
    }

    private boolean isEligibleForCompaction(ItemStack stack) {
        return stack != null && stack.getAmount() > 0 &&
                mayBeStackedFurther(stack);
    }

    private boolean mayBeStackedFurther(ItemStack stack) {
        return stack.getAmount() < getMaxStackSizeFor(stack);
    }

    private int getMaxStackSizeFor(ItemStack stack) {
        if (!oversizablePredicate.test(stack)) {
            return stack.getMaxStackSize();
        }
        if (stack.getMaxStackSize() > maxOversizedStackSize) {
            return stack.getMaxStackSize();
        } else {
            return maxOversizedStackSize;
        }
    }

    private void mergeCurrentWithSimilarStacks() {
        for (int candidateIndex = firstUncheckedItem; candidateIndex < items.length; candidateIndex++) {
            ItemStack candidate = items[candidateIndex];
            if (isEligibleForCompaction(candidate) && areStacksMergeable(currentItem, candidate)) {
                mergeWithCurrent(candidate, candidateIndex);
                if (findSpaceLeftIn(currentItem) <= 0) {
                    return;
                }
            }
        }
    }

    private void mergeWithCurrent(ItemStack candidate, int candidateIndex) {
        int spaceLeftInCurrent = findSpaceLeftIn(currentItem);
        if (candidate.getAmount() > spaceLeftInCurrent) {
            currentItem.setAmount(currentMaxStackSize);
            candidate.setAmount(candidate.getAmount() - spaceLeftInCurrent);
        } else {
            items[candidateIndex] = null;
            currentItem.setAmount(currentItem.getAmount() + candidate.getAmount());
        }
    }

    private int findSpaceLeftIn(ItemStack currentItem) {
        return currentMaxStackSize - currentItem.getAmount();
    }

    private boolean areStacksMergeable(ItemStack item1, ItemStack item2) {
        return item1.getType() == item2.getType() &&
                item1.getDurability() == item2.getDurability() &&
                doItemMetasMatch(item1, item2);
    }

    private boolean doItemMetasMatch(ItemStack item1, ItemStack item2) {
        return neitherHasItemMeta(item1, item2) ||
                bothHaveEqualItemMeta(item1, item2);
    }

    private boolean neitherHasItemMeta(ItemStack item1, ItemStack item2) {
        return !item1.hasItemMeta() && !item2.hasItemMeta();
    }

    private boolean bothHaveEqualItemMeta(ItemStack item1, ItemStack item2) {
        return item1.hasItemMeta() && item2.hasItemMeta() &&
                item1.getItemMeta().equals(item2.getItemMeta());
    }
}
