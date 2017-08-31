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

package li.l1t.mtc.misc.cmd;

import li.l1t.common.misc.HelpManager;
import li.l1t.mtc.helper.MTCHelper;
import li.l1t.mtc.misc.PlayerHeadManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public final class CommandPlayerHead implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!MTCHelper.isEnabledAndMsg(".command.playerhead.command", sender)) {
            return true;
        }
        //permissions handeled in Manager
        if (args.length > 0) {
            switch (args[0]) {
                case "get":
                    PlayerHeadManager phm = new PlayerHeadManager(args, label);
                    phm.getHead(sender);
                    break;
                case "set":
                    PlayerHeadManager phm1 = new PlayerHeadManager(args, label);
                    phm1.setHead(sender);
                    break;
                case "getall":
                    PlayerHeadManager phm2 = new PlayerHeadManager(args, label);
                    phm2.getAllHead(sender);
                    break;
                default:
                    sender.sendMessage("§8Unbekannte Aktion. Versuche §3get§8 oder §3set§8.");
                    HelpManager.tryPrintHelp("ph", sender, label, "", "mts help ph");
            }
        } else {
            HelpManager.tryPrintHelp("ph", sender, label, "", "mts help ph");
        }
        return true;
    }

}
