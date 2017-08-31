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

package li.l1t.mtc.module.shop.ui.inventory;

import com.google.common.base.Preconditions;
import li.l1t.common.util.inventory.ItemStackFactory;
import li.l1t.mtc.hook.VaultHook;
import li.l1t.mtc.module.shop.ShopModule;
import li.l1t.mtc.module.shop.api.ShopItem;
import li.l1t.mtc.module.shop.ui.inventory.button.OpenSellMenuButton;
import li.l1t.mtc.module.shop.ui.inventory.button.PaginationButton;
import li.l1t.mtc.module.shop.ui.inventory.button.SortOrderButton;
import li.l1t.mtc.module.shop.ui.inventory.button.SortTypeButton;
import li.l1t.mtc.module.shop.ui.inventory.comparator.IdBasedComparator;
import li.l1t.mtc.module.shop.ui.inventory.comparator.ShopItemComparator;
import li.l1t.mtc.module.shop.ui.util.ShopStringAdaptor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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
    private ShopItemComparator itemComparator;
    private boolean onlyShowBuyableItems = false;

    private int currentItemStart = 0;

    ShopListMenu(Player player, ShopModule module) {
        super(player, module);
        initTopRow();
    }

    private void initTopRow() {
        setTopRowButton(0, PaginationButton.FIRST_PAGE);
        setTopRowButton(1, PaginationButton.PREVIOUS_PAGE);
        setTopRowButton(3, SortTypeButton.INSTANCE);
        setTopRowButton(4, OpenSellMenuButton.INSTANCE);
        setTopRowButton(5, SortOrderButton.INSTANCE);
        setTopRowButton(7, PaginationButton.NEXT_PAGE);
        setTopRowButton(8, PaginationButton.LAST_PAGE);
    }

    /**
     * Renders this menu in the associated inventory.
     */
    public void render() {
        renderTopMenu();

        int numberOfItemsToDisplay = items.size() - currentItemStart;
        int numberOfSlotsToFill = Math.min(numberOfItemsToDisplay, CANVAS_SIZE);
        VaultHook vaultHook = module.getPlugin().getVaultHook();
        double currentBalance = vaultHook == null ? 0 : vaultHook.getBalance(getPlayer());

        for (int canvasId = 0; canvasId < numberOfSlotsToFill; canvasId++) {
            ShopItem item = items.get(currentItemStart + canvasId);
            if (item != null) {
                ItemStack stack;
                if (vaultHook != null && currentBalance < item.getBuyCost()) {
                    stack = new ItemStackFactory(item.toItemStack(1))
                            .displayName(item.getDisplayName())
                            .lore("§cDas kannst du dir nicht leisten!")
                            .lore("§4Stückpreis: §c" +
                                    ShopStringAdaptor.getCurrencyString(module.getItemManager().getBuyCost(item)))
                            .lore("§4Du hast: §c" + ShopStringAdaptor.getCurrencyString(currentBalance))
                            .produce();
                } else {
                    stack = ShopInventoryHelper.createInfoStack(item, module.getItemManager());
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

    /**
     * @param itemStart the index of the first item to be displayed when {@link #render()} is
     *                  called
     */
    public void setItemStart(int itemStart) {
        Preconditions.checkArgument(itemStart < items.size(),
                "itemStart %s must be less than items size %s!", itemStart, items.size());
        this.currentItemStart = itemStart;
    }

    /**
     * Renders the page currently defined by {@link #setItemStart(int)} by calling {@link
     * #render()}, but reopens the inventory before, so that the page count in the title is
     * updated.
     */
    public void renderCurrentPage() {
        createNewInventory(); //updates the title
        open(); //overrides current inventory
        render();
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
        this.rawItems = new ArrayList<>(unfilteredItems);
        sortItems();
    }

    /**
     * Sorts this menu's items according to the {@link #getItemComparator() item comparator}.
     */
    public void sortItems() {
        this.rawItems.sort(getItemComparator());
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
        shopMenu.render();
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
        return String.format("§9§lMinoTopia Shop §9(%d/%d)", findCurrentPageNum(), findPageCount());
    }

    private int findPageCount() {
        return ceilDiv(items.size(), CANVAS_SIZE);
    }

    private int findCurrentPageNum() {
        return ceilDiv(currentItemStart + 1, CANVAS_SIZE); //+1 because, usually, itemStart is exactly a multiple of CANVAS_SIZE, leading to the page count being too low
    }

    private int ceilDiv(int x, int y) { //this should be in a utility class
        return -Math.floorDiv(-x, y); // http://stackoverflow.com/a/27643634/1117552
    } //why is there no Math.ceilDiv? http://stackoverflow.com/q/38683837/1117552

    /**
     * @return the current comparator used by this menu, never null (lazy init)
     */
    public ShopItemComparator getItemComparator() {
        if (itemComparator == null) {
            itemComparator = IdBasedComparator.ASCENDING;
        }
        return itemComparator;
    }

    /**
     * Sets the item comparator for this menu and updates the view. Renders the first page.
     *
     * @param itemComparator the new comparator
     */
    public void setItemComparator(ShopItemComparator itemComparator) {
        this.itemComparator = itemComparator;
        sortItems();
        setItemStart(0);
        render();
    }
}
