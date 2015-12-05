package io.github.xxyy.mtc.module.shop.ui.text;

import io.github.xxyy.common.util.StringHelper;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ShopPriceCalculator;
import io.github.xxyy.mtc.module.shop.TransactionType;
import io.github.xxyy.mtc.module.shop.ui.util.ShopStringAdaptor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Handles the price action for the shop command.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-01
 */
public class PriceShopAction extends AbstractShopAction {
    private final ShopModule module;
    private final ShopTextOutput output;
    private final ShopPriceCalculator calculator;

    protected PriceShopAction(CommandShop baseCommand) {
        super("shop", "preis", 1, null, "price");
        this.module = baseCommand.getModule();
        output = module.getTextOutput();
        calculator = new ShopPriceCalculator(module.getItemManager());
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        switch (args[1].toLowerCase()) {
            case "hand":
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
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<Item|hand|inv>", "Fragt einen Preis ab.");
        sendHelpLine(plr, "<Item> <Anzahl>", "Berechnet einem Preis für eine bestimmte Anzahl.");
    }

    private void priceNamedItem(String[] args, Player plr) {
        String lastArg = args[args.length - 1];
        int amount = 1;
        int stripArgs = 0; //amount of arguments to ignore for item name, starting with the last one
        if (StringUtils.isNumeric(lastArg)) {
            amount = Integer.parseInt(lastArg);
            stripArgs = 1;
        }

        String name = StringHelper.varArgsString(args, 0, stripArgs, false);
        ShopItem item = module.getItemManager().getItem(name);
        output.sendPriceInfo(plr, item, amount, "§e\"" + name + "\"§6");
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

        ShopItem item = module.getItemManager().getItem(itemInHand);
        output.sendPriceInfo(plr, item, itemInHand.getAmount(), "in deiner Hand");
    }
}
