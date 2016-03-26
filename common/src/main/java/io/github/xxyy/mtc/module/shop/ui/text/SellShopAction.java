package io.github.xxyy.mtc.module.shop.ui.text;

import io.github.xxyy.common.util.StringHelper;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ShopPriceCalculator;
import io.github.xxyy.mtc.module.shop.TransactionType;
import io.github.xxyy.mtc.module.shop.ui.util.ShopStringAdaptor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the sell action for the shop command.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-01
 */
class SellShopAction extends AbstractShopAction {
    private final ShopModule module;
    private final ShopTextOutput output;
    private final ShopPriceCalculator calculator;

    SellShopAction(ShopModule module) {
        super("shop", "verkaufen", 1, null, "sell", "s");
        this.module = module;
        output = module.getTextOutput();
        calculator = new ShopPriceCalculator(module.getItemManager());
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        switch (args[0].toLowerCase()) {
            case "hand":
            case "h":
                sellHand(args, plr);
                break;
            case "inv":
            case "all":
                sellInventory(plr);
                break;
            default:
                sellNamedItem(args, plr);
                break;
        }
    }

    private void sellHand(String[] args, Player plr) {
        int amount;
        ItemStack itemInHand = plr.getItemInHand();
        ShopItem item = module.getItemManager().getItem(itemInHand); //returns null for FullTag items

        if (!output.checkTradable(plr, item, "in deiner Hand", TransactionType.SELL)) {
            return;
        }

        if (args.length < 2) {
            amount = itemInHand.getAmount();
        } else {
            if (!StringUtils.isNumeric(args[1])) {
                plr.sendMessage("§cDie Anzahl der Items muss eine Zahl sein! (gegeben: " + args[2] + ")");
                return;
            }
            amount = Integer.parseInt(args[1]);
        }
        if (amount > itemInHand.getAmount()) {
            plr.sendMessage("§cDu hast nicht so viele Items in deiner Hand!");
            return;
        }

        module.getTransactionExecutor().attemptTransaction(plr, item, amount, TransactionType.SELL);
    }

    private void sellInventory(Player plr) {
        Map<ShopItem, Integer> itemAmounts = new HashMap<>();
        for (ItemStack stack : plr.getInventory().getContents()) {
            if (module.getItemManager().isTradeProhibited(stack)) {
                continue;
            }

            ShopItem item = module.getItemManager().getItem(stack);
            if (item.canBeSold()) { //add up current amount with this amount
                itemAmounts.compute(item, (existing, amount) -> stack.getAmount() + (amount == null ? 0 : amount));
            }
        }

        double totalProfit = itemAmounts.entrySet().stream()
                .mapToDouble(e -> {
                    boolean transactionSucceeded = module.getTransactionExecutor()
                            .attemptTransactionSilent(plr, e.getKey(), e.getValue(), TransactionType.SELL);
                    if (transactionSucceeded) {
                        return calculator.calculatePrice(e.getKey(), e.getValue(), TransactionType.SELL);
                    } else {
                        return 0D;
                    }
                }).sum();

        output.sendPrefixed(plr, "Du hast alles in deinem Inventar verkauft, was nicht niet- und nagelfest (unverkäuflich) war.");
        output.sendPrefixed(plr, "Du hast dadurch §e" + ShopStringAdaptor.getCurrencyString(totalProfit) + "§6 eingenommen.");
    }

    private void sellNamedItem(String[] args, Player plr) {
        String itemName = StringHelper.varArgsString(args, 1, 1, false); //last arg is amount, ignore that
        ShopItem item = module.getItemManager().getItem(itemName);
        if (!output.checkTradable(plr, item, item.getDisplayName(), TransactionType.SELL)) { //handles null
            return;
        }

        String lastArg = args[args.length - 1];
        int amount;
        if (lastArg.equalsIgnoreCase("all")) {
            amount = Arrays.stream(plr.getInventory().getContents())
                    .filter(item::matches) //TODO: This might be a problem with wildcard items
                    .mapToInt(ItemStack::getAmount)
                    .sum();
        } else {
            if (!StringUtils.isNumeric(lastArg)) {
                plr.sendMessage("§cDie Anzahl der Items muss eine Zahl sein! (gegeben: " + lastArg + ")");
                return;
            }
            amount = Integer.parseInt(lastArg);
        }

        module.getTransactionExecutor().attemptTransaction(plr, item, amount, TransactionType.SELL);
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<Item> <Anzahl>", "Verkauft Items.");
        sendHelpLine(plr, "<Item> all", "Verkauft alle Items vom Typ.");
        sendHelpLine(plr, "inv", "Verkauft dein ganzes Inventar.");
        sendHelpLine(plr, "hand", "Verkauft, was du in der Hand hast.");
    }
}
