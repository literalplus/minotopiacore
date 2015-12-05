/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.text;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.misc.cmd.MTCCommandExecutor;
import io.github.xxyy.mtc.module.shop.ShopModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides a text-based front-end to the Shop module.
 *
 * @author Janmm14, Literallie
 */
public class CommandShop extends MTCCommandExecutor { //TODO test (integration test)
    private final ShopModule module;
    private final List<ShopAction> actionList = new ArrayList<>();

    public CommandShop(ShopModule module) {
        this.module = module;
        actionList.add(new BuyShopAction(module));
        actionList.add(new SellShopAction(module));
        actionList.add(new PriceShopAction(module));
    }

    @Override
    public boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
        if (CommandHelper.kickConsoleFromMethod(sender, label)) {
            return true;
        }
        Player plr = (Player) sender;
        if (!CommandHelper.checkPermAndMsg(sender, "mtc.shop.execute", label)) {
            return true;
        }

        if (args.length == 0) {
            //TODO: open gui instead of sending help
            sendHelp(plr);
            return true;
        }

        for (ShopAction action : actionList) {
            if (action.matches(args[0])) {
                if (action.getPermission() != null && !plr.hasPermission(action.getPermission())) {
                    plr.sendMessage("§cDu darfst /shop " + action.getDisplayName() + " nicht verwenden!");
                } else if (action.getMinimumArguments() <= args.length - 1) {
                    plr.sendMessage("§cInvalide Syntax. Versuche:");
                    action.sendHelpLines(plr);
                } else {
                    action.execute(Arrays.copyOfRange(args, 1, args.length, String[].class), plr, args[0]);
                }
                return true;
            }
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
        plr.sendMessage("§9[]------------- §5MinoTopiaShop§9 -------------[]");
        actionList.stream()
            .filter(action -> action.getPermission() == null || plr.hasPermission(action.getPermission()))
            .forEach(action -> action.sendHelpLines(plr));
        plr.sendMessage("§b[]---------------------------------------------[]");
    }
}
