/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.text.admin;

import io.github.xxyy.common.chat.ComponentSender;
import io.github.xxyy.common.chat.XyComponentBuilder;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.common.util.StringHelper;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ui.text.AbstractShopAction;
import io.github.xxyy.mtc.module.shop.ui.util.ShopStringAdaptor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Admin action that displays or sets the discounted price for shop items.
 *
 * @author Janmm14, xxyy
 */
class DiscountAdminAction extends AbstractShopAction {
    private final ShopModule module;

    DiscountAdminAction(ShopModule module) {
        super("shopadmin", "discount", 1, null);
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        int ignoreArgsEnd = args.length == 1 ? 0 : 1; //only strip args if we have anything left then
        String itemName = StringHelper.varArgsString(args, 0, ignoreArgsEnd, false);
        ShopItem item = module.getItemManager().getItem(plr, itemName);
        if (module.getTextOutput().checkNonExistant(plr, item, itemName)) {
            return;
        }

        if (args.length == 1) {
            sendDiscountInfo(item, plr);
            return;
        }

        try {
            double newDiscountPrice = Double.parseDouble(args[args.length - 1]);
            handleSet(item, plr, newDiscountPrice);
            sendDiscountInfo(item, plr);
        } catch (NumberFormatException nfe) {
            if (args.length > 1) {
                plr.sendMessage("§cReduzierter Preis muss eine Zahl sein!");
            }
        }
    }

    private void sendDiscountInfo(ShopItem item, CommandSender sender) {
        XyComponentBuilder builder = new XyComponentBuilder("Das Item ", ChatColor.GOLD)
                .append(item.getDisplayName(), ChatColor.YELLOW).event(module.getTextOutput().createItemHover(item))
                .append(" ", ChatColor.GOLD, ComponentBuilder.FormatRetention.FORMATTING);

        if (item.isDiscountable()) {
            builder.append("kostet im reduzierten Status ")
                    .append(ShopStringAdaptor.getCurrencyString(item.getDiscountedPrice()), ChatColor.YELLOW)
                    .append(" (-" + item.getDiscountPercentage() + "%)");
        } else {
            builder.append("kann nicht reduziert werden");
        }

        ComponentSender.sendTo(builder.append(".", ChatColor.GOLD).create(), sender);
    }

    private boolean handleSet(ShopItem item, CommandSender sender, double discountedPrice) {
        if (discountedPrice < item.getSellWorth()) {
            return !CommandHelper.msg(String.format(
                    "§cDer reduzierte Preis §e%s §ckann nicht geringer als der Verkaufswert §e%s §csein!",
                    discountedPrice, item.getSellWorth()
            ), sender);
        }
        if (discountedPrice >= item.getBuyCost()) {
            return !CommandHelper.msg(String.format(
                    "§cDer reduzierte Preis §e%s §cmuss geringer als der Kaufpreis §e%s §csein!",
                    discountedPrice, item.getBuyCost()
            ), sender);
        }

        item.setDiscountedPrice(discountedPrice);
        module.getItemConfig().updateItem(item);
        sender.sendMessage("§aNeuer reduzierter Preis gesetzt:");
        return true;
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<Item> <reduzierter Preis>", "Setzt den reduzierten Preis für ein Item (0 = aus)");
        sendHelpLine(plr, "<Item>", "Zeigt den reduzierten Preis für ein Item");
    }
}
