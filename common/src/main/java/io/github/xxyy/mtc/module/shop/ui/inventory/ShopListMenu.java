/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.inventory;

import com.google.common.base.Preconditions;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ui.inventory.button.PaginationButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Collection;
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
    private Collection<ShopItem> rawItems;
    private List<ShopItem> items;
    private boolean onlyShowBuyableItems = false;

    private int currentItemStart;

    ShopListMenu(Player player, ShopModule module) {
        super(player, module);
        initTopRow();
    }

    private void initTopRow() {
        setTopRowButton(0, PaginationButton.FIRST_PAGE);
        setTopRowButton(1, PaginationButton.PREVIOUS_PAGE);
        //TODO: sell button at 4
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

        for (int i = 0; i < topRowButtons.length; i++) {
            getInventory().setItem(i, topRowButtons[i].getItemStack(this));
        }

        int numberOfItemsToDisplay = items.size() - itemStart;
        int numberOfSlotsToFill = Math.min(numberOfItemsToDisplay, CANVAS_SIZE);

        for (int canvasId = 0; canvasId < numberOfSlotsToFill; canvasId++) {
            ShopItem item = items.get(itemStart + canvasId);
            if (item != null) {
                int slotId = ROW_SIZE + canvasId;
                getInventory().setItem(slotId, item.toItemStack(1));
                displayedItems[canvasId] = item;
            }
        }
    }

    /**
     * Sets the items to be shown in this menu.
     *
     * @param unfilteredItems the items to be shown in this menu
     */
    public void setItems(Collection<ShopItem> unfilteredItems) {
        this.rawItems = unfilteredItems;
        filterItems();
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
}
