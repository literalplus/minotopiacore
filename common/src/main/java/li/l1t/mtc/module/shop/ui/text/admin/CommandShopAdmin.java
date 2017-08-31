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

import li.l1t.common.util.CommandHelper;
import li.l1t.mtc.misc.cmd.MTCPlayerOnlyCommandExecutor;
import li.l1t.mtc.module.shop.ShopModule;
import li.l1t.mtc.module.shop.ui.text.ShopAction;
import li.l1t.mtc.module.shop.ui.util.ShopActionHelper;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a text-based shop administration front-end.
 *
 * @author Janmm14, Literallie
 */
public class CommandShopAdmin extends MTCPlayerOnlyCommandExecutor {
    private final ShopModule module;
    private final List<ShopAction> actionList = new ArrayList<>();

    public CommandShopAdmin(ShopModule module) {
        this.module = module;
        actionList.add(new AddAdminAction(module));
        actionList.add(new RemoveAdminAction(module));
        actionList.add(new AliasAdminAction(module));
        actionList.add(new BuyCostAdminAction(module));
        actionList.add(new SellWorthAdminAction(module));
        actionList.add(new ForceDiscountAdminAction(module));
        actionList.add(new DiscountAdminAction(module));
        actionList.add(new InfoAdminAction(module));
        actionList.add(new ReloadAdminAction(module));
    }

    @Override
    public boolean catchCommand(Player plr, String plrName, Command cmd, String label, String[] args) {
        if (!CommandHelper.checkPermAndMsg(plr, "mtc.shop.admin", label)) {
            return true;
        }

        if (args.length == 0) {
            sendHelp(plr);
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
        plr.sendMessage("§7========{§8§lMinoTopia Shop: Verwaltungsbefehle§7}========");
        actionList.stream()
                .filter(action -> action.getPermission() == null || plr.hasPermission(action.getPermission()))
                .forEach(action -> action.sendHelpLines(plr));
    }

    public ShopModule getModule() {
        return module;
    }
}
