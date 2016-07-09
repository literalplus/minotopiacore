/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.text.admin;

import org.bukkit.entity.Player;

import io.github.xxyy.common.util.StringHelper;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ui.text.AbstractShopAction;

/**
 * Admin action that removes an existing shop item from the shop.
 *
 * @author xxyy, Janmm14
 */
class RemoveAdminAction extends AbstractShopAction {
    private final ShopModule module;

    RemoveAdminAction(ShopModule module) {
        super("shopadmin", "remove", 1, null);
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        String itemName = StringHelper.varArgsString(args, 0, false);
        ShopItem item = module.getItemManager().getItem(itemName);
        if (module.getTextOutput().checkNonExistant(plr, item, itemName)) {
            return;
        }
        module.getItemConfig().removeItem(item);
        plr.sendMessage("§aDas Item §6" + item.getDisplayName() + "§a wurde aus dem Shop entfernt.");
        if (item.getManager().getDiscountManager().isDiscounted(item)) {
            ShopItem discountedItem = item.getManager().getDiscountManager().selectDiscountedItem(item.getManager());
            if (discountedItem != null) {
                module.getTextOutput().announceDiscount(discountedItem);
            }
        }
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<Item>", "Entfernt ein Item aus dem Shop.");
    }
}
