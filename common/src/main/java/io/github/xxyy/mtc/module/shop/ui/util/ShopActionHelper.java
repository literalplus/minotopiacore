package io.github.xxyy.mtc.module.shop.ui.util;

import io.github.xxyy.mtc.module.shop.ui.text.ShopAction;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public final class ShopActionHelper {

    private ShopActionHelper() {
        throw new UnsupportedOperationException();
    }

    public static boolean matchExecuteAction(List<ShopAction> actionList, Player plr, String[] args, String label) {
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
        return false;
    }
}
