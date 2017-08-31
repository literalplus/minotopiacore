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

package li.l1t.mtc.clan.ui;

import li.l1t.common.xyplugin.GenericXyPlugin;
import li.l1t.mtc.MTC;
import li.l1t.mtc.helper.MTCHelper;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;


public final class ClanHelpManager {

    public static Map<String, ClanHelpManager> helpMans = new HashMap<>();

    public String helpPageTitle;
    public String[] cmdDescLines;
    public Map<String, String> subCmds;

    public ClanHelpManager(String cmdName, String[] cmdDescLines, Map<String, String> subCmds) {
        this.helpPageTitle = cmdName;
        this.subCmds = subCmds;
        this.cmdDescLines = cmdDescLines;
    }

    public void printHelpToSender(CommandSender sender, String label, String pageNum, String helpCmdLabel) {
        //6 Commands/page
        int intPageNum;
        int totalPages = 1;
        int i = 0;
        if (this.subCmds.size() > 6) {
            if ((this.subCmds.size() % 6) == 0) {
                totalPages = this.subCmds.size() / 6;
            } else {
                totalPages = (this.subCmds.size() / 6) + 1;
            }
        }
        if (pageNum.isEmpty()) {
            intPageNum = 1;
        } else {
            try {
                intPageNum = Integer.parseInt(pageNum);
            } catch (Exception e) {
                sender.sendMessage(MTCHelper.loc("XC-helpnan", sender.getName(), false));
                return;
            }
        }
        //render begin
        sender.sendMessage(String.format(MTCHelper.loc("XC-helpheader", sender.getName(), false), this.helpPageTitle, intPageNum, totalPages));
        //command description
        for (String ln : this.cmdDescLines) {
            sender.sendMessage("§8" + ln);
        }
        //subcommands
        for (String key : this.subCmds.keySet()) {
            i = i + 1;
            if (i >= 6 * intPageNum) {
                break;
            }
            if (i < 6 * (intPageNum - 1)) {
                continue;
            }
            MTCHelper.sendLocArgs("XC-helpcmd", sender, false, label, key, this.subCmds.get(key));
        }
        if (totalPages != 1 && intPageNum != totalPages) {
            sender.sendMessage(String.format(MTCHelper.loc("XC-helpnextpage", sender.getName(), false), helpCmdLabel, (intPageNum + 1)));
        }
        sender.sendMessage(String.format(MTCHelper.loc("XC-helpheader", sender.getName(), false), this.helpPageTitle, intPageNum, totalPages));
    }

    public static void clearHelpManagers() {
        ClanHelpManager.helpMans = null;
    }

    public static ClanHelpManager getHelpManager(String helpManId) {
        return ClanHelpManager.helpMans.get(helpManId);
    }

    //INITIALIZATION AND STATIC VALUES

    public static boolean tryPrintHelp(String commandKey, CommandSender sender, String label, String pageNum, String helpCmdLabel) {
        try {
            ClanHelpManager helpMan = ClanHelpManager.getHelpManager(commandKey);
            if (helpMan == null) {
                sender.sendMessage(GenericXyPlugin.pluginPrefix + "Konnte Hilfe für " + MTC.codeChatCol + commandKey + MTC.priChatCol + " nicht laden!");
                return false;
            }
            helpMan.printHelpToSender(sender, label, pageNum, helpCmdLabel);
        } catch (Exception e) {
            sender.sendMessage(GenericXyPlugin.pluginPrefix + "Konnte Hilfe für " + MTC.codeChatCol + commandKey + MTC.priChatCol + " nicht laden :(");
            return false;
        }
        return true;
    }
}
