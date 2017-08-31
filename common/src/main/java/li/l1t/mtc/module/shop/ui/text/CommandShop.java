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

import li.l1t.common.util.CommandHelper;
import li.l1t.mtc.misc.cmd.MTCPlayerOnlyCommandExecutor;
import li.l1t.mtc.module.shop.ShopModule;
import li.l1t.mtc.module.shop.ui.inventory.ShopListMenu;
import li.l1t.mtc.module.shop.ui.util.ShopActionHelper;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a text-based front-end to the Shop module.
 *
 * @author Janmm14, Literallie
 */
public class CommandShop extends MTCPlayerOnlyCommandExecutor { //TODO test (integration test) //TODO maybe allow console to use this command
    private final List<ShopAction> actionList = new ArrayList<>();
    private final ShopModule module;

    public CommandShop(ShopModule module) {
        this.module = module;
        actionList.add(new BuyShopAction(module));
        actionList.add(new SellShopAction(module));
        actionList.add(new PriceShopAction(module));
        actionList.add(new SearchShopAction(module));
    }

    @Override
    public boolean catchCommand(Player plr, String senderName, Command cmd, String label, String[] args) {
        if (!CommandHelper.checkPermAndMsg(plr, "mtc.shop.execute", label)) {
            return true;
        }

        if (args.length == 0) {
            ShopListMenu.openMenu(plr, module);
            return true;
        }

        if (ShopActionHelper.matchExecuteAction(actionList, plr, args, args[0])) {
            return true;
        }
        //no action found

        //if user was not asking for help, send unknown action before help
        if (!args[0].equalsIgnoreCase("help") && !args[0].equals("?")) {
            plr.sendMessage("§cUnbekannte Aktion §6" + args[0]);
        }

        sendHelp(plr);
        return true;
    }

    private void sendHelp(Player plr) {
        plr.sendMessage("§7======={§8§lMinoTopia Shop - Verfügbare Befehle§7}=======");
        actionList.stream()
                .filter(action -> action.getPermission() == null || plr.hasPermission(action.getPermission()))
                .forEach(action -> action.sendHelpLines(plr));
    }
}
