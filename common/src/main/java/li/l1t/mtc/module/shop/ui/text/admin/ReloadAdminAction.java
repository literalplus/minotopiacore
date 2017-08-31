/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
