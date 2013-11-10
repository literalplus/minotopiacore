package io.github.xxyy.minotopiacore.clan;

import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.clan.ClanMemberInfo.ClanRank;
import io.github.xxyy.minotopiacore.helper.MTCHelper;

import org.bukkit.command.CommandSender;


public class ClanPermission {
    public enum Permission{
        //LEADER
        RESET{
            @Override public int bitValue(){ return 0b1_0000_0000_0000_0000_0000; }
        },REMOVE{
            @Override public int bitValue(){ return 0b0_1000_0000_0000_0000_0000; }
        },SETOPTION{
            @Override public int bitValue(){ return 0b0_0100_0000_0000_0000_0000; }
        },ALLIANCEENEMY{
            @Override public int bitValue(){ return 0b0_0010_0000_0000_0000_0000; }
        },WAR{
            @Override public int bitValue(){ return 0b0_0001_0000_0000_0000_0000; }
        },BUY{
            @Override public int bitValue(){ return 0b0_0000_1000_0000_0000_0000; }
        },IGNOREKICK{
            @Override public int bitValue(){ return 0b0_0000_0100_0000_0000_0000; }
        },SETRANK{
            @Override public int bitValue(){ return 0b0_0000_0010_0000_0000_0000; }
        },
        //ADMIN
        SETSEARCH{
            @Override public int bitValue(){ return 0b0_0000_0001_0000_0000_0000; }
        },KICK{
            @Override public int bitValue(){ return 0b0_0000_0000_1000_0000_0000; }
        },SETBASE{
            @Override public int bitValue(){ return 0b0_0000_0000_0100_0000_0000; }
        },BANKGET{
            @Override public int bitValue(){ return 0b0_0000_0000_0010_0000_0000; }
        },
        //MODERATOR
        INVITE{
            @Override public int bitValue(){ return 0b0_0000_0000_0001_0000_0000; }
        },REVOKE{
            @Override public int bitValue(){ return 0b0_0000_0000_0000_1000_0000; }
        },CHATCOLSPECIAL{
            @Override public int bitValue(){ return 0b0_0000_0000_0000_0100_0000; }
        },MUTECHAT{
            @Override public int bitValue(){ return 0b0_0000_0000_0000_0010_0000; }
        },
        //MEMBER
        CHEST{
            @Override public int bitValue(){ return 0b0_0000_0000_0000_0001_0000; }
        },CHATCOL{
            @Override public int bitValue(){ return 0b0_0000_0000_0000_0000_1000; }
        },TPBASE{
            @Override public int bitValue(){ return 0b0_0000_0000_0000_0000_0100; }
        },USECHAT{
            @Override public int bitValue(){ return 0b0_0000_0000_0000_0000_0010; }
        };
        public abstract int bitValue();
    }
    
    public static final int LEADER_PERMISSIONS = 0b1_1111_1111_1111_1111_1111;
    public static final int ADMIN_PERMISSIONS = 0b0_0000_0001_1111_1111_1111;
    public static final int MODERATOR_PERMISSIONS = 0b0_0000_0000_0001_1111_1111;
    public static final int MEMBER_PERMISSIONS = 0b0_0000_0000_0000_0001_1111;
    
    public static int getDefaultPermissionsByRank(ClanRank rank){
        switch(rank){
        case LEADER:
            return ClanPermission.LEADER_PERMISSIONS;
        case ADMIN:
            return ClanPermission.ADMIN_PERMISSIONS;
        case MODERATOR:
            return ClanPermission.MODERATOR_PERMISSIONS;
        default:
            return ClanPermission.MEMBER_PERMISSIONS;
        }
    }
    
    /**
     * Checks if a player has a permission.
     * @param cmi
     * @param perm
     * @return if.
     * @author xxyy98<xxyy98@gmail.com
     */
    public static boolean has(ClanMemberInfo cmi, Permission perm){
//        switch(perm){
//        case REMOVE:
//        case IGNOREKICK:
//        case SETRANK:
//            return cmi.userRankId == 3;//leader
//        case KICK:
//        case SETBASE:
//            return cmi.userRankId >= 2;//admin
//        case CHATCOLSPECIAL:
//        case INVITE:
//        case REVOKE:
//        case MUTECHAT:
//            return cmi.userRankId >= 1;//mod
//        default:
//            return true;//member
//        }
        return ClanPermission.has(cmi.userPermissions, perm);
    }
    
    /**
     * Checks if a permission integer has been flagged to
     * have <code>perm</code>.
     * @param compareValue permission integer
     * @param perm Permission to check for
     * @return if the integer is marked with this permission
     * @author xxyy98<xxyy98@gmail.com
     */
    public static boolean has(int compareValue, Permission perm){
        return (perm.bitValue() & compareValue) != 0;
    }
    
    /**
     * Checks if a player has a permission.
     * Also sends a message if not.
     * @param cmi
     * @param permission
     * @param sender Who is going to receive the message?
     * @return if.
     * @see ClanPermission#has(ClanMemberInfo, Permission)
     * @author xxyy98<xxyy98@gmail.com
     */
    public static boolean hasAndMessage(ClanMemberInfo cmi, Permission perm, CommandSender sender){
        if(!ClanPermission.has(cmi, perm) && !sender.hasPermission("mtc.clana.override")) return !MTCHelper.sendLocArgs("XC-noperm", sender, true, perm.toString(), MTC.codeChatCol);
        return true;
    }
    
    /**
     * Checks if a player has a permission.
     * Also sends a message if not.
     * @param sender Receiver of the message and also subject of the check.
     * @param perm Permission to seek.
     * @return if.
     * @see ClanPermission#has(ClanMemberInfo, Permission)
     * @author xxyy98<xxyy98@gmail.com
     */
    public static boolean hasAndMessage(CommandSender sender, Permission perm){
        String senderName = sender.getName();
        if(!ClanHelper.isInAnyClan(senderName)) return false;
        ClanMemberInfo cmi = ClanHelper.getMemberInfoByPlayerName(senderName);
        if(cmi.clanId < 0) return MTCHelper.sendLocArgs("XC-cmifetcherr", sender, true, cmi.clanId);
        return ClanPermission.hasAndMessage(cmi, perm, sender);
//        if(!ClanPermission.has(cmi, perm)) return !MTCHelper.sendLocArgs("XC-noperm", sender, true, perm.toString(), MTC.codeChatCol);
//        return true;
    }
}
