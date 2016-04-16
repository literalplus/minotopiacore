package io.github.xxyy.mtc.module.shop.ui.text.admin;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.misc.cmd.MTCPlayerOnlyCommandExecutor;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ui.text.ShopAction;
import io.github.xxyy.mtc.module.shop.ui.util.ShopActionHelper;
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
