/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.shop.gui;

import li.l1t.common.inventory.gui.ChildMenu;
import li.l1t.common.inventory.gui.PagingListMenu;
import li.l1t.common.inventory.gui.element.Placeholder;
import li.l1t.common.inventory.gui.element.button.BackToParentButton;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.Category;
import li.l1t.lanatus.shop.api.ItemIconService;
import li.l1t.mtc.module.lanatus.shop.LanatusShopModule;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Displays a menu for selecting a product from a category.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-17-11
 */
public class ProductSelectionMenu extends PagingListMenu<Product> implements ChildMenu {
    private final CategorySelectionMenu parent;
    private final Category category;
    private final BiConsumer<Product, ProductSelectionMenu> clickHandler;
    private final ItemIconService iconService;
    private Map<Product, ItemStack> productIconMap = new HashMap<>();

    ProductSelectionMenu(CategorySelectionMenu parent, Category category, BiConsumer<Product, ProductSelectionMenu> clickHandler,
                         Plugin plugin, Player player, ItemIconService iconService) {
        super(plugin, player);
        this.parent = parent;
        this.category = category;
        this.clickHandler = clickHandler;
        this.iconService = iconService;
        initTopRow();
    }

    /**
     * Sets the items of this menu with their icons.
     *
     * @param productIconMap the mapping of products to their corresponding icons
     */
    public void setItems(Map<Product, ItemStack> productIconMap) {
        this.productIconMap = productIconMap;
        super.setItems(productIconMap.keySet());
    }

    public static ProductSelectionMenu withParent(CategorySelectionMenu parent, Category category,
                                                  BiConsumer<Product, ProductSelectionMenu> clickHandler, LanatusShopModule module) {
        return new ProductSelectionMenu(
                parent, category, clickHandler, module.getPlugin(), parent.getPlayer(),
                module.iconService()
        );
    }

    public static ProductSelectionMenu withoutParent(Category category, BiConsumer<Product, ProductSelectionMenu> clickHandler,
                                                     Player player, LanatusShopModule module) {
        return new ProductSelectionMenu(
                null, category, clickHandler, module.getPlugin(), player, module.iconService()
        );
    }

    @Override
    protected void initTopRow() {
        if (parent != null) {
            addToTopRow(0, BackToParentButton.INSTANCE);
            addToTopRow(8, BackToParentButton.INSTANCE);
        }
        addToTopRow(4, new Placeholder(iconService.createIconStack(category)));
    }

    @Override
    protected void handleValueClick(Product item, InventoryClickEvent evt) {
        clickHandler.accept(item, this);
    }

    @Override
    protected ItemStack drawItem(Product product) {
        return productIconMap.computeIfAbsent(product, prod -> iconService.createIconStack(product, getPlayer().getUniqueId()));
    }

    @Override
    protected String formatTitle(int currentPage, int pageCount) {
        return "§e§l" + category.getDisplayName();
    }

    @Override
    public CategorySelectionMenu getParent() {
        return parent;
    }

    public Category getCategory() {
        return category;
    }
}
