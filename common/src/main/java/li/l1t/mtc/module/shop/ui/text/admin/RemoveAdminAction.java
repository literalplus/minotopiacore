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

package li.l1t.mtc.module.shop.ui.text.admin;

import li.l1t.common.util.StringHelper;
import li.l1t.mtc.module.shop.ShopModule;
import li.l1t.mtc.module.shop.api.ShopItem;
import li.l1t.mtc.module.shop.api.ShopItemManager;
import li.l1t.mtc.module.shop.ui.text.AbstractShopAction;
import org.bukkit.entity.Player;

/**
 * Admin action that removes an existing shop item from the shop.
 *
 * @author xxyy, Janmm14
 */
class RemoveAdminAction extends AbstractShopAction {
    private final ShopModule module;

    RemoveAdminAction(ShopModule module) {
        super("shopadmin", "remove", 1, null);
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        String itemName = StringHelper.varArgsString(args, 0, false);
        ShopItemManager itemManager = module.getItemManager();
        ShopItem item = itemManager.getItem(itemName).orElse(null);
        if (module.getTextOutput().checkNonExistant(plr, item, itemName)) {
            return;
        }
        module.getItemManager().unregisterItem(item);
        module.getItemConfig().asyncSave(module.getPlugin());
        plr.sendMessage("§aDas Item §6" + item.getDisplayName() + "§a wurde aus dem Shop entfernt.");
        if (itemManager.getDiscountManager().isDiscounted(item)) {
            ShopItem discountedItem = itemManager.getDiscountManager().selectDiscountedItem(itemManager);
            if (discountedItem != null) {
                module.getTextOutput().announceDiscount(discountedItem);
            }
        }
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<Item>", "Entfernt ein Item aus dem Shop.");
    }
}
