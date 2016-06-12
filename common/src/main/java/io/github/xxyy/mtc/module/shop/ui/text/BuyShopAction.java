package io.github.xxyy.mtc.module.shop.ui.text;

import io.github.xxyy.common.util.StringHelper;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.TransactionType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

/**
 * Handles the buy action for the shop command.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-01
 */
class BuyShopAction extends AbstractShopAction {
    private final ShopModule module;

    BuyShopAction(ShopModule module) {
        super("shop", "kaufen", 2, null, "buy", "b", "k");
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        String finalArg = args[args.length - 1];
        if (!StringUtils.isNumeric(finalArg)) {
            plr.sendMessage("§cDie Anzahl der Items muss eine Zahl sein! (gegeben: " + finalArg + ")");
            return;
        }

        int amount = Integer.parseInt(finalArg);
        String itemName = StringHelper.varArgsString(args, 1, 1, false);
        ShopItem item = module.getItemManager().getItem(itemName);

        if (!module.getTextOutput().checkTradable(plr, item, itemName, TransactionType.BUY)) {
            return;
        }

        module.getTransactionExecutor().attemptTransaction(plr, item, amount, TransactionType.BUY);
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<Item> <Anzahl>", "Kauft ein Item.");
    }
}
