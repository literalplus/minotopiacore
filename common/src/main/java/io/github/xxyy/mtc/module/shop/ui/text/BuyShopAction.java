package io.github.xxyy.mtc.module.shop.ui.text;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import io.github.xxyy.common.util.StringHelper;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.TransactionType;
import io.github.xxyy.mtc.module.shop.ui.inventory.ShopDetailMenu;

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
            //plr.sendMessage("§cDie Anzahl der Items muss eine Zahl sein! (gegeben: " + finalArg + ")");
            //return;
            amount = Integer.parseInt(finalArg);
            ignoreArgsEnd = 1;
        }

        String itemName = StringHelper.varArgsString(args, 0, ignoreArgsEnd, false);
        ShopItem item = module.getItemManager().getItem(itemName);

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
