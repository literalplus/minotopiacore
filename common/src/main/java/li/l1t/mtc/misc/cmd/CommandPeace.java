/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.misc.cmd;

import com.google.common.collect.Lists;
import li.l1t.common.util.CommandHelper;
import li.l1t.mtc.MTC;
import li.l1t.mtc.helper.MTCHelper;
import li.l1t.mtc.misc.PeaceInfo;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public final class CommandPeace extends MTCPlayerOnlyCommandExecutor implements TabCompleter {

    public static final int PEACE_LIST_PAGE_SIZE = 15;

    public static String getPlayerString(String plrName) {
        Player plr = Bukkit.getPlayerExact(plrName); //REFACTOR
        if (plr == null) {
            return MTCHelper.locArgs("XC-membersoff", "CONSOLE", false, plrName);//continuity
        }
        return MTCHelper.locArgs("XC-memberson", "CONSOLE", false, plrName);
    }

    private static boolean sendPeaceList(CommandSender sender, List<String> lst, int rowstart, int perPage, String label, int nextPage) {
        String toSend = "";
        int i = rowstart; //needed for later use :/
        for (; (i < (rowstart + perPage) && lst.size() > i); i++) {
            toSend += " ► " + CommandPeace.getPlayerString(lst.get(i)) + "\n";
        }
        if (toSend.equals("")) {
            toSend = MTCHelper.loc("XU-ppageempty", sender, false);
        }
        if (i < lst.size()) {
            return MTCHelper.sendLocArgs("XU-pmorepages", sender, false, label, nextPage);
        }
        return CommandHelper.msg(toSend, sender);
    }

    @Override
    public boolean catchCommand(Player plr, String plrName, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            return MTCHelper.sendLocArgs("XU-peacehelp", plr, false, label);
        }
        switch (args[0]) {
            case "list":
                PeaceInfo piList = PeaceInfo.get(plrName);
                if (piList.errCode < 0) {
                    return MTCHelper.sendLoc("XU-nopeace", plr, true);
                }
                int rowstart = 0;
                int page = 1;
                if (args.length >= 2 && StringUtils.isNumeric(args[1])) {
                    page = Integer.parseInt(args[1]);
                    if (page <= 0)// if 0, rowstart would be negative
                    {
                        page = 1;
                    }
                    rowstart = (page - 1) * PEACE_LIST_PAGE_SIZE;
                }
                return CommandPeace.sendPeaceList(plr, piList.peacedPlrs, rowstart, PEACE_LIST_PAGE_SIZE, label, page + 1);
            case "status":
                if (args.length < 2) {
                    return MTCHelper.sendLocArgs("XU-peacehelp", plr, false, label);
                }
                if (args[1].equalsIgnoreCase(plrName)) {
                    return CommandHelper.msg(MTC.chatPrefix + "Ich nehme an, dass du mit dir selbst Frieden hast.", plr);
                }
                if (PeaceInfo.isInPeaceWith(plrName, args[1])) {
                    return MTCHelper.sendLocArgs("XU-plrpeace", plr, true, args[1]);
                }
                if (PeaceInfo.hasRequest(plrName, args[1])) {
                    return MTCHelper.sendLocArgs("XU-preqpending", plr, true, args[1]);
                }
                if (PeaceInfo.hasRequest(args[1], plrName)) {
                    return MTCHelper.sendLocArgs("XU-preqpendingown", plr, true, args[1]);
                }
                return MTCHelper.sendLocArgs("XU-plrnopeace", plr, true, args[1]);
            case "help":
                return MTCHelper.sendLocArgs("XU-peacehelp", plr, false, label);
            case "revoke":
                if (args.length < 2) {
                    return MTCHelper.sendLocArgs("XU-peacehelp", plr, false, label);
                }
                if (args[1].equalsIgnoreCase(plrName)) {
                    return CommandHelper.msg(MTC.chatPrefix + "Du hast dir selbst keine Friedensanfrage gesendet.", plr);
                }
                if (!PeaceInfo.hasRequest(plrName, args[1])) {
                    return MTCHelper.sendLocArgs("XU-nopreqsent", plr, true, args[1]);
                }
                PeaceInfo.revokeRequest(plrName, args[1]);
                MTCHelper.sendLocOrSaveArgs("XU-preqrevokedbyother", args[1], "peace", 3, true, plrName);
                return MTCHelper.sendLocArgs("XU-preqrevoked", plr, true, args[1]);
            default:
                if (args[0].equalsIgnoreCase(plrName)) {
                    return CommandHelper.msg(MTC.chatPrefix + "Du kannst nicht mit dir selbst Frieden schliessen :)", plr);
                }
                PeaceInfo piDefault = PeaceInfo.get(plrName);
                if (piDefault.errCode < 0 && piDefault.errCode != -4) {
                    return CommandHelper.msg("§cGenerischer SQL-Fehler: " + piDefault.errCode, plr);
                }
                if (piDefault.peacedPlrs.contains(args[0])) {//has peace w/
                    piDefault.peacedPlrs.remove(args[0]);
                    PeaceInfo piOther = PeaceInfo.get(args[0]);
                    if (piOther.errCode < 0 && piOther.errCode != -4) {
                        return CommandHelper.msg("§cGenerischer SQL-Fehler (3): " + piDefault.errCode, plr);
                    }
                    piOther.peacedPlrs.remove(plrName);
                    piDefault.flush();
                    piOther.flush();
                    MTCHelper.sendLocOrSaveArgs("XU-prevokedother", args[0], "peace", 4, true, plrName);
                    return MTCHelper.sendLocArgs("XU-prevoked", plr, true, args[0]);
                }
                if (PeaceInfo.hasRequest(plrName, args[0])) {
                    return MTCHelper.sendLocArgs("XU-palreadysent", plr, true, args[0]); //already sent request
                }
                if (PeaceInfo.hasRequest(args[0], plrName)) {
                    PeaceInfo.revokeRequest(args[0], plrName);
                    piDefault.peacedPlrs.add(args[0]);
                    PeaceInfo piOther = PeaceInfo.get(args[0]);
                    if (piOther.errCode < 0 && piOther.errCode != -4) {
                        return CommandHelper.msg("§cGenerischer SQL-Fehler (2): " + piDefault.errCode, plr);
                    }
                    piOther.peacedPlrs.add(plrName);
                    piDefault.flush();
                    piOther.flush();
                    MTCHelper.sendLocOrSaveArgs("XU-preqaccepted", args[0], "peace", 1, true, plrName);
                    return MTCHelper.sendLocArgs("XU-paccepted", plr, true, args[0]);
                }
                PeaceInfo.sendRequest(plrName, args[0]);
                MTCHelper.sendLocOrSaveArgs("XU-preqreceived", args[0], "peace", 2, true, plrName);
                return MTCHelper.sendLoc("XU-preqsent", plr, true);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return Lists.newArrayList("Du kannst diesen Befehl nur als Spieler ausfuehren!!!11");
        }
        if (args.length == 0) {
            List<String> lst = CommandHelper.getOnlinePlayerNames();
            lst.add("list");
            lst.add("status");
            lst.add("help");
            lst.add("revoke");
            return lst;
        } else if (args[0].equalsIgnoreCase("revoke")) {
            PeaceInfo pi = PeaceInfo.get(sender.getName());
            if (pi.errCode < 0) {
                return null;
            }
            return pi.peacedPlrs;
        }
        return null;
    }
}
