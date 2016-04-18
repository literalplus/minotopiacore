/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.inventory;

import io.github.xxyy.common.util.inventory.ItemStackFactory;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ui.inventory.button.BackToListButton;
import io.github.xxyy.mtc.module.shop.ui.inventory.button.GenericButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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
        setTopRowButton(8, BackToListButton.INSTANCE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean handleClick(InventoryClickEvent evt) {
        if (evt.isLeftClick()) {
            return super.handleClick(evt);
        }
        ItemStack cursor = evt.getCursor();
        if (cursor == null) {
            return true;
        }

        int amount;
        switch (evt.getAction()) {
            case PLACE_ALL:
                amount = cursor.getAmount();
                break;
            case PLACE_SOME:
                amount = cursor.getAmount() / 2;
                break;
            case PLACE_ONE:
                amount = 1;
                break;
            default:
                return true;
        }

        ShopItem item = getModule().getItemManager().getItem(cursor);
        ItemStack newCursor = module.getTransactionExecutor().attemptRawSell(
                getPlayer(), cursor, item, amount
        );
        evt.setCursor(newCursor);      //This should *theoretically* be okay if we
        getPlayer().updateInventory(); // do this

        return true;
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
