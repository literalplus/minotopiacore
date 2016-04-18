/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.inventory;

import io.github.xxyy.common.util.inventory.ItemStackFactory;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ShopPriceCalculator;
import io.github.xxyy.mtc.module.shop.ui.inventory.button.BackToListButton;
import io.github.xxyy.mtc.module.shop.ui.inventory.button.GenericButton;
import io.github.xxyy.mtc.module.shop.ui.inventory.button.SellButton;
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
                new ItemStackFactory(Material.SKULL_ITEM)
                        .skullOwner("MHF_Question")
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
