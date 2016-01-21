package io.github.xxyy.mtc.module.shop.ui.text.admin;

import com.google.common.base.Joiner;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ui.text.AbstractShopAction;
import io.github.xxyy.mtc.module.shop.ui.util.ShopStringAdaptor;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SetWorthShopAdminAction extends AbstractShopAction {
    private static final Joiner SPACE_JOINER = Joiner.on(' ');
    private final ShopModule module;

    protected SetWorthShopAdminAction(ShopModule module) {
        super("shopadmin", "setworth", 2, null);
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        String itemName = SPACE_JOINER.join(Arrays.copyOfRange(args, 1, args.length));
        ShopItem item = module.getItemManager().getItem(itemName);
        if (item == null) {
            plr.sendMessage("§cItem §6" + itemName + "§c nicht gefunden.");
            return;
        }
        try {
            double price = Double.parseDouble(args[0]);
            if (price >= item.getBuyCost()) {
                plr.sendMessage("§cDu kannst den Verkaufspreis nicht geringer als den Kaufpreis machen.");
                return;
            }
            item.setSellWorth(price);
            module.getItemConfig().asyncSave(module.getPlugin());
            plr.sendMessage("§aDas Item §6" + item.getDisplayName() + "§a kann nun für §6" + ShopStringAdaptor.getCurrencyString(price) + "§a verkauft werden.");
        } catch (NumberFormatException ignored) {
            plr.sendMessage("§cDer Preis muss eine Zahl sein!");
        }
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<preis> <shopitem>", "Setzt den Verkaufspreis eines Items");
    }
}
