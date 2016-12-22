/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.shop.ui.text.admin;

import li.l1t.common.chat.ComponentSender;
import li.l1t.common.util.CommandHelper;
import li.l1t.common.util.StringHelper;
import li.l1t.mtc.module.shop.api.ShopItem;
import li.l1t.mtc.module.shop.ShopModule;
import li.l1t.mtc.module.shop.ui.text.AbstractShopAction;
import li.l1t.mtc.module.shop.ui.util.ShopStringAdaptor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Admin action that displays or sets the sell worth of a shop item.
 *
 * @author Janmm14, xxyy
 */
class SellWorthAdminAction extends AbstractShopAction {
    private final ShopModule module;

    SellWorthAdminAction(ShopModule module) {
        super("shopadmin", "worth", 2, null, "sell");
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        double newSellWorth = -1;
        try {
            newSellWorth = Double.parseDouble(args[0]);
        } catch (NumberFormatException ignore) {
            //If it's not a double, they are probably querying the status -> not an error
        }

        String itemName = StringHelper.varArgsString(args, 1, false);
        ShopItem item = module.getItemManager().getItem(plr, itemName).orElse(null);
        if (module.getTextOutput().checkNonExistant(plr, item, itemName)) {
            return;
        }

        if (newSellWorth != -1) {
            handleSet(item, plr, newSellWorth);
        }

        sendWorthInfo(item, plr);
    }

    private void sendWorthInfo(ShopItem item, CommandSender sender) {
        if (item.canBeSold()) {
            ComponentSender.sendTo(module.getPrefixBuilder().append(item.getDisplayName(), ChatColor.YELLOW)
                            .append(" kann für ", ChatColor.GOLD)
                            .append(ShopStringAdaptor.getCurrencyString(module.getItemManager().getSellWorth(item)), ChatColor.YELLOW)
                            .append(" verkauft werden.", ChatColor.GOLD).create(),
                    sender);
        } else {
            ComponentSender.sendTo(module.getPrefixBuilder().append(item.getDisplayName(), ChatColor.YELLOW)
                            .append(" kann nicht verkauft werden.", ChatColor.GOLD).create(),
                    sender);
        }
    }

    private boolean handleSet(ShopItem item, CommandSender sender, double sellWorth) {
        if (item.isDiscountable() && sellWorth >= item.getDiscountedPrice()) {
            return !CommandHelper.msg(String.format(
                    "§cDer Verkaufswert §e%s §cmuss geringer als der reduzierte Preis §e%s §csein!",
                    sellWorth, item.getDiscountedPrice()
            ), sender);
        }
        if (sellWorth >= item.getBuyCost() && item.canBeBought()) {
            return !CommandHelper.msg(String.format(
                    "§cDer Verkaufswert §e%s §cmuss geringer als der Kaufpreis §e%s §csein!",
                    sellWorth, item.getBuyCost()
            ), sender);
        }

        item.setSellWorth(sellWorth);
        module.getItemConfig().updateItem(item);
        sender.sendMessage("§aNeuer Verkaufswert gesetzt:");
        return true;
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<neuer Verkaufswert> <Item>", "Setzt den Verkaufswert für ein Item");
    }
}
