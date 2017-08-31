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

package li.l1t.mtc.module.shop.ui.text.admin;

import li.l1t.common.chat.ComponentSender;
import li.l1t.common.util.CommandHelper;
import li.l1t.common.util.StringHelper;
import li.l1t.mtc.module.shop.ShopModule;
import li.l1t.mtc.module.shop.api.ShopItem;
import li.l1t.mtc.module.shop.ui.text.AbstractShopAction;
import li.l1t.mtc.module.shop.ui.util.ShopStringAdaptor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Admin action that displays or sets the buy cost of a shop item.
 *
 * @author xxyy, Janmm14
 */
class BuyCostAdminAction extends AbstractShopAction {
    private final ShopModule module;

    BuyCostAdminAction(ShopModule module) {
        super("shopadmin", "cost", 2, null, "buy");
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        double newBuyCost = -1;
        try {
            newBuyCost = Double.parseDouble(args[0]);
        } catch (NumberFormatException ignore) {
            //If it's not a double, they are probably querying the status -> not an error
        }

        String itemName = StringHelper.varArgsString(args, 1, false);
        ShopItem item = module.getItemManager().getItem(plr, itemName).orElse(null);
        if (module.getTextOutput().checkNonExistant(plr, item, itemName)) {
            return;
        }

        if (newBuyCost != -1) {
            handleSet(item, plr, newBuyCost);
        }

        sendCostInfo(item, plr);
    }

    private void sendCostInfo(ShopItem item, CommandSender sender) {
        if (item.canBeBought()) {
            ComponentSender.sendTo(module.getPrefixBuilder().append(item.getDisplayName(), ChatColor.YELLOW)
                            .append(" kann für ", ChatColor.GOLD)
                            .append(ShopStringAdaptor.getCurrencyString(module.getItemManager().getBuyCost(item)), ChatColor.YELLOW)
                            .append(" erworben werden.", ChatColor.GOLD).create(),
                    sender);
        } else {
            ComponentSender.sendTo(module.getPrefixBuilder().append(item.getDisplayName(), ChatColor.YELLOW)
                            .append(" kann nicht erworben werden.", ChatColor.GOLD).create(),
                    sender);
        }
    }

    private boolean handleSet(ShopItem item, CommandSender sender, double buyCost) {
        if (item.isDiscountable() && buyCost <= item.getDiscountedPrice()) {
            return !CommandHelper.msg(String.format(
                    "§cDer Kaufpreis §e%s §cmuss höher als der reduzierte Preis §e%s §csein!",
                    buyCost, item.getDiscountedPrice()
            ), sender);
        }
        if (buyCost <= item.getSellWorth()) {
            return !CommandHelper.msg(String.format(
                    "§cDer Kaufpreis §e%s §cmuss höher als der Verkaufswert §e%s §csein!",
                    buyCost, item.getSellWorth()
            ), sender);
        }

        item.setBuyCost(buyCost);
        module.getItemConfig().updateItem(item);
        sender.sendMessage("§aNeuer Kaufpreis gesetzt:");
        return true;
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<neuer Kaufpreis> <Item>", "Setzt den Kaufpreis für ein Item");
    }
}
