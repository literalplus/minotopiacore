/*
▒█▀▄▀█ ░▀░ █▀▀▄ █▀▀█ ▀▀█▀▀ █▀▀█ █▀▀█ ░▀░ █▀▀█ ▒█▀▀█ █▀▀█ █▀▀█ █▀▀ 
▒█▒█▒█ ▀█▀ █░░█ █░░█ ░▒█░░ █░░█ █░░█ ▀█▀ █▄▄█ ▒█░░░ █░░█ █▄▄▀ █▀▀ 
▒█░░▒█ ▀▀▀ ▀░░▀ ▀▀▀▀ ░▒█░░ ▀▀▀▀ █▀▀▀ ▀▀▀ ▀░░▀ ▒█▄▄█ ▀▀▀▀ ▀░▀▀ ▀▀▀ 
Copyright (C) 2013 xxyy98. 
Decompilation, redistribution or usage
without explicit written permission
by the author are not permitted and
may result in legal steps being
taken. 

This program uses the Bukkit API but
is not in any way affiliated with it
and/or it's authors.
*/

package io.github.xxyy.mtc.clan;

import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.mtc.Const;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.chat.MTCChatHelper;
import io.github.xxyy.mtc.helper.LaterMessageHelper;
import io.github.xxyy.mtc.helper.MTCHelper;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public final class ClanHelper { //REFACTOR

    public static Map<Integer, ClanInfo> cacheById = new HashMap<>(); //TODO uuids
    public static Map<String, ClanInfo> cacheByName = new HashMap<>();
    public static Map<String, ClanMemberInfo> memberCache = new HashMap<>();
    public static Map<String, ClanInfo> playerClanCache = new HashMap<>();
    public static Map<Integer, Set<String>> memberNamesCache = new HashMap<>();//key is clan id
    public static List<String> inClanChatNames = new ArrayList<>();
    //there is no cache by player because if a player logged off there would be ghost objects, etc.

    private ClanHelper() {

    }

    /////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Broadcasts a message to all clan members.
     * @param clanId ID of target clan
     * @param msg message; Will be localised!
     */
    public static void broadcast(int clanId, String msg, boolean sendMTCPrefix){
        if(clanId < 0) {
            return;
        }
        Set<Player> set = ClanHelper.getAllMembers(clanId);
        if(set == null||set.isEmpty()) {
            return;
        }
        for(Player plr : set){
            if(plr == null || !plr.isOnline()) {
                continue;
            }
            plr.sendMessage(MTCHelper.loc(msg, plr.getName(), sendMTCPrefix));
        }
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Broadcasts a message to all clan members.
     * @param clanId ID of target clan
     * @param msg message; Will be localised!
     * @param args Arguments. See: {@link String#format(String, Object...)}
     */
    public static void broadcast(int clanId, String msg, boolean sendMTCPrefix, Object... args){
        if(clanId < 0) {
            return;
        }
        Set<Player> set = ClanHelper.getAllMembers(clanId);
        if(set == null||set.isEmpty()) {
            return;
        }
        for(Player plr : set){
            if(plr == null || !plr.isOnline()) {
                continue;
            }
            plr.sendMessage(MTCHelper.locArgs(msg, plr.getName(), sendMTCPrefix, args));
        }
    }
    
    /**
     * Broadcasts a message to all members online
     * and notifies others on their next join.
     * type IDs: 1=remove; 2=leave; 3=invited (clan); 4=joined
     * @param clanId self-explaining
     * @param msg message to send or cache. Will be localised!
     * @param sendMTCPrefix Prepends {@link io.github.xxyy.mtc.MTC#chatPrefix}
     */ //TODO: TypeIDs should be an enum
    public static void broadcastOrSave(int clanId, String msg, int typeId, boolean sendMTCPrefix){
        if(clanId < 0) {
            return;
        }
        Set<Player> set = ClanHelper.getAllMembers(clanId);
        if(set == null || set.isEmpty()) {
            return;
        }
        for(Player plr : set){
            if(plr == null) {
                continue;
            }
            if(plr.isOnline()) {
                MTCHelper.sendLoc(msg, plr, sendMTCPrefix);
            }else{
                LaterMessageHelper.addMessage(plr.getName(), "C", typeId, msg, true, sendMTCPrefix);
            }
        }
    }
    
    public static void clearCache() {
        ClanHelper.cacheById.clear();
        ClanHelper.cacheByName.clear();
        ClanHelper.memberCache.clear();
        ClanHelper.memberNamesCache.clear();
        InvitationInfo.invStringCache.clear();
        ClanHelper.playerClanCache.clear();
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    
    public static void clearInvitationsByClan(int clanId){
        SafeSql sql = MTC.instance().getSql();
        sql.safelyExecuteUpdate("DELETE FROM "+sql.dbName+"."+Const.TABLE_CLAN_INVITATIONS+" WHERE clan_id=?", clanId);
        InvitationInfo.invStringCache.clear();
    }
    
    public static Set<String> getAllMemberNames(int id){
        if(ClanHelper.memberNamesCache.containsKey(id)) {
            return ClanHelper.memberNamesCache.get(id);
        }
        SafeSql sql = MTC.instance().getSql();
        ResultSet rs = sql.safelyExecuteQuery("SELECT user_name FROM "+sql.dbName+"."+Const.TABLE_CLAN_MEMBERS+" WHERE clan_id=?", id);
        Set<String> rtrn = new HashSet<>();
        try {
            if(rs == null || !rs.isBeforeFirst()) {
                return rtrn;
            }
            while(rs.next()){
                rtrn.add(rs.getString("user_name"));
            }
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "ClanHelper.getAllMemberNames()");
        }
        if(!rtrn.isEmpty()) {
            ClanHelper.memberNamesCache.put(id, rtrn);
        }
        return rtrn;
    }
    
    /**
     * Gets all players. 
     * <b>Note that players CAN be offline AND/OR <code>null</code>!!</b>
     * @param id clan id
     * @return
     */ //REFACTOR
    public static Set<Player> getAllMembers(int id){
        SafeSql sql = MTC.instance().ssql;
        ResultSet rs = sql.safelyExecuteQuery("SELECT user_name FROM "+sql.dbName+"."+Const.TABLE_CLAN_MEMBERS+" WHERE clan_id=?", id);
        Set<Player> rtrn = new HashSet<>();
        try {
            if(rs == null || !rs.isBeforeFirst()) {
                return rtrn;
            }
            while(rs.next()){
                rtrn.add(Bukkit.getPlayerExact(rs.getString("user_name")));
            }
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "ClanHelper.getAllMembers()");
        }
        return rtrn;
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    
    public static ClanInfo getClanInfoById(int id){
        if(ClanHelper.cacheById.containsKey(id)) {
            return ClanHelper.cacheById.get(id);
        }
        ClanInfo rtrn = ClanInfo.getById(id);
        ClanHelper.cacheById.put(id, rtrn);
        ClanHelper.cacheByName.put(rtrn.name, rtrn);
        return rtrn;
    }
    
    public static ClanInfo getClanInfoByName(String name){
        if(ClanHelper.cacheByName.containsKey(name)) {
            return ClanHelper.cacheByName.get(name);
        }
        ClanInfo rtrn = ClanInfo.getByName(name);
        ClanHelper.cacheByName.put(name, rtrn);
        ClanHelper.cacheById.put(rtrn.id, rtrn);
        return rtrn;
    }
    
    public static ClanInfo getClanInfoByPlayerName(String plrName){
        if(ClanHelper.playerClanCache.containsKey(plrName)) {
            return ClanHelper.playerClanCache.get(plrName);
        }
        return ClanInfo.getByPlayerName(plrName);
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    
    public static ClanInfo getClanInfoByPrefix(String prefix){
        return ClanInfo.getByPrefix(prefix);
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    
    public static String getFormattedPrefix(ClanInfo ci){
        return "§a"+ci.prefix;//TODO clan levels
    }
    
    /////////////////////////////////////////////////////////////////////////////////

    public static double getKD(int kills, int deaths){
        if(deaths == 0) {
            return kills;
        }
        return ((double)kills)/((double)deaths);
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    
    public static ClanMemberInfo getMemberInfoByPlayerName(String plrName){
        if(ClanHelper.memberCache.containsKey(plrName)) {
            return ClanHelper.memberCache.get(plrName);
        }
        ClanMemberInfo rtrn = ClanMemberInfo.getByPlayerName(plrName);
        ClanHelper.memberCache.put(plrName, rtrn);
        return rtrn;
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    
    public static int getMemberNum(int clanId){
        SafeSql sql = MTC.instance().getSql();
        ResultSet rs = sql.safelyExecuteQuery("SELECT COUNT(*) AS cnt FROM "+sql.dbName+"."+Const.TABLE_CLAN_MEMBERS+" WHERE clan_id=?", clanId);
        try {
            if(rs == null || !rs.isBeforeFirst()) {
                return -1;
            }
            rs.next();
            return rs.getInt("cnt");
        } catch (SQLException e) {
            e.printStackTrace();
            return -2;
        }
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    
    public static String getMembersString(int id){
        SafeSql sql = MTC.instance().getSql();
        ResultSet rs = sql.safelyExecuteQuery("SELECT user_name FROM "+sql.dbName+"."+Const.TABLE_CLAN_MEMBERS+" WHERE clan_id=?", id);
        StringBuilder sb = new StringBuilder();
        try {
            if(rs == null || !rs.next()) {
                return MTCHelper.loc("XC-membersempty", true);
            }
            sb.append(ClanHelper.getPlayerString(rs.getString("user_name")));
            while(rs.next()){
                sb.append("§7,").append(ClanHelper.getPlayerString(rs.getString("user_name")));
            }
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "ClanHelper.getMembersString()");
        }
        return sb.toString();
    }
    
    /////////////////////////////////////////////////////////////////////////////////

    public static String getNameFormatByRank(String plrName, ClanMemberInfo.ClanRank rank){
        switch(rank){
        case MEMBER:
            return "§a"+plrName+"§7: ";
        case MODERATOR:
            return "§9"+plrName+"§7: ";
        case ADMIN:
            return "§c"+plrName+"§7: ";
        case LEADER:
            return "§4"+plrName+"§8: ";
        default:
            return "§a"+plrName+"§7: ";//WTF, java...this is not even possible and I *have* to inculde this? :(
        }
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    
    public static String getPlayerString(String plrName){
        Player plr = Bukkit.getPlayerExact(plrName);
        if(plr == null) {
            return MTCHelper.locArgs("XC-membersoff", "CONSOLE", false, plrName);
        }
        return MTCHelper.locArgs("XC-memberson", "CONSOLE", false, plrName);
    }
    
    /////////////////////////////////////////////////////////////////////////////////

    public static String getPrefix(String plrName){
        ClanInfo ci = ClanHelper.getClanInfoByPlayerName(plrName);
        if(ci.id > 0) {
            return ClanHelper.getFormattedPrefix(ci);
        }
        return "";
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    
    public static String getStarsByRank(ClanMemberInfo.ClanRank rank){
        switch(rank){
        case MEMBER:
            return "§8*";
        case MODERATOR:
            return "§9*";
        case ADMIN:
            return "§c*";
        case LEADER:
            return "§4*";
        default:
            throw new AssertionError();
        }
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    
    public static boolean isInAnyClan(String plrName){
        return ClanHelper.getMemberInfoByPlayerName(plrName).clanId > 0;
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    
    public static boolean isInChat(String plrName){
        return ClanHelper.inClanChatNames.contains(plrName);
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    
    public static boolean isLeader(String plrName){
        if(!ClanHelper.isInAnyClan(plrName)) {
            return false;
        }
        ClanMemberInfo cmi = ClanHelper.getMemberInfoByPlayerName(plrName);
        return cmi.clanId >= 0 && cmi.getRank().equals(ClanMemberInfo.ClanRank.LEADER);
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    
    public static String parseChatMessage(String msg, ClanMemberInfo cmi){
        msg = msg.replaceFirst("#", "");
        if(ClanPermission.has(cmi, ClanPermission.CHATCOLSPECIAL)) {
            msg = ChatColor.translateAlternateColorCodes('&', msg);
        } else if(ClanPermission.has(cmi, ClanPermission.CHATCOL)) {
            msg = MTCChatHelper.convertStandardColors(msg);
        }
        return msg;
    }
    
    /////////////////////////////////////////////////////////////////////////////////   
    
    public static boolean printClanInfoTo(CommandSender sender, ClanInfo ci){
        if(ci.id < 0) {
            return MTCHelper.sendLocArgs("XC-cifetcherr", sender, true, ci.id);
        }
        MTCHelper.sendLocArgs("XC-infoformat", sender, false, ci.name, ClanHelper.getFormattedPrefix(ci),
                ci.leaderName, ci.money, ci.kills, ci.deaths, ClanHelper.getKD(ci.kills, ci.deaths),
                ClanHelper.getMemberNum(ci.id), ci.level);
        return true;
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    
    public static boolean printClanMembersTo(CommandSender sender, ClanInfo ci){
        return MTCHelper.sendLocArgs("XC-memberformat", sender, false, 
                StringUtils.center(MTCHelper.locArgs("XC-memberheadernameformat", sender.getName(),
                false, ci.name), 58, '='),ci.leaderName,ClanHelper.getMembersString(ci.id));
    }
    
    public static boolean printOwnClanMembersTo(CommandSender sender, ClanInfo ci){//does not fetch ci because so that implementations can check themselves if a player is in any clan, etc.
        String admins = "";
        String mods = "";
        String members = "";
        SafeSql sql = MTC.instance().ssql;
        ResultSet rs = sql.safelyExecuteQuery("SELECT user_name,user_rank FROM "+sql.dbName+"."+Const.TABLE_CLAN_MEMBERS+" WHERE clan_id=?", ci.id);
        try {
            if(rs != null && rs.next()){
                while(rs.next()){
                    switch(rs.getByte("user_rank")){
                    case 0:
                        members = ClanHelper.playerList(rs.getString("user_name"), members);
                        continue;
                    case 1:
                        mods = ClanHelper.playerList(rs.getString("user_name"), mods);
                        continue;
                    case 2:
                        admins = ClanHelper.playerList(rs.getString("user_name"), admins);
                    }
                }
            }else{
                sender.sendMessage("§cGenerischer Datenbankfehler. Das könnte bedeuten, dass die Datenbank nicht erreichbar ist.");
                return true;
            }
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "ClanHelper.getMembersString()");
            sender.sendMessage("§cGenerischer Datenbankfehler. Das könnte bedeuten, dass die Datenbank nicht erreichbar ist. (2)");
            return true;
        }
        
        return MTCHelper.sendLocArgs("XC-ownmemberformat", sender, false, 
                StringUtils.center(MTCHelper.locArgs("XC-memberheadernameformat", sender.getName(),
                false, ci.name), 58, '='), 
                MTCHelper.loc("XC-membersleader", sender.getName(), false) + ClanHelper.getPlayerString(ci.leaderName) + "\n" +
                ((admins.isEmpty()) ? "" : MTCHelper.loc("XC-membersadmins", sender.getName(), false) + admins + "\n") +
                ((mods.isEmpty()) ? "" : MTCHelper.loc("XC-membersmods", sender.getName(), false) + mods + "\n") +
                ((members.isEmpty()) ? "" : MTCHelper.loc("XC-membersmembers", sender.getName(), false) + members));
    }
    
    /**
     * Adds a player to a list of players.
     * Colors corresponding to online state.
     * <code>plrName</code> is prepended with "§7," if it's not the
     * first one in the list.
     * @param plrName Name to add to list
     * @param list current list
     * @return new list
     */
    private static String playerList(String plrName, String list){
        if(list.isEmpty()) {
            list += ClanHelper.getPlayerString(plrName);
        } else {
            list += "§7, " + ClanHelper.getPlayerString(plrName);
        }
        return list;
    }
    
    
}
