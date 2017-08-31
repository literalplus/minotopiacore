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

package li.l1t.mtc.module.shop.ui.text;

import li.l1t.common.util.StringHelper;
import li.l1t.mtc.module.shop.ShopModule;
import li.l1t.mtc.module.shop.TransactionType;
import li.l1t.mtc.module.shop.api.ShopItem;
import li.l1t.mtc.module.shop.ui.inventory.ShopDetailMenu;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

/**
 * Handles the buy action for the shop command.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-01
 */
class BuyShopAction extends AbstractShopAction {
    private final ShopModule module;

    BuyShopAction(ShopModule module) {
        super("shop", "kaufen", 1, null, "buy", "b", "k");
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        String finalArg = args[args.length - 1];
        int amount = -1;
        int ignoreArgsEnd = 0; //how many args to strip from the name, from right to left
        if (StringUtils.isNumeric(finalArg)) {
            amount = Integer.parseInt(finalArg);
            ignoreArgsEnd = 1;
        }

        String itemName = StringHelper.varArgsString(args, 0, ignoreArgsEnd, false);
        ShopItem item = module.getItemManager().getItem(itemName).orElse(null);

        if (!module.getTextOutput().checkTradable(plr, item, itemName, TransactionType.BUY)) {
            return;
        }

        if (amount == -1) {
            ShopDetailMenu.openMenu(plr, module, item);
        } else {
            module.getTransactionExecutor().attemptTransaction(plr, item, amount, TransactionType.BUY);
        }
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<Item> <Anzahl>", "Kauft ein Item.");
    }
}
