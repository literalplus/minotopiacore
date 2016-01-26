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
import io.github.xxyy.mtc.module.shop.ui.util.ShopActionHelper;
import io.github.xxyy.mtc.module.shop.ui.util.SimpleReference;
import org.bukkit.entity.Player;

public class SetReducedItemShopAdminAction extends AbstractShopAction {
    private static final Joiner SPACE_JOINER = Joiner.on(' ');
    private final ShopModule module;

    protected SetReducedItemShopAdminAction(ShopModule module) {
        super("shopadmin", "setreduceditem", 2, null);
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        SimpleReference<String> itemName = new SimpleReference<>(SPACE_JOINER.join(args));
        boolean broadcast = ShopActionHelper.isNoBroadcast(itemName);
        ShopItem item = module.getItemManager().getItem(itemName.get());
        if (item == null) {
            plr.sendMessage("§cItem §6" + itemName.get() + "§c nicht gefunden.");
            return;
        }
        module.getItemManager().setItemOnSale(item);
        module.drawNewSaleItem(broadcast);
        if (!broadcast) {
            plr.sendMessage("§7Die folgende Nachricht erhälst nur du:");
            module.getTextOutput().sendItemSale(item, plr);
        }
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "[-nobroadcast] <shopitem>", "Setzt das gegebene Item zum neuen reduzierten Item.");
    }
}
