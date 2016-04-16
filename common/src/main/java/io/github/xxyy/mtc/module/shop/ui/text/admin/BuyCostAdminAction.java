package io.github.xxyy.mtc.module.shop.ui.text.admin;

import io.github.xxyy.common.chat.ComponentSender;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.common.util.StringHelper;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ui.text.AbstractShopAction;
import io.github.xxyy.mtc.module.shop.ui.util.ShopStringAdaptor;
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
        ShopItem item = module.getItemManager().getItem(plr, itemName);
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
                            .append(ShopStringAdaptor.getCurrencyString(item.getManager().getBuyCost(item)), ChatColor.YELLOW)
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
        module.getItemConfig().asyncSave(module.getPlugin());
        sender.sendMessage("§aNeuer Kaufpreis gesetzt:");
        return true;
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<neuer Kaufpreis> <Item>", "Setzt den Kaufpreis für ein Item");
    }
}
