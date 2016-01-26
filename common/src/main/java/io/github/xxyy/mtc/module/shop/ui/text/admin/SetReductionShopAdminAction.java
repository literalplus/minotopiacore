/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.text.admin;

import com.google.common.base.Joiner;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ui.text.AbstractShopAction;
import io.github.xxyy.mtc.module.shop.ui.util.ShopStringAdaptor;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SetReductionShopAdminAction extends AbstractShopAction {
    private static final Joiner SPACE_JOINER = Joiner.on(' ');
    private final ShopModule module;

    protected SetReductionShopAdminAction(ShopModule module) {
        super("shopadmin", "setreduction", 2, null);
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
            double reductionPercent = Integer.parseInt(args[0]);
            reductionPercent /= 100.0D;
            double previousReductionPercent = item.getSaleReductionPercent();
            if (!item.setSaleReductionPercent(reductionPercent)) {
                plr.sendMessage("§cDu musst eine Zahl zwischen 0 und 99 eingeben!");
                return;
            }
            if (item.getBuyCostSale() < item.getSellWorth()) {
                item.setSaleReductionPercent(previousReductionPercent);
                plr.sendMessage("§cDu kannst die Aktionsprozente nicht so niedrig setzen, dass dadurch der Kaufpreis kleiner dem Verkaufspreis wird!");
                return;
            }
            module.getItemConfig().asyncSave(module.getPlugin());
            plr.sendMessage("§aDer Kaufpreis des Item §6" + item.getDisplayName() + "§a wird nun um §6" + module.getTextOutput().formatPercentage(reductionPercent) + "§a reduziert.");
            plr.sendMessage("§aEs kostet während des Angebotes dadurch §6" + ShopStringAdaptor.getCurrencyString(item.getBuyCostSale()) +
                                "§a statt §6" + ShopStringAdaptor.getCurrencyString(item.getBuyCostSale()) + "§a.");
        } catch (NumberFormatException ignored) {
            plr.sendMessage("§cDer Preis muss eine Zahl sein!");
        }
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<prozent> <shopitem>", "Setzt die prozentuale Reduktion eines Items.");
    }
}
