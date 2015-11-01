/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.text;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.common.util.StringHelper;
import io.github.xxyy.mtc.misc.cmd.MTCCommandExecutor;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ShopPriceCalculator;
import io.github.xxyy.mtc.module.shop.TransactionType;
import io.github.xxyy.mtc.module.shop.transaction.ShopTransactionExecutor;
import io.github.xxyy.mtc.module.shop.ui.util.ShopStringAdaptor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides a text-based front-end to the Shop module.
 *
 * @author Janmm14, Literallie
 */ //FIXME: this class is too long, should use more abstraction layers, that we could then unit-test
public class CommandShop extends MTCCommandExecutor { //TODO add help messages, test (integration test)
    private final ShopModule module;
    private final ShopTextOutput output;
    private final ShopPriceCalculator calculator;
    private final ShopTransactionExecutor transactionExecutor;

    public CommandShop(ShopModule module) {
        this.module = module;
        output = new ShopTextOutput(module);
        calculator = new ShopPriceCalculator(module.getItemManager());
        transactionExecutor = new ShopTransactionExecutor(module);
    }

    @Override
    public boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
        if (CommandHelper.kickConsoleFromMethod(sender, label)) {
            return true;
        }
        Player plr = (Player) sender;
        if (!CommandHelper.checkPermAndMsg(sender, "mtc.shop.use", label)) { //TODO: less generic permission
            return true;
        }
        if (args.length == 0) {
            //TODO: open gui instead of sending help
            sendHelp(plr);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "preis":
            case "price":
            case "p": {
                if (args.length < 2) {
                    plr.sendMessage("§cInvalide Syntax. Versuche:");
                    plr.sendMessage("§6/shop preis <Item|hand|inv> §eFragt einen Preis ab.");
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "hand":
                    case "held":
                    case "h":
                        priceHand(plr);
                        break;
                    case "inv":
                    case "all":
                    case "i":
                        priceInventory(plr);
                        break;
                    default:
                        priceNamedItem(args, plr);
                        break;
                }
                break;
            }
            case "kaufen":
            case "buy":
            case "b":
                if (args.length < 3) {
                    plr.sendMessage("§cInvalide Syntax. Versuche:");
                    plr.sendMessage("§6/shop kaufen <Item> <Anzahl>");
                    return true;
                }
                buy(args, plr);
                break;
            case "verkaufen":
            case "sell":
            case "s":
                if (args.length < 2) {
                    plr.sendMessage("§cFalsche Syntax. Nutze diese hier:");
                    plr.sendMessage("§6/shop verkaufen <Itemname> <Anzahl|all> §c- §bItems verkaufen");
                    plr.sendMessage("§6/shop verkaufen inv §c- §bgesamtes Inventar verkaufen");
                    plr.sendMessage("§6/shop verkaufen hand <Anzahl>§c- §bItems in deiner Hand verkaufen");
                    plr.sendMessage("§6/shop verkaufen hand all §c- §bAlle Items im Inventar der Art des Items in deiner Hand verkaufen");
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "hand":
                    case "held":
                    case "h":
                        sellHand(args, plr);
                        break;
                    case "inv":
                    case "all":
                    case "i":
                    case "a":
                        sellInventory(plr);
                        break;
                    default:
                        sellNamedItem(args, plr);
                        break;
                }
                break;
            default:
                plr.sendMessage("§cUnbekannte Aktion: " + args[0]);
            //noinspection fallthrough //intended to send help if subcommand not found
            case "help":
                sendHelp(plr);
                break;
        }
        return true;
    }

    //  /shop verkaufen <Item> <Anzahl|all>
    private void sellNamedItem(String[] args, Player plr) {
        String itemName = StringHelper.varArgsString(args, 2, 1, false); //last arg is amount, ignore that
        ShopItem item = module.getItemManager().getItem(itemName);
        if (!output.checkTradable(plr, item, item.getDisplayName(), TransactionType.SELL)) { //handles null
            return;
        }

        String lastArg = args[args.length - 1];
        int amount;
        if (lastArg.equalsIgnoreCase("all")) {
            amount = Arrays.stream(plr.getInventory().getContents())
                    .filter(item::matches)
                    .mapToInt(ItemStack::getAmount)
                    .sum();
        } else {
            if (!StringUtils.isNumeric(lastArg)) {
                plr.sendMessage("§cDie Anzahl der Items muss eine Zahl sein! (gegeben: " + lastArg + ")");
                return;
            }
            amount = Integer.parseInt(lastArg);
        }

        transactionExecutor.attemptTransaction(plr, item, amount, TransactionType.SELL);
    }

    private void priceNamedItem(String[] args, Player plr) {
        String name = StringHelper.varArgsString(args, 2, false);
        output.sendPriceInfo(plr, module.getItemManager().getItem(name), "§e\"" + name + "\"§6");
    }

    private void priceInventory(Player plr) {
        plr.sendMessage("§6Dein Inventarinhalt ist §e" +
                ShopStringAdaptor.getCurrencyString(
                        calculator.sumInventoryPrices(plr, TransactionType.SELL)
                ) +
                " wert.");
    }

    private void priceHand(Player plr) {
        ItemStack itemInHand = plr.getItemInHand();
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            output.sendPrefixed(plr, "§cDu hast nichts in der Hand!");
            return;
        }

        output.sendPriceInfo(plr, module.getItemManager().getItem(itemInHand), "in deiner Hand");
    }

    //buy0 item1 amount2
    private void buy(String[] args, Player plr) {
        if (!StringUtils.isNumeric(args[2])) {
            plr.sendMessage("§cDie Anzahl der Items muss eine Zahl sein! (gegeben: " + args[2] + ")");
            return;
        }

        int amount = Integer.parseInt(args[2]);
        String itemName = StringHelper.varArgsString(args, 2, 1, false);
        ShopItem item = module.getItemManager().getItem(itemName);

        if (!output.checkTradable(plr, item, itemName, TransactionType.BUY)) {
            return;
        }

        transactionExecutor.attemptTransaction(plr, item, amount, TransactionType.BUY);
    }

    private void sellHand(String[] args, Player plr) {
        int amount;
        ItemStack itemInHand = plr.getItemInHand();
        ShopItem item = module.getItemManager().getItem(itemInHand);
        if (!output.checkTradable(plr, item, "in deiner Hand", TransactionType.SELL)) {
            return;
        }

        if (args.length < 3 || args[2].equalsIgnoreCase("all")) {
            amount = itemInHand.getAmount();
        } else {
            if (!StringUtils.isNumeric(args[2])) {
                plr.sendMessage("§cDie Anzahl der Items muss eine Zahl sein! (gegeben: " + args[2] + ")");
                return;
            }
            amount = Integer.parseInt(args[2]);
        }

        transactionExecutor.attemptTransaction(plr, item, amount, TransactionType.SELL);
    }

    private void sellInventory(Player plr) {
        Map<ShopItem, Integer> itemAmounts = new HashMap<>();
        for (ItemStack stack : plr.getInventory().getContents()) {
            ShopItem item = module.getItemManager().getItem(stack);
            if (item.canBeSold()) { //add up current amount with this amount
                itemAmounts.compute(item, (existing, amount) -> stack.getAmount() + (amount == null ? 0 : amount));
            }
        }

        double totalProfit = itemAmounts.entrySet().stream()
                .mapToDouble(e -> {
                    transactionExecutor.attemptTransactionSilent(plr, e.getKey(), e.getValue(), TransactionType.SELL);
                    return calculator.calculatePrice(e.getKey(), e.getValue(), TransactionType.SELL);
                }).sum();

        output.sendPrefixed(plr, "Du hast alles in deinem Inventar verkauft, was nicht niet- und nagelfest (unverkäuflich) ist.");
        output.sendPrefixed(plr, "Du hast dadurch §e" + ShopStringAdaptor.getCurrencyString(totalProfit) + "§6 eingenommen.");
    }

    private void sendHelp(Player plr) {
        plr.sendMessage("§b[]----- §5MinoTopia Shop Hilfe §b-----[]");
        plr.sendMessage("§6/shop preis <Itemname|Hand|Inv> §c- §bPreise im Shop");
        plr.sendMessage("§6/shop kaufen <Itemname> <Anzahl> §c- §bItems kaufen");
        plr.sendMessage("§6/shop verkaufen <Itemname> <Anzahl|all> §c- §bItems verkaufen");
        plr.sendMessage("§6/shop verkaufen inv §c- §bgesamtes Inventar verkaufen");
        plr.sendMessage("§6/shop verkaufen hand §c- §bItems in deiner Hand verkaufen");
        plr.sendMessage("§6/shop verkaufen hand all §c- §bAlle Items im Inventar der Art des Items in deiner Hand verkaufen");
        plr.sendMessage("§b[]----------------------------[]");
    }
}
