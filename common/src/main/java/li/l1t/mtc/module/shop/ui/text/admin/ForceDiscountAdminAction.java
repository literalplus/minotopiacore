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

import li.l1t.mtc.module.shop.ShopModule;
import li.l1t.mtc.module.shop.api.ShopItem;
import li.l1t.mtc.module.shop.ui.text.AbstractShopAction;
import org.bukkit.entity.Player;

/**
 * Admin action that forces the discount manager to discount a new item.
 *
 * @author Janmm14, xxyy
 */
class ForceDiscountAdminAction extends AbstractShopAction {
    private final ShopModule module;

    ForceDiscountAdminAction(ShopModule module) {
        super("shopadmin", "forcediscount", 0, null, "newdiscount");
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        ShopItem discountedItem = module.getItemManager().getDiscountManager().selectDiscountedItem(module.getItemManager());

        if (discountedItem == null) {
            plr.sendMessage("§cEs gibt keine reduzierbaren Items.");
            return;
        }
        module.getTextOutput().announceDiscount(discountedItem);
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "", "Generiert ein neues Sonderangebot.");
    }
}
