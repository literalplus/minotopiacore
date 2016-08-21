/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
