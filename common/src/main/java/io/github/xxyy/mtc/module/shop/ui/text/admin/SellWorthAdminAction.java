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
 * Admin action that displays or sets the sell worth of a shop item.
 *
 * @author Janmm14, xxyy
 */
public class SellWorthAdminAction extends AbstractShopAction {
    private final ShopModule module;

    protected SellWorthAdminAction(ShopModule module) {
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

        String itemName = StringHelper.varArgsString(args, 0, false);
        ShopItem item = module.getItemManager().getItem(plr, itemName);
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
                            .append(ShopStringAdaptor.getCurrencyString(item.getManager().getSellWorth(item)), ChatColor.YELLOW)
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
        if (sellWorth >= item.getBuyCost()) {
            return !CommandHelper.msg(String.format(
                    "§cDer Verkaufswert §e%s §cmuss geringer als der Kaufpreis §e%s §csein!",
                    sellWorth, item.getBuyCost()
            ), sender);
        }

        item.setSellWorth(sellWorth);
        module.getItemConfig().asyncSave(module.getPlugin());
        sender.sendMessage("§aNeuer Verkaufswert gesetzt:");
        return true;
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<neuer Verkaufswert> <Item>", "Setzt den Verkaufswert für ein Item");
    }
}
