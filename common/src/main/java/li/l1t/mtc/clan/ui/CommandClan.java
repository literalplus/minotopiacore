/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.clan.ui;

import li.l1t.common.util.CommandHelper;
import li.l1t.mtc.ConfigHelper;
import li.l1t.mtc.MTC;
import li.l1t.mtc.clan.ClanHelper;
import li.l1t.mtc.clan.ClanInfo;
import li.l1t.mtc.clan.ClanMemberInfo;
import li.l1t.mtc.clan.ClanPermission;
import li.l1t.mtc.clan.InvitationInfo;
import li.l1t.mtc.clan.RunnableTpClanBase;
import li.l1t.mtc.helper.LaterMessageHelper;
import li.l1t.mtc.helper.MTCHelper;
import li.l1t.mtc.misc.cmd.MTCCommandExecutor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandClan extends MTCCommandExecutor { //REFACTOR

    private final MTC plugin;

    public CommandClan(MTC plugin) {
        this.plugin = plugin;
    }

    private static boolean printHelpTo(CommandSender sender, String label, String page) {
        //TODO like a factions
        ClanHelpManager.tryPrintHelp("xclan", sender, label, page, "clan help");
        return true;
    }

    @Override
    public boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            return CommandClan.printHelpTo(sender, label, (args.length >= 2) ? args[1] : "1");
        }/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if (args[0].equalsIgnoreCase("create")) {
            if (CommandHelper.kickConsoleFromMethod(sender, label + " create")) {
                return true;
            }
            if (!CommandHelper.checkPermAndMsg(sender, "mtc.clan.create", label)) {
                return true;
            }
            if (args.length < 3) {
                return MTCHelper.sendLoc("XC-createusage", sender, true);
            }
            if (args[1].length() > 20) {
                return MTCHelper.sendLoc("XC-namelength", sender, true);
            }
            if (args[2].length() > 5 || args[2].length() < 1) {
                return MTCHelper.sendLoc("XC-prefixlength", sender, true);
            }
            if (ClanHelper.isInAnyClan(senderName)) {
                return MTCHelper.sendLoc("XC-inclan", sender, true);
            }
            if (ClanHelper.getClanInfoByName(args[1]).id > 0) {
                return MTCHelper.sendLoc("XC-nameexists", sender, true);
            }
            if (ClanHelper.getClanInfoByPrefix(args[2]).id > 0) {
                return MTCHelper.sendLoc("XC-prefixexists", sender, true);
            }
            if (args[1].matches("(.*)[^a-zA-Z0-9äöüÄÖÜß²³_\\-]+(.*)")) {
                return MTCHelper.sendLocArgs("XC-invalidchars", sender, true, "Clannamen");
            }
            if (args[2].matches("(.*)[^a-zA-Z0-9äöüÄÖÜß²³_\\-]+(.*)")) {
                return MTCHelper.sendLocArgs("XC-invalidchars", sender, true, "Clanprefix");
            }
            //Now to the logic! Yay!
            ClanInfo ci = ClanInfo.create(args[1], args[2], (Player) sender);
            if (ci.id < 0) {
                return MTCHelper.sendLocArgs("XC-cierror", sender, true, ci.id);
            }
            ClanHelper.cacheById.put(ci.id, ci);
            ClanHelper.cacheByName.put(ci.name, ci);
            ClanMemberInfo cmi = ClanMemberInfo.create(senderName, ci.id, (short) 3, ClanPermission.LEADER_PERMISSIONS);
            if (cmi.clanId < 0) {
                return MTCHelper.sendLocArgs("XC-cmierror", sender, true, cmi.clanId);
            }
            ClanHelper.memberCache.put(senderName, cmi);
            return MTCHelper.sendLocArgs("XC-created", sender, true, MTC.codeChatCol);
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (CommandHelper.kickConsoleFromMethod(sender, label + " remove")) {
                return true;
            }
            if (!CommandHelper.checkPermAndMsg(sender, "mtc.clan.remove", label)) {
                return true;
            }
            if (!ClanHelper.isInAnyClan(senderName)) {
                return MTCHelper.sendLoc("XC-notinclan", sender, true);
            }
            if (!ClanPermission.hasAndMessage(sender, ClanPermission.REMOVE)) {
                return true;
            }
            if (args.length >= 2 && args[1].equalsIgnoreCase("sure")) {
                ClanInfo ci = ClanHelper.getClanInfoByPlayerName(senderName);
                if (ci.id < 0) {
                    return MTCHelper.sendLocArgs("XC-cifetcherr", sender, true, ci.id);
                }
                if (plugin.getVaultHook().isEconomyHooked()) {
                    plugin.getVaultHook().depositPlayer((Player) sender, ci.money);
                }
                ClanHelper.broadcastOrSave(ClanHelper.getClanInfoByPlayerName(senderName).id, MTCHelper.locArgs("XC-removedbroadcast", senderName, false, senderName), 1, true);
                ci.nullify();
                ClanHelper.cacheById.remove(ci.id);
                ClanHelper.cacheByName.remove(ci.name);
                ClanHelper.memberNamesCache.remove(ci.id);
                ClanHelper.memberCache.clear();
                ClanHelper.playerClanCache.clear();//safer
                ClanHelper.clearInvitationsByClan(ci.id);
                return MTCHelper.sendLoc("XC-removed", sender, true);
            }
            return MTCHelper.sendLoc("XC-removewarning", sender, false);
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if (args[0].equalsIgnoreCase("leave")) {
            if (!CommandHelper.checkPermAndMsg(sender, "mtc.clan.leave", label)) {
                return true;
            }
            if (!ClanHelper.isInAnyClan(senderName)) {
                return MTCHelper.sendLoc("XC-notinclan", sender, true);
            }
            if (ClanHelper.isLeader(senderName)) {
                return MTCHelper.sendLoc("XC-leaderleave", sender, true);
            }
            ClanMemberInfo cmi = ClanHelper.getMemberInfoByPlayerName(senderName);
            if (cmi.clanId < 0) {
                return MTCHelper.sendLocArgs("XC-cmifetcherr", sender, true, cmi.clanId);
            }
            cmi.nullify();
            ClanHelper.memberNamesCache.remove(cmi.clanId);//ghost mebers
//            ClanHelper.broadcast(cmi.clanId, "XC-leavebroadcast");
            ClanHelper.broadcastOrSave(cmi.clanId, MTCHelper.locArgs("XC-leavebroadcast", "CONSOLE", true, senderName), 2, true);
            if (ClanHelper.memberCache.containsKey(senderName)) {
                ClanHelper.memberCache.remove(senderName);
            }
            if (ClanHelper.playerClanCache.containsKey(senderName)) {
                ClanHelper.playerClanCache.remove(senderName);
            }
            if (ClanHelper.inClanChatNames.contains(senderName)) {
                ClanHelper.inClanChatNames.remove(senderName);
            }
            if (ClanHelper.playerClanCache.containsKey(senderName)) {
                ClanHelper.playerClanCache.remove(senderName);
            }
            return MTCHelper.sendLoc("XC-leave", sender, true);
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if (args[0].equalsIgnoreCase("kick")) {
            if (!CommandHelper.checkPermAndMsg(sender, "mtc.clan.kick", label)) {
                return true;
            }
            if (!ClanHelper.isInAnyClan(senderName)) {
                return MTCHelper.sendLoc("XC-notinclan", sender, true);
            }
            if (!ClanPermission.hasAndMessage(sender, ClanPermission.KICK)) {
                return true;
            }
            if (args.length < 2) {
                return MTCHelper.sendLoc("XC-kickusage", sender, true);
            }
            ClanMemberInfo cmi = ClanHelper.getMemberInfoByPlayerName(args[1]);
            if (cmi.clanId == -102) {
                return MTCHelper.sendLoc("XC-notinyourclan", sender, true);
            }
            if (ClanPermission.has(cmi, ClanPermission.IGNOREKICK)) {
                return MTCHelper.sendLoc("XC-ignorekick", sender, true);
            }
            if (cmi.clanId < 0) {
                return MTCHelper.sendLocArgs("XC-cmifetcherr", sender, true, cmi.clanId);
            }
            ClanInfo ci = ClanHelper.getClanInfoByPlayerName(senderName);
            if (ci.id < 0) {
                return MTCHelper.sendLoc("XC-cifetcherr", sender, true);
            }
            if (ci.id != cmi.clanId) {
                return MTCHelper.sendLoc("XC-notinyourclan", sender, true);
            }
            ClanHelper.broadcastOrSave(cmi.clanId, MTCHelper.locArgs("XC-kickbroadcast", "CONSOLE", false, senderName, MTC.codeChatCol, MTC.priChatCol, args[1]), 2, true);
            cmi.nullify();
            ClanHelper.memberNamesCache.remove(cmi.clanId);//ghost members
            if (ClanHelper.memberCache.containsKey(args[1])) {
                ClanHelper.memberCache.remove(args[1]);
            }
            if (ClanHelper.playerClanCache.containsKey(args[1])) {
                ClanHelper.playerClanCache.remove(args[1]);
            }
            if (ClanHelper.inClanChatNames.contains(args[1])) {
                ClanHelper.inClanChatNames.remove(args[1]);
            }
            if (ClanHelper.playerClanCache.containsKey(args[1])) {
                ClanHelper.playerClanCache.remove(args[1]);
            }
            return true;
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if (args[0].equalsIgnoreCase("invite")) {
            if (!CommandHelper.checkPermAndMsg(sender, "mtc.clan.invite", label)) {
                return true;
            }
            if (!ClanHelper.isInAnyClan(senderName)) {
                return MTCHelper.sendLoc("XC-notinclan", sender, true);
            }
            if (!ClanPermission.hasAndMessage(sender, ClanPermission.INVITE)) {
                return true;
            }
            if (args.length < 2) {
                return MTCHelper.sendLoc("XC-inviteusage", sender, true);
            }
            if (senderName.equalsIgnoreCase(args[1])) {
                return MTCHelper.sendLoc("XC-inviteself", sender, true);
            }
            ClanInfo ci = ClanHelper.getClanInfoByPlayerName(senderName);
            if (ci.id < 0) {
                return MTCHelper.sendLocArgs("XC-cifetcherr", sender, true, ci.id);
            }
            if (ClanHelper.getMemberInfoByPlayerName(args[1]).clanId == ci.id) {
                return MTCHelper.sendLoc("XC-inviteyourclan", sender, true);
            }
            int memCnt = ClanHelper.getMemberNum(ci.id);
            if (!sender.hasPermission("mtc.clan.moremembers") && memCnt >= ConfigHelper.getClanMaxUsers()) {
                return MTCHelper.sendLocArgs("XC-userlimit", sender, true, ConfigHelper.getClanMaxUsers(), MTC.codeChatCol, MTC.priChatCol);
            }
            if (!sender.hasPermission("mtc.ignore") && memCnt >= ConfigHelper.getClanMaxUsersExtended()) {
                return MTCHelper.sendLocArgs("XC-userlimit", sender, true, ConfigHelper.getClanMaxUsersExtended(), MTC.codeChatCol, MTC.priChatCol);
            }
            if (InvitationInfo.getInvitationCount(args[1]) > 4) {
                return MTCHelper.sendLoc("XC-invlimitexceeded", sender, true);
            }
            if (InvitationInfo.hasInvitationFrom(ci.id, args[1])) {
                return MTCHelper.sendLocArgs("XC-alreadyinvited", sender, true, args[1], MTC.codeChatCol, MTC.priChatCol);
            }
            Player plr = Bukkit.getPlayerExact(args[1]);
//            boolean online = true;
//            if(plr == null || !plr.isOnline()) {
//                online = false;
//            }
            InvitationInfo.create(args[1], ci.id);
            if (plr != null && plr.isOnline()) {
                MTCHelper.sendLoc("XC-invitenotonline", sender, true);
                plr.sendMessage(InvitationInfo.getInvitationString(args[1], false));
            }
            ClanHelper.broadcast(ci.id, MTCHelper.locArgs("XC-invitedbroadcast", "CONSOLE", false, senderName, args[1], MTC.codeChatCol, MTC.priChatCol), true);
            return true;
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if (args[0].equalsIgnoreCase("invitations")) {
            if (!CommandHelper.checkPermAndMsg(sender, "mtc.clan.invitations", label)) {
                return true;
            }
            if (!InvitationInfo.hasInvitation(senderName)) {
                return MTCHelper.sendLoc("XC-noinvs", sender, true);
            }
            String str = InvitationInfo.getInvitationString(sender.getName(), false);
            if (str != null) {
                CommandHelper.msg(str, sender);
            }
            ClanInfo ci = ClanHelper.getClanInfoByPlayerName(senderName);
            if (ci.id < 0) {
                return true;//a
            }
            if (ClanHelper.isInAnyClan(senderName)) {
                return MTCHelper.sendLocArgs("XC-noteinclan", sender, true, ci.name, MTC.codeChatCol, MTC.priChatCol);
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if (args[0].equalsIgnoreCase("ignore")) {
            if (args.length < 2) {
                return MTCHelper.sendLoc("XC-ignoreusage", sender, true);
            }
            if (!StringUtils.isNumeric(args[1])) {
                return MTCHelper.sendLocArgs("XC-nan", sender, true, args[1]);
            }
            ClanInfo ci = ClanHelper.getClanInfoById(Integer.parseInt(args[1]));//No exception handling needed because it's already checked :P
            if (ci.id < 0) {
                return MTCHelper.sendLocArgs("XC-cifetcherr", sender, true, ci.id);
            }
            InvitationInfo ii = InvitationInfo.getByNameAndClan(senderName, ci.id);
            if (ii.id == -203) {
                return MTCHelper.sendLocArgs("XC-notinvited", sender, true, ci.name, MTC.codeChatCol, MTC.priChatCol);
            }
            if (ii.id < 0) {
                return MTCHelper.sendLocArgs("XC-gensqlerr", sender, true, ii.id, InvitationInfo.class.getName());
            }
            ii.nullify();
            return MTCHelper.sendLoc("XC-ignored", sender, true);
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if (args[0].equalsIgnoreCase("join")) {
            if (ClanHelper.isInAnyClan(senderName)) {
                return MTCHelper.sendLoc("XC-inclan", sender, true);
            }
            if (args.length < 2) {
                return MTCHelper.sendLoc("XC-joinusage", sender, true);
            }
            if (!StringUtils.isNumeric(args[1])) {
                return MTCHelper.sendLocArgs("XC-nan", sender, true, args[1]);
            }
            ClanInfo ci = ClanHelper.getClanInfoById(Integer.parseInt(args[1]));//No exception handling needed because it's already checked :P
            if (ci.id == -3) {
                return MTCHelper.sendLoc("XC-invalidclan", sender, true);
            }
            if (ci.id < 0) {
                return MTCHelper.sendLocArgs("XC-cifetcherr", sender, true, ci.id);
            }
            InvitationInfo ii = InvitationInfo.getByNameAndClan(senderName, ci.id);
            if (ii.id == -203) {
                if (!sender.hasPermission("mtc.ignore")) {
                    return MTCHelper.sendLocArgs("XC-notinvited", sender, true, ci.name, MTC.codeChatCol, MTC.priChatCol);
                }
            } else if (ii.id < 0) {
                return MTCHelper.sendLocArgs("XC-gensqlerr", sender, true, ii.id, InvitationInfo.class.getName());
            }
            ClanMemberInfo cmi = ClanMemberInfo.create(senderName, ci.id, (short) 0, ClanPermission.MEMBER_PERMISSIONS);
            if (cmi.clanId < 0) {
                return MTCHelper.sendLocArgs("XC-cmifetcherr", sender, true, cmi.clanId);
            }
            ClanHelper.broadcastOrSave(ci.id, MTCHelper.locArgs("XC-joinbroadcast", "CONSOLE", false,
                    senderName, MTC.codeChatCol, MTC.priChatCol), 4, true);
            ClanHelper.cacheById.remove(ci.id);//safety
            ClanHelper.cacheByName.remove(ci.name);//safety
            ClanHelper.memberNamesCache.remove(ci.id);//safety
            ClanHelper.memberCache.remove(senderName);//safety
            ClanHelper.playerClanCache.remove(senderName);//safety
            ii.nullify();
            return MTCHelper.sendLocArgs("XC-joined", sender, true, ci.name, MTC.codeChatCol, MTC.priChatCol);
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if (args[0].equalsIgnoreCase("base")) {
            if (!ClanHelper.isInAnyClan(senderName)) {
                return MTCHelper.sendLoc("XC-notinclan", sender, true);
            }
            if (CommandHelper.kickConsoleFromMethod(sender, label + " base")) {
                return true;
            }
            if (!ClanPermission.hasAndMessage(sender, ClanPermission.TPBASE)) {
                return true;
            }
            Player plr = (Player) sender;
            ClanInfo ci = ClanHelper.getClanInfoByPlayerName(senderName);
            if (ci.id < 0) {
                return MTCHelper.sendLocArgs("XC-cifetcherr", sender, true, ci.id);
            }
            Bukkit.getScheduler().runTaskLater(plugin, new RunnableTpClanBase(plr, plr.getLocation(), plr.getHealth(), ci.id), 40);
            return MTCHelper.sendLoc("XC-preparetp", sender, true);
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if (args[0].equalsIgnoreCase("setbase")) {
            if (!ClanHelper.isInAnyClan(senderName)) {
                return MTCHelper.sendLoc("XC-notinclan", sender, true);
            }
            if (!ClanPermission.hasAndMessage(sender, ClanPermission.SETBASE)) {
                return true;
            }
//            if(!ClanHelper.isLeader(senderName)) return MTCHelper.sendLoc("XC-notleader", sender, true);
            if (CommandHelper.kickConsoleFromMethod(sender, label + " setbase")) {
                return true;
            }
            ClanInfo ci = ClanHelper.getClanInfoByPlayerName(senderName);
            if (ci.id < 0) {
                return MTCHelper.sendLoc("XC-cifetcherr", sender, true);
            }
            ci.base = ((Player) sender).getLocation();
            ci.flush();
            ClanHelper.broadcast(ci.id, "XC-setbasebroadcast", true, senderName, MTC.codeChatCol, MTC.priChatCol);
            return MTCHelper.sendLoc("XC-setbase", sender, true);
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if (args[0].equalsIgnoreCase("chat")) {
            if (!ClanHelper.isInAnyClan(senderName)) {
                return MTCHelper.sendLoc("XC-notinclan", sender, true);
            }
            if (!ClanPermission.hasAndMessage(sender, ClanPermission.USECHAT)) {
                return true;
            }
            if (args.length == 1) {
                if (ClanHelper.inClanChatNames.contains(senderName)) {
                    ClanHelper.inClanChatNames.remove(senderName);
                } else {
                    ClanHelper.inClanChatNames.add(senderName);
                }
                return MTCHelper.sendLoc("XC-toggledchat", sender, true);
            }
            ClanInfo ci = ClanHelper.getClanInfoByPlayerName(senderName);
            if (ci.id < 0) {
                return MTCHelper.sendLocArgs("XC-cifetcherr", sender, true, ci.id);
            }
            ClanMemberInfo cmi = ClanHelper.getMemberInfoByPlayerName(senderName);
            String msg = args[1];
            if (args.length > 2) {
                for (int i = 2; i < args.length; i++) {
                    msg += " " + args[i];
                }
            }
            if (cmi.clanId < 0) {
                return MTCHelper.sendLocArgs("XC-cmifetcherr", sender, true, cmi.clanId);
            }
            ClanHelper.sendChatMessage(ci, msg, cmi);
        } else if (args[0].equalsIgnoreCase("revoke")) {
            if (!ClanHelper.isInAnyClan(senderName)) {
                return MTCHelper.sendLoc("XC-notinclan", sender, true);
            }
            if (!ClanPermission.hasAndMessage(sender, ClanPermission.REVOKE)) {
                return true;
            }
            if (args.length < 2) {
                return MTCHelper.sendLoc("XC-revokeusage", sender, true);
            }
            ClanInfo ci = ClanHelper.getClanInfoByPlayerName(senderName);
            if (ci.id < 0) {
                return MTCHelper.sendLocArgs("XC-cifetcherr", sender, true, ci.id);
            }
            InvitationInfo ii = InvitationInfo.getByNameAndClan(args[1], ci.id);
            if (ii.id == -203) {
                return MTCHelper.sendLocArgs("XC-notinvited2", sender, true, args[1], MTC.codeChatCol, MTC.priChatCol, args[1]);
            }
            if (ii.id < 0) {
                return MTCHelper.sendLocArgs("XC-gensqlerr", sender, true, ii.id, InvitationInfo.class.getName());
            }
            ii.nullify();
            ClanHelper.broadcast(ci.id, "XC-revokedbroadcast", true, senderName, MTC.codeChatCol, MTC.priChatCol, args[1], '’');
            Player plr = Bukkit.getPlayerExact(args[1]);
            InvitationInfo.invStringCache.remove(args[0]);
            if (plr == null) {
                LaterMessageHelper.addMessage(args[1], "C", 5,
                        MTCHelper.locArgs("XC-revokeduser", args[1], true, ci.name, MTC.codeChatCol, MTC.priChatCol),
                        true, false);
            } else {
                MTCHelper.sendLocArgs("XC-revokeduser", plr, true, ci.name, MTC.codeChatCol, MTC.priChatCol);
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if (args[0].equalsIgnoreCase("setrank")) {
            if (!ClanHelper.isInAnyClan(senderName)) {
                return MTCHelper.sendLoc("XC-notinclan", sender, true);
            }
            ClanMemberInfo cmi = ClanHelper.getMemberInfoByPlayerName(senderName);
            if (cmi.clanId < 0) {
                return MTCHelper.sendLocArgs("XC-gensqlerr", sender, true, cmi.clanId);
            }
            if (!ClanPermission.hasAndMessage(cmi, ClanPermission.SETRANK, sender)) {
                return true;
            }
            if (args.length < 3 || args[1].equalsIgnoreCase("help")) {
                return MTCHelper.sendLoc("XC-setrankhelp", sender, false);
            }
            ClanMemberInfo cmiTarget = ClanHelper.getMemberInfoByPlayerName(args[1]);
            if (cmiTarget.clanId != cmi.clanId || cmiTarget.clanId == -102) {
                return MTCHelper.sendLoc("XC-notinyourclan", sender, true);
            }
            if (cmiTarget.clanId < 0) {
                return MTCHelper.sendLocArgs("XC-cmifetcherr", sender, true, cmiTarget.clanId);
            }
            int rankId = ClanMemberInfo.getRankIdFromString(args[2]);
            if (rankId < 0) {
                return MTCHelper.sendLoc("XC-invalidrank", sender, true);
            }
            if (rankId >= cmi.userRankId && cmi.getRank() != ClanMemberInfo.ClanRank.LEADER && !sender.hasPermission("mtc.clana.override")) {
                return MTCHelper.sendLoc("XC-sethigherrank", sender, true);
            }
            cmiTarget.userRankId = rankId;
            cmiTarget.userPermissions = ClanPermission.getDefaultPermissionsByRank(cmiTarget.getRank());
            cmiTarget.flush();
            if (rankId == 3) {
                cmi.userRankId = 2;
                cmi.flush();
                ClanInfo ci = ClanHelper.getClanInfoById(cmi.clanId);
                if (ci.id > 0) {
                    ci.leaderName = args[1];
                    ci.flush();
                }
                MTCHelper.sendLoc("XC-setleader", sender, true);
            }
            ClanHelper.broadcast(cmi.clanId, "XC-ranksetbroadcast", true, args[1], MTC.codeChatCol, MTC.priChatCol, ClanMemberInfo.getRankName(rankId));
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if (args[0].equalsIgnoreCase("info")) {
            if (args.length < 2) {
                if (!ClanHelper.isInAnyClan(senderName)) {
                    return MTCHelper.sendLoc("XC-notinclan", sender, true);
                }
                ClanInfo ci = ClanHelper.getClanInfoByPlayerName(senderName);
                return ClanHelper.printClanInfoTo(sender, ci);
            }
            if (args[1].equalsIgnoreCase("help")) {
                return MTCHelper.sendLoc("XC-infohelp", sender, false);
            }
            if (args.length >= 3 && args[1].equalsIgnoreCase("player")) {
                ClanMemberInfo cmi = ClanHelper.getMemberInfoByPlayerName(args[2]);
                if (cmi.clanId == -102) {
                    return MTCHelper.sendLoc("XC-plrnotinclan", sender, true);
                }
                if (cmi.clanId < 0) {
                    return MTCHelper.sendLocArgs("XC-cmifetcherr", sender, true, cmi.clanId);
                }
                ClanInfo ci = ClanHelper.getClanInfoById(cmi.clanId);
                if (ci.id < 0) {
                    return MTCHelper.sendLocArgs("XC-cifetcherr", sender, true, ci.id);
                }
                return ClanHelper.printClanInfoTo(sender, ci);
            }
            if (!args[1].isEmpty() && StringUtils.isNumeric(args[1])) {
                ClanInfo ci = ClanHelper.getClanInfoById(Integer.parseInt(args[1]));
                if (ci.id > 0) {
                    return ClanHelper.printClanInfoTo(sender, ci);
                }
            }
            ClanInfo ci2 = ClanHelper.getClanInfoByPrefix(args[1]);
            if (ci2.id > 0) {
                return ClanHelper.printClanInfoTo(sender, ci2);
            }
            ClanInfo ci3 = ClanHelper.getClanInfoByName(args[1]);
            if (ci3.id > 0) {
                return ClanHelper.printClanInfoTo(sender, ci3);
            }
            return MTCHelper.sendLoc("XC-404", sender, true);
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if (args[0].equalsIgnoreCase("members")) {
            if (args.length < 2) {
                if (!ClanHelper.isInAnyClan(senderName)) {
                    return MTCHelper.sendLoc("XC-notinclan", sender, true);
                }
                ClanInfo ci = ClanHelper.getClanInfoByPlayerName(senderName);
                if (ci.id < 0) {
                    return MTCHelper.sendLocArgs("XC-cifetcherr", sender, true, ci.id);
                }
                return ClanHelper.printOwnClanMembersTo(sender, ci);
            }
            if (args.length >= 3 && args[1].equalsIgnoreCase("player")) {
                if (!ClanHelper.isInAnyClan(args[2])) {
                    return MTCHelper.sendLoc("XC-plrnotinclan", sender, true);
                }
                ClanInfo ci = ClanHelper.getClanInfoByPlayerName(args[2]);
                if (ci.id < 0) {
                    return MTCHelper.sendLoc("XC-cifetcherr", sender, true);
                }
                return ClanHelper.printClanMembersTo(sender, ci);
            }
            if (!args[1].isEmpty() && StringUtils.isNumeric(args[1])) {
                ClanInfo ci = ClanHelper.getClanInfoById(Integer.parseInt(args[1]));
                if (ci.id > 0) {
                    return ClanHelper.printClanMembersTo(sender, ci);
                }
            }
            ClanInfo ci = ClanHelper.getClanInfoByPrefix(ChatColor.translateAlternateColorCodes('&', args[1]));
            if (ci.id > 0) {
                return ClanHelper.printClanMembersTo(sender, ci);
            }
            ClanInfo ci2 = ClanHelper.getClanInfoByName(args[1]);
            if (ci2.id > 0) {
                return ClanHelper.printClanMembersTo(sender, ci2);
            }
            return MTCHelper.sendLoc("XC-404", sender, true);
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if (args[0].equalsIgnoreCase("permission")) {
            return MTCHelper.sendLoc("XC-nyi", sender, true);
        }/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if (args[0].equalsIgnoreCase("search")) {
            return MTCHelper.sendLoc("XC-nyi", sender, true);
        }/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if (args[0].equalsIgnoreCase("setsearch")) {
            return MTCHelper.sendLoc("XC-nyi", sender, true);
        }/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if (args[0].equalsIgnoreCase("bank")) {
            return MTCHelper.sendLoc("XC-nyi", sender, true);
        }/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if (args[0].equalsIgnoreCase("level")) {
            return MTCHelper.sendLoc("XC-nyi", sender, true);
        }/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if (args[0].equalsIgnoreCase("top")) {
            return MTCHelper.sendLoc("XC-nyi", sender, true);
        }/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if (args[0].equalsIgnoreCase("tutorial")) {
            return MTCHelper.sendLoc("XC-nyi", sender, true);
        }/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if (args[0].equalsIgnoreCase("reset")) {
            return MTCHelper.sendLoc("XC-nyi", sender, true);
        }/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if (args[0].equalsIgnoreCase("setmotd")) {
            return MTCHelper.sendLoc("XC-nyi", sender, true);
        }/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if (args[0].equalsIgnoreCase("request")) {
            return MTCHelper.sendLoc("XC-nyi", sender, true);
        }/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if (args[0].equalsIgnoreCase("options")) {
            return MTCHelper.sendLoc("XC-nyi", sender, true);
        }/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if (args[0].equalsIgnoreCase("chest")) {
            return MTCHelper.sendLoc("XC-nyi", sender, true);
        }/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if (args[0].equalsIgnoreCase("alliance")) {
            return MTCHelper.sendLoc("XC-nyi", sender, true);
        }/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if (args[0].equalsIgnoreCase("enemy")) {
            return MTCHelper.sendLoc("XC-nyi", sender, true);
        }/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if (args[0].equalsIgnoreCase("war")) {
            return MTCHelper.sendLoc("XC-nyi", sender, true);
        } else {
            MTCHelper.sendLoc("XC-wrongusage", sender, true);
            CommandClan.printHelpTo(sender, label, "1");
        }
        return true;
    }

}
