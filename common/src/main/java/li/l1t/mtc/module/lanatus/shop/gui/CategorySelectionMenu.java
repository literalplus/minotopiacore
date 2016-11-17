/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.shop.gui;

import li.l1t.common.inventory.gui.PaginationListMenu;
import li.l1t.lanatus.shop.api.Category;
import li.l1t.lanatus.shop.api.ItemIconService;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.function.BiConsumer;

/**
 * Displays an inventory menu for selection of a category to shop in.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-17-11
 */
public class CategorySelectionMenu extends PaginationListMenu<Category> {
    private final ItemIconService iconService;
    private final BiConsumer<Category, CategorySelectionMenu> clickHandler;

    public CategorySelectionMenu(Plugin plugin, Player player, ItemIconService iconService, BiConsumer<Category, CategorySelectionMenu> clickHandler) {
        super(plugin, player);
        this.iconService = iconService;
        this.clickHandler = clickHandler;
    }

    @Override
    protected void initTopRow() {
        super.initTopRow();
        //TODO: button to show my positions #628
        //TODO: button to show my purchases #629
    }

    @Override
    protected void handleValueClick(Category item, InventoryClickEvent evt) {
        clickHandler.accept(item, this);
    }

    @Override
    protected ItemStack drawItem(Category toDraw) {
        return iconService.createIconStack(toDraw);
    }

    @Override
    protected String formatTitle(int currentPage, int pageCount) {
        return String.format("§e§lPremiumshop §9(%s/%s)", currentPage, pageCount);
    }
}
