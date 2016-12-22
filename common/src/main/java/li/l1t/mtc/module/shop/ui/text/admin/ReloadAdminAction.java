/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.shop.ui.text.admin;

import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.module.shop.ShopModule;
import li.l1t.mtc.module.shop.ui.text.AbstractShopAction;
import org.bukkit.entity.Player;

/**
 * Executes an admin action that reloads some things related to the shop.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
class ReloadAdminAction extends AbstractShopAction {
    private final ShopModule module;

    ReloadAdminAction(ShopModule module) {
        super("shopadmin", "reload", 0, null);
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        module.getItemConfig().tryLoad();
        module.reloadConfig();
        MessageType.RESULT_LINE_SUCCESS.sendTo(plr, "Konfigurierts relodiert. Herzlichen Glückwunsch!");
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "", "Relodiert die Konfigurierts.");
    }
}
