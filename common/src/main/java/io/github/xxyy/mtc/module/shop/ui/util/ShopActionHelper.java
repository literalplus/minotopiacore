package io.github.xxyy.mtc.module.shop.ui.util;

import io.github.xxyy.lib.guava17.base.Joiner;
import io.github.xxyy.mtc.module.shop.ui.text.ShopAction;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Static helper class for dealing with shop actions.
 *
 * @author Janmm14
 */
public final class ShopActionHelper {
    private ShopActionHelper() {
    }

    public static boolean matchExecuteAction(List<ShopAction> actionList, Player plr, String[] args, String label) {
        for (ShopAction action : actionList) {
            if (action.matches(args[0])) {
                attemptExecution(plr, args, label, action);
                return true;
            }
        }

        //In most cases, we'll have an exact match, so an extra loop makes sense here
        List<ShopAction> matchedActions = new ArrayList<>();
        for (ShopAction action : actionList) {
            if (action.fuzzyMatches(args[0])) {
                matchedActions.add(action);
                return true;
            }
        }

        if (matchedActions.size() == 1) {
            attemptExecution(plr, args, label, matchedActions.get(0));
            return true;
        } else if (!matchedActions.isEmpty()) {
            plr.sendMessage("§cMeintest du: §e/" + label + " " + Joiner.on("§c, §e")
                    .join(matchedActions.stream().map(ShopAction::getDisplayName).iterator()) + " §c...?");
        }

        return false;
    }

    public static void attemptExecution(Player plr, String[] args, String label, ShopAction action) {
        if (action.getPermission() != null && !plr.hasPermission(action.getPermission())) {
            plr.sendMessage("§cDu darfst /" + label + " " + action.getDisplayName() + " nicht verwenden!");
        } else if (action.getMinimumArguments() > args.length - 1) {
            plr.sendMessage("§cInvalide Syntax, zu wenige Argumente:");
            action.sendHelpLines(plr);
        } else {
            action.execute(Arrays.copyOfRange(args, 1, args.length, String[].class), plr, args[0]);
        }
    }
}
