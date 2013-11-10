package io.github.xxyy.minotopiacore.clan;

import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.minotopiacore.Const;
import io.github.xxyy.minotopiacore.MTC;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;


public class ClanMemberInfo {
    public enum ClanRank{
        MEMBER, MODERATOR, ADMIN, LEADER
    }
    public String userName = "Mark_Meier";
    public int clanId = -100;
    public int userPermissions = 0;//no permission
    
    public short userRankId = 0;
    public ClanMemberInfo(int errCode){//< -100
        this.clanId = errCode;
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////
    
    public ClanMemberInfo(String userName, int clanId, short userRankId, int userPermissions){
        this.userName=userName; this.clanId=clanId; this.userRankId=userRankId;
        this.userPermissions = userPermissions;
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////
    
    public void flush(){
        SafeSql sql = MTC.instance().ssql;
        sql.safelyExecuteUpdate("UPDATE "+sql.dbName+"."+Const.TABLE_CLAN_MEMBERS+" SET " +
        		"clan_id="+this.clanId+", user_rank="+this.userRankId+", user_permissions='"+
        		StringUtils.leftPad(Integer.toBinaryString(this.userPermissions), 21, '0')+"' WHERE user_name=?", this.userName);
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////
    
    public ClanInfo getClanInfo(){
        return ClanHelper.getClanInfoById(this.clanId);
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////
    
    public ClanRank getRank(){
        switch(this.userRankId){
        case 0:
            return ClanRank.MEMBER;
        case 1:
            return ClanRank.MODERATOR;
        case 2:
            return ClanRank.ADMIN;
        case 3:
            return ClanRank.LEADER;
        default:
            return ClanRank.MEMBER;
        }
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////
    
    public void nullify(){
        SafeSql sql = MTC.instance().ssql;
        sql.safelyExecuteUpdate("DELETE FROM "+sql.dbName+"."+Const.TABLE_CLAN_MEMBERS+" WHERE user_name=?", this.userName);
    }
    
    public static ClanMemberInfo create(String plrName, int clanId, short userRankId, int userPermissions){
       SafeSql sql = MTC.instance().ssql;
       int rows = sql.safelyExecuteUpdate("INSERT INTO "+sql.dbName+"."+Const.TABLE_CLAN_MEMBERS+" SET " +
       		"user_name=?, clan_id="+clanId+", user_rank="+userRankId+",user_permissions='"+StringUtils.leftPad(Integer.toBinaryString(userPermissions), 21, '0')+"'", plrName);
       if(rows > 0) return new ClanMemberInfo(plrName, clanId, userRankId, userPermissions);
       return new ClanMemberInfo(-104);
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////
    
    public static short getRankIdFromString(String str){
        switch(str.toLowerCase()){
        case "member":
        case "mitglied":
        case "noob":
        case "0":
            return 0;
        case "mod":
        case "moderator":
        case "supporter":
        case "supp":
        case "1":
            return 1;
        case "admin":
        case "administrator":
        case "adminisrator": //hehe
        case "2":
            return 2;
        case "leader":
        case "leiter":
        case "owner":
        case "besitzer":
        case "gründer":
        case "founder":
        case "root":
        case "3":
            return 3;
        default:
            return -1;
        }
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////
    
    public static String getRankName(short id){
        switch(id){
        case 0:
            return "MITGLIED";
        case 1:
            return "MODERATOR";
        case 2:
            return "ADMIN";
        case 3:
            return "LEITER";
        default:
            return "MITGLIED";
        }
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////
    
    protected static ClanMemberInfo getByPlayerName(String plrName){
        SafeSql sql = MTC.instance().ssql;
        if(sql == null) return new ClanMemberInfo(-111);
        ResultSet rs = sql.safelyExecuteQuery("SELECT * FROM "+sql.dbName+"."+Const.TABLE_CLAN_MEMBERS+" WHERE user_name=?", plrName);
        if(rs == null) return new ClanMemberInfo(-101);
        try {
            if(!rs.next()) return new ClanMemberInfo(-102);
            String permStr = rs.getString("user_permissions");
            int intPerms;
            if(StringUtils.isNumeric(permStr)) {
                intPerms = Integer.parseInt(permStr, 2);
            } else {
                intPerms = ClanPermission.MEMBER_PERMISSIONS;
            }
            return new ClanMemberInfo(rs.getString("user_name"), rs.getInt("clan_id"), rs.getShort("user_rank"), intPerms);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ClanMemberInfo(-103);
        }
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////
    
/*
 * CREATE TABLE `mtc_clan_members` (
    `user_name` VARCHAR(50) NOT NULL COLLATE 'latin1_swedish_ci',
    `clan_id` INT(10) UNSIGNED NOT NULL,
    `user_rank` TINYINT(4) UNSIGNED NOT NULL DEFAULT '0' COMMENT '0=member;1=mod;2=admin;3=leader',
    `user_permissions` BINARY(21) NOT NULL DEFAULT '000000100010010001000',
    UNIQUE INDEX `clan_id_user_name` (`user_name`)
)
COMMENT='Clan members'
COLLATE='utf8_general_ci'
ENGINE=MyISAM;
*/
}
