/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.text.admin;

import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ui.text.AbstractShopAction;
import org.bukkit.entity.Player;

public class DrawReducedItemShopAdminAction extends AbstractShopAction {
    private final ShopModule module;

    protected DrawReducedItemShopAdminAction(ShopModule module) {
        super("shopadmin", "drawreduceditem", 0, null);
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        boolean broadcast = true;
        if (args.length > 0) {
            String arg0 = args[0].toLowerCase().trim();
            if (arg0.equalsIgnoreCase("nobroadcast") || arg0.equalsIgnoreCase("-nobroadcast")) {
                broadcast = false;
            }
        }
        module.drawNewSaleItem(broadcast);
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "[nobroadcast]", "Startet den Zufallsgenerator, um ein neues reduziertes Item festzulegen.");
    }
}
