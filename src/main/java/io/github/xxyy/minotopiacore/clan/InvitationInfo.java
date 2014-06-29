package io.github.xxyy.minotopiacore.clan;

import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.minotopiacore.Const;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.helper.MTCHelper;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class InvitationInfo {
    public static Map<String, String> invStringCache = new HashMap<>();
    
    public int id = -1;
    public String userName = "Marcel.Davis";
    public int clanId = -201;
    
    private InvitationInfo(int errCode){
        this.id=errCode;
    }
    private InvitationInfo(int id, String userName, int clanId){
        this.id = id; this.userName = userName; this.clanId = clanId;
    }
    
    ////////////////////////////////////////////////////////////////////////
    
    public void nullify(){
        SafeSql sql = MTC.instance().ssql;
        sql.safelyExecuteUpdate("DELETE FROM "+sql.dbName+"."+Const.TABLE_CLAN_INVITATIONS+" WHERE id=? LIMIT 1",this.id);
        InvitationInfo.invStringCache.remove(this.userName);
    }
    
    ////////////////////////////////////////////////////////////////////////
    
    public static InvitationInfo create(String userName, int clanId){
        SafeSql sql = MTC.instance().ssql;
        int rows = sql.safelyExecuteUpdate("INSERT INTO "+sql.dbName+"."+Const.TABLE_CLAN_INVITATIONS+" SET " +
        		"user_name=?,clan_id="+clanId, userName);
        if(rows < 1) return new InvitationInfo(-202);
        ResultSet rs = sql.executeQuery("SELECT LAST_INSERT_ID()");
        try {
            if(rs == null || !rs.isBeforeFirst()) return new InvitationInfo(-203);
            rs.next();
            InvitationInfo.invStringCache.remove(userName);
            return new InvitationInfo(rs.getInt(1), userName, clanId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new InvitationInfo(-204);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////
    
    public static Set<InvitationInfo> getByName(String userName){
        SafeSql sql = MTC.instance().ssql;
        ResultSet rs = sql.safelyExecuteQuery("SELECT * FROM "+sql.dbName+"."+Const.TABLE_CLAN_INVITATIONS+" WHERE user_name=?", userName);
        Set<InvitationInfo> set = new HashSet<>();
        try {
            if(rs == null || !rs.isBeforeFirst()) return set;
            while(rs.next()) {
                set.add(new InvitationInfo(rs.getInt("id"), rs.getString("user_name"), rs.getInt("clan_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return set;
    }
    public static InvitationInfo getByNameAndClan(String userName, int clanId){
        SafeSql sql = MTC.instance().ssql;
        ResultSet rs = sql.safelyExecuteQuery("SELECT * FROM "+sql.dbName+"."+Const.TABLE_CLAN_INVITATIONS+" WHERE user_name=? AND clan_id="+clanId, userName);
        try {
            if(rs == null || !rs.isBeforeFirst()) return new InvitationInfo(-203);
            rs.next();
            return new InvitationInfo(rs.getInt("id"), rs.getString("user_name"), rs.getInt("clan_id"));
        } catch (SQLException e) {
            e.printStackTrace();
            return new InvitationInfo(-204);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////
    
    public static int getInvitationCount(String plrName){
        SafeSql sql = MTC.instance().ssql;
        ResultSet rs = sql.safelyExecuteQuery("SELECT COUNT(*) AS cnt FROM "+sql.dbName+"."+Const.TABLE_CLAN_INVITATIONS+" WHERE user_name=?", plrName);
        try {
            if(rs == null || !rs.isBeforeFirst()) return 0;
            rs.next();
            return rs.getInt("cnt");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public static int getInvitationCount(String plrName, int clanId){
        SafeSql sql = MTC.instance().ssql;
        ResultSet rs = sql.safelyExecuteQuery("SELECT COUNT(*) AS cnt FROM "+sql.dbName+"."+Const.TABLE_CLAN_INVITATIONS+" WHERE user_name=? AND clan_id="+clanId, plrName);
        try {
            if(rs == null || !rs.isBeforeFirst()) return 0;
            rs.next();
            return rs.getInt("cnt");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    ////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns all InviatationInfos in a human-readable format.
     * @param plrName 
     * @param cryIfNone If true, returns a message if there are none; else null is returned.
     * @return fullInfo
     */
    public static String getInvitationString(String plrName, boolean cryIfNone){
        if(InvitationInfo.invStringCache.containsKey(plrName)) return InvitationInfo.invStringCache.get(plrName);//do it twice, safe a SQL query.
        return InvitationInfo.getInvitationString(plrName, InvitationInfo.getByName(plrName), cryIfNone);
    }
    
    /**
     * Returns all InviatationInfos in a human-readable format.
     * @param plrName
     * @param set Set that was fetched before by {@link InvitationInfo#getByName(String)}
     * @param cryIfNone If true, returns a message if there are none; else null is returned.
     * @return fullInfo
     */
    public static String getInvitationString(String plrName, Set<InvitationInfo> set, boolean cryIfNone){
        if(InvitationInfo.invStringCache.containsKey(plrName)) return InvitationInfo.invStringCache.get(plrName);
        if(set == null || set.isEmpty()) return (cryIfNone) ?
                MTCHelper.loc("XC-noinvs", plrName, true) : null;
        String rtrn = StringUtils.rightPad("§3║", 38, '▒')+"║\n";
        for(InvitationInfo ii : set){
            ClanInfo ci = ClanHelper.getClanInfoById(ii.clanId);
            if(ci.id < 0){
                rtrn += MTCHelper.locArgs("XC-cifetcherr", plrName, true, ci.id);
            }
            rtrn += MTCHelper.locArgs("XC-invitation", plrName, true, ci.name, MTC.codeChatCol, MTC.priChatCol, ci.id);
            rtrn += StringUtils.rightPad("§3║", 38, '▒')+"║";
        }
        InvitationInfo.invStringCache.put(plrName, rtrn);
        return rtrn;
    }
    
    ////////////////////////////////////////////////////////////////////////
    
    public static boolean hasInvitation(String userName){
        SafeSql sql = MTC.instance().ssql;
        ResultSet rs = sql.safelyExecuteQuery("SELECT COUNT(*) AS cnt FROM "+sql.dbName+"."+Const.TABLE_CLAN_INVITATIONS+" WHERE user_name=?", userName);
        try {
            if(rs == null || !rs.isBeforeFirst()) return false;
            rs.next();
            return rs.getInt("cnt") > 0;//checks if there are any invitations <-- 
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // <-- returns false for security
        }
    }
    public static boolean hasInvitationFrom(int clanId, String userName){
        SafeSql sql = MTC.instance().ssql;
        ResultSet rs = sql.safelyExecuteQuery("SELECT COUNT(*) AS cnt FROM "+sql.dbName+"."+Const.TABLE_CLAN_INVITATIONS+" WHERE user_name=? AND clan_id="+clanId, userName);
        try {
            if(rs == null || !rs.isBeforeFirst()) return false;
            rs.next();
            return rs.getInt("cnt") > 0;//checks if there are any invitations <-- 
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // <-- returns false for security
        }
    }
}
/*
CREATE TABLE `mtc_clan_invitations` (
    `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_name` VARCHAR(30) NOT NULL,
    `clan_id` INT(10) UNSIGNED NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `user_name_clan_id` (`user_name`, `clan_id`)
)
COLLATE='utf8_general_ci'
ENGINE=MyISAM
AUTO_INCREMENT=2;
*/
