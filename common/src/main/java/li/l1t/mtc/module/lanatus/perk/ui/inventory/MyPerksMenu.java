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

package li.l1t.mtc.module.lanatus.perk.ui.inventory;

import li.l1t.common.inventory.gui.PaginationListMenu;
import li.l1t.mtc.module.lanatus.perk.api.Perk;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * An inventory menu that allows players to select from a list of perks.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-07
 */
public class MyPerksMenu extends PaginationListMenu<Perk> {
    private final BiConsumer<Perk, InventoryClickEvent> clickHandler;
    private final Function<Perk, ItemStack> iconFunction;

    public MyPerksMenu(Player player, Plugin plugin, BiConsumer<Perk, InventoryClickEvent> clickHandler,
                       Function<Perk, ItemStack> iconFunction) {
        super(plugin, player);
        this.clickHandler = clickHandler;
        this.iconFunction = iconFunction;
    }

    @Override
    protected void handleValueClick(Perk perk, InventoryClickEvent inventoryClickEvent) {
        clickHandler.accept(perk, inventoryClickEvent);
    }

    @Override
    protected ItemStack drawItem(Perk perk) {
        return iconFunction.apply(perk);
    }

    @Override
    protected String formatTitle(int currentPageNum, int pageCount) {
        return String.format("§9MinoTopia Perks (%d/%d)", currentPageNum, pageCount);
    }
}
