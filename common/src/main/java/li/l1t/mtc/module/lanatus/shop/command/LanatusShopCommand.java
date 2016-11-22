/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.shop.command;

import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.Category;
import li.l1t.mtc.api.command.CommandExecution;
import li.l1t.mtc.command.BukkitExecutionExecutor;
import li.l1t.mtc.module.lanatus.shop.LanatusShopModule;
import li.l1t.mtc.module.lanatus.shop.gui.CategorySelectionMenu;
import li.l1t.mtc.module.lanatus.shop.gui.ProductDetailMenu;
import li.l1t.mtc.module.lanatus.shop.gui.ProductSelectionMenu;

import java.util.Collection;

/**
 * Executes the /lashop command, which allows to open the category selection menu.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-17-11
 */
public class LanatusShopCommand extends BukkitExecutionExecutor {
    private final LanatusShopModule module;

    public LanatusShopCommand(LanatusShopModule module) {
        this.module = module;
    }

    @Override
    public boolean execute(CommandExecution exec) throws UserException, InternalException {
        if (exec.hasArg(0)) {
            exec.respondUsage("", "", "Öffnet das Shopmenü.");
        } else {
            Collection<Category> categories = module.categories().findAll();
            CategorySelectionMenu menu = new CategorySelectionMenu(
                    module.getPlugin(), exec.player(), module.iconService(), this::handleCategoryClick
            );
            menu.addItems(categories);
            menu.open();
        }
        return true;
    }

    private void handleCategoryClick(Category category, CategorySelectionMenu oldMenu) {
        ProductSelectionMenu newMenu = ProductSelectionMenu.withParent(
                oldMenu, category, this::handleProductClick, module
        );
        newMenu.addItems(module.categories().findProductsOf(category));
        newMenu.open();
    }

    private void handleProductClick(Product product, ProductSelectionMenu oldMenu) {
        ProductDetailMenu newMenu = new ProductDetailMenu(
                product, oldMenu, module.iconService(), module.buyService()
        );
        newMenu.open();
    }
}
