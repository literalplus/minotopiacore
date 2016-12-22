/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.shop.ui.text;

import li.l1t.common.exception.UserException;
import li.l1t.common.util.StringHelper;
import li.l1t.mtc.module.shop.ShopModule;
import li.l1t.mtc.module.shop.ShopPriceCalculator;
import li.l1t.mtc.module.shop.TransactionType;
import li.l1t.mtc.module.shop.api.ShopItem;
import li.l1t.mtc.module.shop.ui.inventory.ShopSellMenu;
import li.l1t.mtc.module.shop.ui.util.ShopStringAdaptor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

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
        super("shop", "verkaufen", 0, null, "sell", "s");
        this.module = module;
        output = module.getTextOutput();
        calculator = new ShopPriceCalculator(module.getItemManager());
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        if (args.length == 0) {
            ShopSellMenu.openMenu(plr, module);
            return;
        }

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
        ItemStack itemInHand = plr.getInventory().getItemInMainHand();
        ShopItem item = module.getItemManager().getItem(itemInHand)
                .orElseThrow(() -> new UserException("Dieses Item gibt es nicht."));

        if (!output.checkTradable(plr, item, "in deiner Hand", TransactionType.SELL)) {
            return;
        }

        if (args.length < 2) {
            amount = itemInHand.getAmount();
        } else {
            if (!StringUtils.isNumeric(args[1])) {
                plr.sendMessage("§cDie Anzahl der Items muss eine Zahl sein! (gegeben: " + args[1] + ")");
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
        for (ItemStack stack : plr.getInventory().getStorageContents()) {
            Optional.ofNullable(stack)
                    .flatMap(module.getItemManager()::getItem)
                    .filter(ShopItem::canBeSold)
                    .ifPresent(item -> itemAmounts.compute(item, mergeAmount(stack)));
        }
        double totalProfit = itemAmounts.entrySet().stream()
                .filter(e -> module.getTransactionExecutor()
                        .attemptTransactionSilent(plr, e.getKey(), e.getValue(), TransactionType.SELL))
                .mapToDouble(e -> calculator.calculatePrice(e.getKey(), e.getValue(), TransactionType.SELL))
                .sum();
        output.sendPrefixed(plr, "Du hast alles in deinem Inventar verkauft, was nicht niet- und nagelfest (unverkäuflich) war.");
        output.sendPrefixed(plr, "Du hast dadurch §e" + ShopStringAdaptor.getCurrencyString(totalProfit) + "§6 eingenommen.");
    }

    private BiFunction<ShopItem, Integer, Integer> mergeAmount(ItemStack stack) {
        return (existing, amount) -> stack.getAmount() + (amount == null ? 0 : amount);
    }

    private void sellNamedItem(String[] args, Player plr) {
        String itemName = StringHelper.varArgsString(args, 0, 1, false); //last arg is amount, ignore that
        ShopItem item = module.getItemManager().getItem(itemName)
                .orElseThrow(() -> new UserException("So ein Item gibt es nicht: '%s'", itemName));
        if (!output.checkTradable(plr, item, itemName, TransactionType.SELL)) {
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
