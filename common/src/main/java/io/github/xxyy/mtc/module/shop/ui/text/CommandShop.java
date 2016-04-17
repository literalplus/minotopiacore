/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.text;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.misc.cmd.MTCPlayerOnlyCommandExecutor;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ui.inventory.ShopListMenu;
import io.github.xxyy.mtc.module.shop.ui.util.ShopActionHelper;
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
