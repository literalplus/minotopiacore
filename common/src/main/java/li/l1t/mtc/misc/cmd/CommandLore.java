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
import li.l1t.mtc.misc.LoreManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public final class CommandLore implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!MTCHelper.isEnabledAndMsg(".command.lore", sender)) {
            return true;
        }
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Das Kommando /" + label + " kann nur von einem Spieler benutzt werden!");
            }
            this.printHelpToSender(sender, label);
        } else if (args.length >= 1) {
            LoreManager lm = new LoreManager(sender, label, args, this);
            switch (args[0]) {
                case "add":
                    lm.addLore();
                    break;
                case "clear":
                    lm.clearLore();
                    break;
                case "remove":
                    lm.removeLore();
                    break;
                case "set":
                    lm.setLoreAt();
                    break;
                case "list":
                    lm.listlore();
                    break;
                default:
                    sender.sendMessage("§8Invalide Aktion '" + args[0] + "'! Valide Aktionen: add,clear,remove,set,list.");
                    this.printHelpToSender(sender, label);
            }
        } else {
            sender.sendMessage("§8Falsche Verwendung von " + label + "!");
            this.printHelpToSender(sender, label);
        }
        return true;
    }

    public void printHelpToSender(CommandSender sender, String label) {
        HelpManager.tryPrintHelp("lore", sender, label, "", "mtc help lore");
    }


}
