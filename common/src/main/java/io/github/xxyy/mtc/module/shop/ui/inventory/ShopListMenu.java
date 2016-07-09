/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.inventory;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import io.github.xxyy.common.util.inventory.ItemStackFactory;
import io.github.xxyy.mtc.hook.VaultHook;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.manager.DiscountManager;
import io.github.xxyy.mtc.module.shop.ui.inventory.button.OpenSellMenuButton;
import io.github.xxyy.mtc.module.shop.ui.inventory.button.PaginationButton;
import io.github.xxyy.mtc.module.shop.ui.inventory.comparator.NameBasedComparator;
import io.github.xxyy.mtc.module.shop.ui.util.ShopStringAdaptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This inventory allows players to visually select items to buy, sell or view the price of.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-04-17
 */
public class ShopListMenu extends ShopMenu {
    private ShopItem[] displayedItems = new ShopItem[CANVAS_SIZE];
    private List<ShopItem> rawItems;
    private List<ShopItem> items;
    private Comparator<ShopItem> itemComparator;
    private boolean onlyShowBuyableItems = false;

    private int currentItemStart;

    ShopListMenu(Player player, ShopModule module) {
        super(player, module);
        initTopRow();
    }

    private void initTopRow() {
        setTopRowButton(0, PaginationButton.FIRST_PAGE);
        setTopRowButton(1, PaginationButton.PREVIOUS_PAGE);
        setTopRowButton(4, OpenSellMenuButton.INSTANCE);
        setTopRowButton(7, PaginationButton.NEXT_PAGE);
        setTopRowButton(8, PaginationButton.LAST_PAGE);
    }

    /**
     * Renders this menu in the associated inventory.
     *
     * @param itemStart the index of the first shop item to render (for paging)
     */
    public void render(int itemStart) {
        Preconditions.checkArgument(itemStart < items.size(),
                "itemStart %s must be less than items size %s!", itemStart, items.size());
        this.currentItemStart = itemStart;
        renderTopMenu();

        int numberOfItemsToDisplay = items.size() - itemStart;
        int numberOfSlotsToFill = Math.min(numberOfItemsToDisplay, CANVAS_SIZE);
        VaultHook vaultHook = module.getPlugin().getVaultHook();
        double currentBalance = vaultHook == null ? 0 : vaultHook.getBalance(getPlayer());

        for (int canvasId = 0; canvasId < numberOfSlotsToFill; canvasId++) {
            ShopItem item = items.get(itemStart + canvasId);
            if (item != null) {
                ItemStack stack;
                if (vaultHook != null && currentBalance < item.getBuyCost()) {
                    stack = new ItemStackFactory(item.toItemStack(1))
                            .displayName(item.getDisplayName())
                            .lore("§cDas kannst du dir nicht leisten!")
                            .lore("§4Stückpreis: §c" +
                                    ShopStringAdaptor.getCurrencyString(item.getManager().getBuyCost(item)))
                            .lore("§4Du hast: §c" + ShopStringAdaptor.getCurrencyString(currentBalance))
                            .produce();
                } else {
                    stack = ShopInventoryHelper.createInfoStack(item, true);
                }
                setItem(canvasId, item, stack);
            }
        }
        if (numberOfSlotsToFill < CANVAS_SIZE) {
            for (int canvasId = numberOfSlotsToFill; canvasId < CANVAS_SIZE; canvasId++) {
                setItem(canvasId, null, null);
            }
        }
    }

    private void setItem(int canvasId, ShopItem item, ItemStack stack) {
        displayedItems[canvasId] = item;
        getInventory().setItem(canvasId + ROW_SIZE, stack);
    }

    /**
     * Sets the items to be shown in this menu.
     *
     * @param unfilteredItems the items to be shown in this menu
     */
    public void setItems(List<ShopItem> unfilteredItems) {
        this.rawItems = new ArrayList<>();
        DiscountManager discountManager = getModule().getItemManager().getDiscountManager();
        if (discountManager.hasDiscount()) {
            this.rawItems.add(discountManager.getDiscountedItem());
        }
        this.rawItems.addAll(unfilteredItems);
        sortItems();
        filterItems();
    }

    private void sortItems() {
        Collections.sort(this.rawItems, getItemComparator());
    }

    private void filterItems() {
        Predicate<ShopItem> predicate;
        if (isOnlyShowBuyableItems()) {
            predicate = ShopItem::canBeBought;
        } else {
            predicate = item -> item.canBeBought() || item.canBeSold();
        }

        this.items = this.rawItems.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * @return the index of the first item currently displayed
     */
    public int getCurrentItemStart() {
        return currentItemStart;
    }

    @Override
    protected void handleCanvasClick(InventoryClickEvent evt, int canvasId) {
        ShopItem item = displayedItems[canvasId];
        if (item != null) {
            ShopDetailMenu.openMenu(getPlayer(), getModule(), item);
        }
    }

    /**
     * @return the list of items this menu displays
     */
    public List<ShopItem> getItems() {
        return items;
    }

    /**
     * Opens a new shop list menu for a given player.
     *
     * @param plr    the player to open the menu for
     * @param module the shop module managing the menu
     * @return the created menu
     */
    public static ShopMenu openMenu(Player plr, ShopModule module) {
        ShopListMenu shopMenu = new ShopListMenu(plr, module);
        shopMenu.setItems(module.getItemManager().getItems());
        shopMenu.open();
        shopMenu.render(0);
        return shopMenu;
    }

    /**
     * Sets whether only buyable items are shown (tradable otherwise).
     *
     * @param onlyShowBuyableItems whether only buyable items are shown
     */
    public void setOnlyShowBuyableItems(boolean onlyShowBuyableItems) {
        this.onlyShowBuyableItems = onlyShowBuyableItems;
        filterItems();
    }

    /**
     * @return whether only buyable items are shown (tradable otherwise)
     */
    public boolean isOnlyShowBuyableItems() {
        return onlyShowBuyableItems;
    }

    @Override
    protected String getInventoryTitle() {
        return "§9§lMinoTopia Shop";
    }

    public Comparator<ShopItem> getItemComparator() {
        if (itemComparator == null) {
            itemComparator = new NameBasedComparator();
        }
        return itemComparator;
    }
}
