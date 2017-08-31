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

package li.l1t.mtc.module.shop.ui.util;

import com.google.common.base.Joiner;
import li.l1t.mtc.module.shop.ui.text.ShopAction;
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
