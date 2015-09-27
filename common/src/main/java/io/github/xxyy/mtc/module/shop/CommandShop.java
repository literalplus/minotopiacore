/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.common.util.StringHelper;
import io.github.xxyy.common.util.math.NumberHelper;
import io.github.xxyy.mtc.hook.VaultHook;
import io.github.xxyy.mtc.misc.cmd.MTCCommandExecutor;
import net.milkbowl.vault.economy.EconomyResponse;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides a text-based front-end to the Shop module.
 *
 * @author Janmm14, Literallie
 */ //FIXME: this class is too long, should use more abstraction layers, that we could then unit-test
class CommandShop extends MTCCommandExecutor { //TODO add help messages, test (integration test)
    private final ShopModule module;

    public CommandShop(ShopModule module) {
        this.module = module;
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
            //TODO open gui instead of sending help
            sendHelp(plr);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "preis":
            case "price":
            case "p": {
                if (args.length < 2) {
                    plr.sendMessage("§chalp wip price");
                    //TODO send help of that sub-cmd
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "hand":
                    case "held":
                    case "h": {
                        Boolean x = priceHand(plr);
                        if (x != null) {
                            return x;
                        }
                    }
                    case "inv":
                    case "all":
                    case "i": {
                        priceInventory(plr);
                        break;
                    }
                    default: {
                        priceNamedItem(args, plr);
                        break;
                    }
                }
                break;
            }
            case "kaufen":
            case "buy":
            case "b": {
                Boolean x = buy(args, plr);
                if (x != null) {
                    return x;
                }
            }
            case "verkaufen":
            case "sell":
            case "s": {
                if (args.length < 2) {
                    plr.sendMessage("§chalp wip sell");
                    //TODO send help of that sub-cmd
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "hand":
                    case "held":
                    case "h": {
                        Boolean x = sellHand(args, plr);
                        if (x != null) {
                            return x;
                        }
                        break;
                    }
                    case "inv":
                    case "all":
                    case "i": {
                        Boolean x = sellInventory(plr);
                        if (x != null) {
                            return x;
                        }
                        break;
                    }
                    default:
                        Boolean x = sellNamedItem(args, plr);
                        if (x != null) {
                            return x;
                        }
                        break;
                }
                break;
            }
            default:
                plr.sendMessage(String.format("§cUnbekannte Aktion '%s'", args[0]));
                //noinspection fallthrough //intended //nice one
            case "help":
                sendHelp(plr);
                break;
        }
        return true;
    }

    private Boolean sellNamedItem(String[] args, Player plr) {
        int amount;
        if (args[args.length - 1].equalsIgnoreCase("all")) {
            amount = -1;
        } else {
            amount = NumberHelper.tryParseInt(args[args.length - 1], Integer.MIN_VALUE);
        }
        boolean hasAmount = amount != Integer.MIN_VALUE;
        if (!hasAmount || amount == 0 || amount < -1) {
            amount = 1;
        }
        String itemName = StringHelper.varArgsString(hasAmount ? Arrays.copyOf(args, args.length - 1) : args, 2, false);
        ShopItem item = module.getItemConfig().getItem(itemName);
        if (item == null) {
            plr.sendMessage("§cDas Item in deiner Hand ist nicht im Shop handelbar.");
            return true;
        }
        if (!checkHasAccountAndMsg(plr)) {
            return true;
        }

        if (amount == -1) { // 3rd argument is "all", -1 equals all items of that type
            amount = (int) Arrays.stream(plr.getInventory().getContents())
                    .filter(is -> is.getType() == item.getMaterial()
                            && is.getData().getData() == item.getDataValue())
                    .count();
        }

        float worth = item.getSellWorth() * amount;

        EconomyResponse ecoResponse = module.getPlugin().getVaultHook().depositPlayer(plr, worth);
        if (!checkSuccessAndMsgLog(plr, ecoResponse, true, worth)) {
            return true;
        }

        int amountToRemove = amount;

        ItemStack[] contentsCopy = plr.getInventory().getContents();

        for (int i = 0; i < contentsCopy.length; i++) {
            ItemStack is = contentsCopy[i];
            if (is.getType() == item.getMaterial()
                    && is.getData().getData() == item.getDataValue()) {
                if (is.getAmount() <= amountToRemove) {
                    amountToRemove -= is.getAmount();
                    plr.getInventory().setItem(i, null);
                } else {
                    is.setAmount(is.getAmount() - amountToRemove);
                    plr.getInventory().setItem(i, is);
                    break;
                }
            }
        }
        plr.sendMessage("§6Das Item §e" + item.getDisplayName() + " wurde verkauft (§e" + amount + " §6Stück), dir wurden §e" + worth + " §6MineCoins gutgeschrieben.");
        return null;
    }

    private void priceNamedItem(String[] args, Player plr) {
        String name = StringHelper.varArgsString(args, 2, false);
        ShopItem item = module.getItemConfig().getItem(name);

        if (item == null) {
            plr.sendMessage("§cEs wurde kein Item mit dem Namen §6" + name + " §cgefunden oder es kann nicht im Shop gehandelt werden.");
        } else {
            plr.sendMessage("§e" + item.getDisplayName() + " §6kostet §e" + item.getBuyCost() + "§6 MineCoins.");
            plr.sendMessage("§6Bei Verkauf bekommst du §e" + item.getSellWorth() + " §6MineCoins.");
        }
    }

    private void priceInventory(Player plr) {
        float worth = 0;
        for (ItemStack stack : plr.getInventory()) {
            ShopItem item = module.getItemConfig().getItem(stack.getType(), stack.getData().getData());
            if (item != null) {
                worth += item.getSellWorth();
            }
        }

        plr.sendMessage("§6Dein Inventarinhalt ist §e" + worth + " §6MineCoins wert.");
    }

    @Nullable
    private Boolean priceHand(Player plr) {
        ItemStack hand = plr.getItemInHand();
        if (hand == null || hand.getType() == Material.AIR) {
            return CommandHelper.msg("§cDu hast kein Item in der Hand!", plr);
        }
        @SuppressWarnings("deprecation") // no other way for data value available //@formatter:off
                ShopItem item = module.getItemConfig().getItem(hand.getType(), hand.getData().getData()); //TODO maybe add ShopItemConfiguration#getItem(ItemStack)
        //@formatter:on
        if (item == null) {
            plr.sendMessage("§cDas Item in deiner Hand ist nicht im Shop handelbar.");
            return true;
        }
        plr.sendMessage("§6Ein §e" + item.getDisplayName() + " §6kostet §e" + item.getBuyCost() + "§6 MineCoins.");
        plr.sendMessage("§6Bei Verkauf bekommst du für eines §e" + item.getSellWorth() + " §6MineCoins.");
        plr.sendMessage("§6Alle Items in deiner Hand sind §6" + (item.getSellWorth() * hand.getAmount()) + " §6MineCoins wert.");
        return null;
    }

    @Nullable
    private Boolean buy(String[] args, Player plr) {
        if (args.length < 2) {
            plr.sendMessage("§chalp wip buy");
            //TODO send help of that sub-cmd
            return true;
        }

        int amount = NumberHelper.tryParseInt(args[args.length - 1], Integer.MIN_VALUE);
        boolean hasAmount = amount != Integer.MIN_VALUE;
        if (!hasAmount) {
            amount = 1;
        }
        String name = StringHelper.varArgsString(hasAmount ? Arrays.copyOf(args, args.length - 1) : args, 2, false);
        ShopItem item = module.getItemConfig().getItem(name);

        if (item == null) {
            plr.sendMessage("§cEs wurde kein Item mit dem Namen §6" + name + " §cgefunden oder es kann nicht im Shop gehandelt werden.");
            return true;
        }
        VaultHook vault = module.getPlugin().getVaultHook();
        if (!checkHasAccountAndMsg(plr)) {
            return true;
        }

        float totalCost = item.getBuyCost() * amount;
        double balance = vault.getBalance(plr);
        if (balance < totalCost) {
            plr.sendMessage("§cDu hast nicht genügend Geld. Dir fehlen §6" + (totalCost - balance) + " §cMineCoins.");
            return true;
        }
        EconomyResponse ecoResponse = vault.withdrawPlayer(plr, totalCost);
        if (!checkSuccessAndMsgLog(plr, ecoResponse, false, totalCost))
            return true;
        ItemStack itemStack = new ItemStack(item.getMaterial(), amount);
        MaterialData data = itemStack.getData();
        //noinspection deprecation
        data.setData(item.getDataValue());
        itemStack.setData(data);
        plr.getInventory().addItem(itemStack);
        plr.sendMessage("§6Du hast §e" + amount + "x " + item.getDisplayName() + " für " + totalCost + " MineCoins erworben.");
        return null;
    }

    @Nullable
    private Boolean sellHand(String[] args, Player plr) {
        int amount_;
        ItemStack hand = plr.getItemInHand();
        ShopItem item = module.getItemConfig().getItem(hand.getType(), hand.getData().getData());
        if (item == null) {
            plr.sendMessage("§cDas Item in deiner Hand ist nicht im Shop handelbar.");
            return true;
        }
        VaultHook vault = module.getPlugin().getVaultHook();
        if (!checkHasAccountAndMsg(plr)) return true;
        if (args.length > 2) {
            if (NumberUtils.isDigits(args[2])) {
                amount_ = NumberHelper.tryParseInt(args[2], -2);
            } else if (args[2].equalsIgnoreCase("all")) {
                amount_ = -1;
            } else {
                plr.sendMessage("§chalp wip sell");
                //TODO send help of that sub-cmd
                return true;
            }
        } else {
            amount_ = hand.getAmount();
        }
        if (amount_ == -2) { //3rd argument is digits, but cannot be parsed
            plr.sendMessage("§chalp wip sell");
            //TODO send help of that sub-cmd
            return true;
        }
        if (amount_ == -1) { // 3rd argument is "all" -1 equals all items of that type
            amount_ = (int) Arrays.stream(plr.getInventory().getContents())
                    .filter(is -> is.getType() == hand.getType()
                            && is.getData().getData() == hand.getData().getData())
                    .count();
        }
        final int amount = amount_;
        float worth = item.getSellWorth() * amount;
        EconomyResponse ecoResponse = vault.depositPlayer(plr, worth);
        if (!checkSuccessAndMsgLog(plr, ecoResponse, true, worth)) {
            return true;
        }
        int amountToRemove = amount;
        if (hand.getAmount() >= amountToRemove) {
            if (hand.getAmount() == amountToRemove) {
                plr.setItemInHand(null);
            } else {
                hand.setAmount(hand.getAmount() - amountToRemove);
                plr.setItemInHand(hand);
            }
        } else {
            int handAmount = hand.getAmount();
            plr.setItemInHand(null);
            amountToRemove -= handAmount;
            ItemStack[] iss = Arrays.copyOf(plr.getInventory().getContents(), plr.getInventory().getContents().length);
            for (int i = 0; i < iss.length; i++) {
                ItemStack is = iss[i];
                if (is.getType() == hand.getType()
                        && is.getData().getData() == hand.getData().getData()) {
                    if (is.getAmount() <= amountToRemove) {
                        amountToRemove -= is.getAmount();
                        plr.getInventory().setItem(i, null);
                    } else {
                        is.setAmount(is.getAmount() - amountToRemove);
                        plr.getInventory().setItem(i, is);
                        break;
                    }
                }
            }
        }
        plr.sendMessage("§6Das Item in deiner Hand wurde verkauft (§e" + amount + " §6Stück), dir wurden §e" + worth + " §6MineCoins gutgeschrieben.");
        return null;
    }

    @Nullable
    private Boolean sellInventory(Player plr) {
        VaultHook vault = module.getPlugin().getVaultHook();
        if (!checkHasAccountAndMsg(plr)) return true;

        float worth = 0;
        ItemStack[] invContent = plr.getInventory().getContents();
        Set<Integer> validItemPositions = new HashSet<>();

        for (int i = 0; i < invContent.length; i++) {
            final ItemStack stack = invContent[i];
            ShopItem item = module.getItemConfig().getItem(stack.getType(), stack.getData().getData());
            if (item != null) {
                worth += item.getSellWorth();
                validItemPositions.add(i);
            }
        }
        EconomyResponse ecoResponse = vault.depositPlayer(plr, worth);
        if (!checkSuccessAndMsgLog(plr, ecoResponse, true, worth))
            return true;

        PlayerInventory inventory = plr.getInventory();
        validItemPositions.forEach(i -> inventory.setItem(i, null));

        plr.sendMessage("§6Du hast dein Inventar verkauft. Es war §e" + worth + " §6MineCoins wert.");
        return null;
    }

    private boolean checkSuccessAndMsgLog(Player plr, EconomyResponse ecoResponse, boolean withdraw, float amount) {
        if (!ecoResponse.transactionSuccess()) {
            plr.sendMessage("§cAuf dein Konto konnte nicht zugegriffen werden.");
            module.getPlugin().getLogger().warning("[" + module.getName() + "] Konnte nicht auf Konto von " + plr + " zugreifen, um " + amount + " MineCoins " + (withdraw ? "gutzuschrieben" : "abzuziehen") + ".");
            module.getPlugin().getLogger().warning("[" + module.getName() + "] Fehlertyp: " + ecoResponse.type + " Fehler: " + ecoResponse.errorMessage);
            return false;
        }
        return true;
    }

    private boolean checkHasAccountAndMsg(Player plr) {
        VaultHook vault = module.getPlugin().getVaultHook();
        if (!vault.assureHasAccount(plr)) {
            plr.sendMessage("§cFehler bei der Verarbeitung. Dein Konto wurde nicht gefunden.");
            return false;
        }
        return true;
    }

    private void sendHelp(Player plr) {
        plr.sendMessage("§chalp wip bla");
        //TODO send help actually
    }
}
