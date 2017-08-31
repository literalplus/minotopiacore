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

import li.l1t.common.util.inventory.ItemStackFactory;
import li.l1t.mtc.module.shop.ShopModule;
import li.l1t.mtc.module.shop.ShopPriceCalculator;
import li.l1t.mtc.module.shop.ui.inventory.button.BackToListButton;
import li.l1t.mtc.module.shop.ui.inventory.button.GenericButton;
import li.l1t.mtc.module.shop.ui.inventory.button.SellButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * An inventory menu where players can drop items to sell to the shop.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 18.4.16
 */
public class ShopSellMenu extends ShopMenu {
    protected ShopSellMenu(Player player, ShopModule module) {
        super(player, module);
        initTopRow();
        renderTopMenu();
    }

    private void initTopRow() {
        setTopRowButton(0, BackToListButton.INSTANCE);
        setTopRowButton(3, new GenericButton(
                new ItemStackFactory(Material.BOOK)
                        .displayName("§6Info")
                        .lore("§7Um etwas zu verkaufen, ziehe")
                        .lore("§7es in dieses Inventar.")
                        .lore(" ")
                        .lore("§6Alternativ: /shop verkaufen")
                        .produce(), null));
        setTopRowButton(4, new SellButton(new ShopPriceCalculator(module.getItemManager())));
        setTopRowButton(8, BackToListButton.INSTANCE);
    }

    /**
     * Updates the top menu of this inventory to match the current canvas value.
     */
    public void updateTopMenu() {
        renderTopMenu();
    }

    /**
     * Deletes all items from the canvas, returning anything left to the player.
     */
    public void clearCanvas() {
        ItemStack[] contents = getInventory().getContents();
        for (int slotId = ROW_SIZE; slotId < contents.length; slotId++) {
            returnStackToPlayer(contents[slotId]);
        }
        getInventory().clear();
        renderTopMenu();
    }

    @Override
    @SuppressWarnings({"SimplifiableIfStatement"})
    public boolean handleClick(InventoryClickEvent evt) {
        if (evt.getSlot() < ROW_SIZE) { //no modifications to top menu bar
            return super.handleClick(evt);
        }

        return false;
    }

    @Override
    public void handleClickMonitor(InventoryClickEvent evt) {
        switch (evt.getAction()) {
            case CLONE_STACK:
            case DROP_ALL_CURSOR:
            case DROP_ALL_SLOT:
            case DROP_ONE_CURSOR:
            case DROP_ONE_SLOT:
            case NOTHING:
            case UNKNOWN:
                return;
        }

        getModule().getPlugin().getServer().getScheduler().runTaskLater(
                getModule().getPlugin(),
                this::updateTopMenu, //update price since contents probably changed
                10L
        );
    }

    @Override
    public void handleClose(InventoryCloseEvent evt) {
        ItemStack[] contents = evt.getInventory().getContents();
        for (int slotId = ROW_SIZE; slotId < contents.length; slotId++) {
            returnStackToPlayer(contents[slotId]);
        }
    }

    private void returnStackToPlayer(ItemStack stack) {
        if (stack != null && stack.getType() != Material.AIR) {
            //Intended: Players keep their stuff at death, chris said it doesn't really matter
            HashMap<Integer, ItemStack> leftover =
                    getPlayer().getInventory().addItem(stack);
            if (!leftover.isEmpty()) {
                for (ItemStack leftoverStack : leftover.values()) {
                    getPlayer().getWorld().dropItem(getPlayer().getLocation(), leftoverStack);
                }
            }
        }
    }

    @Override
    protected void handleCanvasClick(InventoryClickEvent evt, int canvasId) {
        //not used by this implementation, see #handleClick(...)
    }

    @Override
    protected String getInventoryTitle() {
        return "§9§lMinoTopia Shop - Verkaufen";
    }

    @Override
    public boolean permitsHotbarSwap(InventoryClickEvent evt) {
        updateTopMenu();
        return true;
    }

    /**
     * Opens a new shop sell menu for a given player.
     *
     * @param player the player to open the menu for
     * @param module the shop module managing the menu
     * @return the created menu
     */
    public static ShopSellMenu openMenu(Player player, ShopModule module) {
        ShopSellMenu menu = new ShopSellMenu(player, module);
        menu.open();
        return menu;
    }
}
