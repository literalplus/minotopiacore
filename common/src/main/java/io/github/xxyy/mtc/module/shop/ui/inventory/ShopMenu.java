/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.inventory;

import com.google.common.base.Preconditions;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ui.inventory.button.MenuButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * A simple menu icon framework for the Shop inventory. Abstract base class for inventory menus for trading.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-04-17
 */
public abstract class ShopMenu implements InventoryHolder {
    public static final int ROW_SIZE = 9;
    public static final int INVENTORY_SIZE = 6 * ROW_SIZE;
    public static final int CANVAS_SIZE = INVENTORY_SIZE - ROW_SIZE;
    protected final ShopModule module;
    private final Inventory inventory;
    private final Player player;
    protected MenuButton[] topRowButtons = new MenuButton[ROW_SIZE];

    protected ShopMenu(Player player, ShopModule module) {
        this.player = player;
        this.module = module;
        this.inventory = module.getPlugin().getServer()
                .createInventory(this, INVENTORY_SIZE, getInventoryTitle());
    }

    public void open() {
        if (player.getOpenInventory() != null) {
            player.closeInventory();
        }

        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * @return the player holding this menu
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the top row button with given index for this menu, or null if there is no button at that index.
     *
     * @param index the index to get the button for (0 <= index < 9)
     * @return the button, or null if there is none
     */
    public MenuButton getTopRowButton(int index) {
        Preconditions.checkArgument(index >= 0 && index < 9,
                "index must be positive and less than 10, given: %s", index);
        return topRowButtons[index];
    }

    /**
     * Sets the top row button with given index for this menu.
     *
     * @param index  the index to set the button for (0 <= index < 9)
     * @param button the button to set, or null to set no button
     */
    protected void setTopRowButton(int index, MenuButton button) {
        Preconditions.checkArgument(index >= 0 && index < 9,
                "index must be positive and less than 10, given: %s", index);
        topRowButtons[index] = button;
    }

    /**
     * @return the module managing this menu
     */
    public ShopModule getModule() {
        return module;
    }

    /**
     * Handles a click on this menu.
     *
     * @param evt the event causing the click
     */
    public void handleClick(InventoryClickEvent evt) {
        int slotId = evt.getSlot();
        if (slotId < ROW_SIZE) { //top row
            MenuButton button = topRowButtons[slotId];
            if (button != null) {
                button.handleMenuClick(evt, this);
            }
        } else {
            handleCanvasClick(evt, slotId - ROW_SIZE);
        }
    }

    /**
     * Handles a click into the canvas area (e.g. everything except the top row) of this menu.
     *
     * @param canvasId the index of the clicked slot in the canvas (slotId - {@value #ROW_SIZE})
     * @param evt      the event causing the click
     */
    protected abstract void handleCanvasClick(InventoryClickEvent evt, int canvasId);

    protected abstract String getInventoryTitle();
}
