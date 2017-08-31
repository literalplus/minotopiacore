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

package li.l1t.mtc.module.lanatus.shop.gui;

import li.l1t.common.inventory.gui.PaginationListMenu;
import li.l1t.common.inventory.gui.element.Placeholder;
import li.l1t.common.util.inventory.ItemStackFactory;
import li.l1t.lanatus.api.account.AccountRepository;
import li.l1t.lanatus.api.account.AccountSnapshot;
import li.l1t.lanatus.shop.api.Category;
import li.l1t.lanatus.shop.api.ItemIconService;
import org.bukkit.Material;
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
    private final AccountRepository accountRepository;

    public CategorySelectionMenu(Plugin plugin, Player player, ItemIconService iconService, BiConsumer<Category, CategorySelectionMenu> clickHandler, AccountRepository accountRepository) {
        super(plugin, player);
        this.iconService = iconService;
        this.clickHandler = clickHandler;
        this.accountRepository = accountRepository;
        addToTopRow(4, new Placeholder(currentMelonsCountIcon())); //called here because we need the account repo
    }

    @Override
    protected void initTopRow() {
        super.initTopRow();
        //TODO: button to show my positions #628
        //TODO: button to show my purchases #629
    }

    private ItemStack currentMelonsCountIcon() {
        AccountSnapshot snapshot = accountRepository.findOrDefault(getPlayer().getUniqueId());
        return new ItemStackFactory(Material.MELON)
                .displayName("§e§lDein Guthaben:")
                .lore("§a" + snapshot.getMelonsCount() + "§a Melonen")
                .produce();
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
