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

package li.l1t.mtc.module.lanatus.shop.command;

import li.l1t.common.command.BukkitExecution;
import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.lanatus.api.account.AccountSnapshot;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.Category;
import li.l1t.lanatus.shop.api.event.CategoryDisplayEvent;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.command.MTCExecutionExecutor;
import li.l1t.mtc.module.lanatus.shop.LanatusShopModule;
import li.l1t.mtc.module.lanatus.shop.gui.CategorySelectionMenu;
import li.l1t.mtc.module.lanatus.shop.gui.ProductDetailMenu;
import li.l1t.mtc.module.lanatus.shop.gui.ProductSelectionMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.function.Function;

/**
 * Executes the /lashop command, which allows to open the category selection menu.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-17-11
 */
public class LanatusShopCommand extends MTCExecutionExecutor {
    private final LanatusShopModule module;

    public LanatusShopCommand(LanatusShopModule module) {
        this.module = module;
    }

    @Override
    public boolean execute(BukkitExecution exec) throws UserException, InternalException {
        if (exec.hasArg(0)) {
            exec.respondUsage("", "", "Öffnet das Shopmenü.");
        } else {
            Collection<Category> categories = module.categories().findAll();
            CategorySelectionMenu menu = new CategorySelectionMenu(
                    module.getPlugin(), exec.player(), module.iconService(), this::handleCategoryClick,
                    module.client().accounts());
            menu.addItems(categories);
            menu.open();
        }
        return true;
    }

    private void handleCategoryClick(Category category, CategorySelectionMenu oldMenu) {
        ProductSelectionMenu newMenu = ProductSelectionMenu.withParent(
                oldMenu, category, this::handleProductClick, module
        );
        AccountSnapshot account = module.client().accounts().findOrDefault(oldMenu.getPlayer().getUniqueId());
        Collection<Product> products = module.categories().findProductsOf(category);
        CategoryDisplayEvent event = new CategoryDisplayEvent(
                oldMenu.getPlayer(), category, products, account, productIconFunction(oldMenu.getPlayer())
        );
        module.getPlugin().getServer().getPluginManager().callEvent(event);
        newMenu.setItems(event.getProductIconMap());
        newMenu.open();
    }

    private Function<Product, ItemStack> productIconFunction(Player player) {
        return product -> module.iconService().createIconStack(product, player.getUniqueId());
    }

    private void handleProductClick(Product product, ProductSelectionMenu oldMenu) {
        if (isPermanentAndHasAlreadyBeenBought(product, oldMenu.getPlayer())) {
            MessageType.USER_ERROR.sendTo(oldMenu.getPlayer(), "Das besitzt du bereits!");
            return;
        }
        ProductDetailMenu newMenu = new ProductDetailMenu(
                product, oldMenu, module.iconService(), module.buyService()
        );
        newMenu.open();
    }

    private boolean isPermanentAndHasAlreadyBeenBought(Product product, Player player) {
        return product.isPermanent() && playerOwns(product, player);
    }

    private boolean playerOwns(Product product, Player player) {
        return module.client().positions().playerHasProduct(player.getUniqueId(), product.getUniqueId());
    }
}
