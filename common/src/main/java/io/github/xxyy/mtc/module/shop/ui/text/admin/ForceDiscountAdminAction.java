/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.text.admin;

import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ui.text.AbstractShopAction;
import org.bukkit.entity.Player;

/**
 * Admin action that forces the discount manager to discount a new item.
 *
 * @author Janmm14, xxyy
 */
class ForceDiscountAdminAction extends AbstractShopAction {
    private final ShopModule module;

    ForceDiscountAdminAction(ShopModule module) {
        super("shopadmin", "forcediscount", 0, null, "newdiscount");
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        ShopItem discountedItem = module.getItemManager().getDiscountManager().selectDiscountedItem(module.getItemManager());

        if (discountedItem == null) {
            plr.sendMessage("§cEs gibt keine reduzierbaren Items.");
            return;
        }
        module.getTextOutput().announceDiscount(discountedItem);
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "", "Generiert ein neues Sonderangebot.");
    }
}
