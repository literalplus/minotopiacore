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

package li.l1t.mtc.module.shop.ui.inventory.button;

import li.l1t.common.util.inventory.ItemStackFactory;
import li.l1t.mtc.module.shop.ui.inventory.ShopMenu;
import li.l1t.mtc.module.shop.ui.inventory.ShopSellMenu;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A button that forwards the player to a new {@link ShopSellMenu}.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-04-18
 */
public class OpenSellMenuButton implements MenuButton<ShopMenu> {
    public static final OpenSellMenuButton INSTANCE = new OpenSellMenuButton();

    @Override
    public ItemStack getItemStack(ShopMenu menu) {
        return new ItemStackFactory(Material.EMERALD)
                .displayName("§eItems verkaufen...")
                .lore("§7Klicke hier, um Items zu verkaufen.")
                .produce();
    }

    @Override
    public void handleMenuClick(InventoryClickEvent evt, ShopMenu menu) {
        ShopSellMenu.openMenu(menu.getPlayer(), menu.getModule());
    }
}
