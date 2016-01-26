package io.github.xxyy.mtc.module.shop.ui.util;

import io.github.xxyy.mtc.module.shop.ui.text.ShopAction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public final class ShopActionHelper {

    private static final Pattern NOBROADCAST_SPACE_INFRONT = Pattern.compile(" -nobroadcast", Pattern.LITERAL);
    private static final Pattern NOBROADCAST_SPACE_BEHIND = Pattern.compile("-nobroadcast ", Pattern.LITERAL);

    private ShopActionHelper() {
        throw new UnsupportedOperationException();
    }

    public static boolean matchExecuteAction(List<ShopAction> actionList, Player plr, String[] args, String label) {
        for (ShopAction action : actionList) {
            if (action.matches(args[0])) {
                if (action.getPermission() != null && !plr.hasPermission(action.getPermission())) {
                    plr.sendMessage("§cDu darfst /" + label + " " + action.getDisplayName() + " nicht verwenden!");
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

    /**
     * Checks whether {@code " -nobroadcast"} or {@code "-nobroadcast "} occurr in the given String and
     * replaces any occurence then in the referenced string and sets the reference to the new string.
     * @param itemName the reference to the current item name string, reference may not be null too
     * @return whether one of the two -nobroadcast strings were found
     */
    public static boolean isNoBroadcast(@NotNull SimpleReference<String> itemName) {
        String itemNameStr = itemName.get();
        if (NOBROADCAST_SPACE_BEHIND.matcher(itemNameStr).matches() ||
                NOBROADCAST_SPACE_INFRONT.matcher(itemNameStr).matches()) {
            itemNameStr = NOBROADCAST_SPACE_BEHIND.matcher(itemNameStr).replaceAll("");
            itemName.set(NOBROADCAST_SPACE_INFRONT.matcher(itemNameStr).replaceAll(""));
            return false;
        } else {
            return true;
        }
    }
}
