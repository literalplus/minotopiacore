package io.github.xxyy.mtc.warns;

import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.bans.BanHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public final class WarnHelper { //REFACTOR

    private WarnHelper() {

    }

    public static List<String> playerTimeouts = new ArrayList<>();

    public static WarnInfo addWarn(String plrName, CommandSender sender, String reason, byte genericReasonId) {
        return WarnInfo.create(plrName.toLowerCase(), sender.getName().toLowerCase(), reason, genericReasonId);
    }

    public static void checkWarnNumberAndDoStuff(String victimName, CommandSender culprit, byte warnsAdded) {
        WarnHelper.checkWarnNumberAndDoStuff(victimName, culprit, WarnHelper.getWarnCountByPlayerName(victimName), warnsAdded);
    }

    public static void checkWarnNumberAndDoStuff(String victimName, CommandSender culprit, int warnCount, byte warnsAdded) {
        Player victim = Bukkit.getPlayerExact(victimName); //REFACTOR
        if (victim == null || !victim.isOnline()) {
            return;
        }
        if (warnCount >= 10) {
            Bukkit.broadcastMessage(MTC.warnChatPrefix + "§cDer Spieler §e" + victimName + "§c hat §o" + warnCount + "§c Warns erreicht und wurde deswegen" +
                    " §lpermanent§c gebannt.");
            victim.kickPlayer("§c§l[WARNUNG]\n§cDu wurdest §lpermanent§c gebannt,\n" +
                    " weil du §n10 Warnungen §cerreicht hast!\n" +
//					"§eLetzter Warngrund: " + getWarnsByPlayerName(victimName, false).get(0).reason + "\n\n" + 
                    "§6Unban im Forum: §bminotopia.me\n" +
                    "§9Deine Warnungen: §bminotopia.me?p=5&n=" + victimName);
            BanHelper.setBanned(victimName.toLowerCase(), culprit.getName().toLowerCase(), "Du hast " + warnCount + " Warnungen erreicht!", (byte) 0, -42L);
        } else if (warnCount > 7) {
            culprit.sendMessage(MTC.warnChatPrefix + "Der Spieler §b" + victimName + "§6 hat " + warnCount + " Warns und wurde für 4 Stunden gebannt.");
            long timestamp = (Calendar.getInstance().getTimeInMillis() / 1000) + (14400L * warnsAdded);//unix time! (no need to divide because factor is already divided)
            victim.kickPlayer("§4§l[WARNUNG]\n§cDu bist bis §b" + (new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(timestamp * 1000L)) + "§c gebannt,\n" +
                    "§cweil du §o" + warnCount + "§c Warnungen erreicht hast.\n" +
//							"§eLetzter Warngrund: " + getWarnsByPlayerName(victimName, false).get(0).reason + "\n\n" +
                    "§3Bitte beachte /rules!" + "\n§9Deine Warnungen: §bminotopia.me?p=5&n=" + victimName + "\n§6Bei 10 Warnungen wirst du permanent gebannt.");
            BanHelper.setBanned(victimName.toLowerCase(), culprit.getName().toLowerCase(), "Du hast " + warnCount + " Warnungen erreicht!", (byte) 0, timestamp);
        } else if (warnCount > 4) {
            culprit.sendMessage(MTC.warnChatPrefix + "Der Spieler §b" + victimName + "§6 hat " + warnCount + " Warns und wurde für 2 Stunden gebannt.");
            long timestamp = (Calendar.getInstance().getTimeInMillis() / 1000) + (7200L * warnsAdded);//unix time! (no need to divide because factor is already divided)
            victim.kickPlayer("§4§l[WARNUNG]\n§cDu bist bis §b" + (new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(timestamp * 1000L)) + "§c gebannt,\n" +
                    "§cweil du §o" + warnCount + "§c Warnungen erreicht hast.\n" +
//							"§eLetzter Warngrund: " + getWarnsByPlayerName(victimName, false).get(0).reason + "\n\n" + 
                    "§3/rules\n" + "§9Deine Warnungen: §bminotopia.me?p=5&n=" + victimName + "\n" + "§6Bei 8 Warnungen wirst du für 4 Stunden gebannt.");
            BanHelper.setBanned(victimName.toLowerCase(), culprit.getName().toLowerCase(), "Du hast " + warnCount + " Warnungen erreicht!", (byte) 0, timestamp);
        } else {
            culprit.sendMessage(MTC.warnChatPrefix + "Der Spieler §b" + victimName + "§6 hat " + warnCount + " Warns und wurde gekickt.");
            victim.kickPlayer("§4§l[WARNUNG]\n§cDu hast §o" + warnCount + "§c Warnung" + ((warnCount == 1) ? "" : "en") + " erreicht!\n" +
//					"§eLetzter Warngrund:\n" + getWarnsByPlayerName(victimName, false).get(0).reason + "\n\n" +
                    "§3Bitte beachte /rules!\n" +
                    "§9Deine Warnungen: §b/warns\n" +
                    "§6Bei 5 Warnungen wirst du temporär gebannt.");
        }
    }

    public static int getWarnCountByPlayerName(String plrName) {
        SafeSql sql = MTC.instance().ssql;
        if (sql == null) //no msg
        {
            return -1;
        }
        ResultSet rs = sql.safelyExecuteQuery("SELECT COUNT(*) AS num FROM " + sql.dbName + ".mtc_warns WHERE user_name=? AND status=0", plrName);
        if (rs == null) {
            System.out.println("§4[MTC] rs == null -> db down? (3)");
            return -2;
        }
        try {
            //no need for .isBeforeForst() check because the result can never be empty
            rs.next();
            return rs.getInt("num");
        } catch (SQLException e) {
            System.out.println("§4[MTC] Warns-2");
            return -3;
        }
    }

    public static int getWarnCountGivenByPlayerName(String plrName) {
        SafeSql sql = MTC.instance().ssql;
        if (sql == null) //no msg
        {
            return -1;
        }
        ResultSet rs = sql.safelyExecuteQuery("SELECT COUNT(*) AS num FROM " + sql.dbName + ".mtc_warns WHERE warned_by_name=?", plrName);
        if (rs == null) {
            System.out.println("§4[MTC] rs == null -> db down? (3)");
            return -2;
        }
        try {
            //no need for .isBeforeForst() check because the result can never be empty
            rs.next();
            return rs.getInt("num");
        } catch (SQLException e) {
            System.out.println("§4[MTC] Warns-3");
            return -3;
        }
    }

    public static List<WarnInfo> getWarnsByPlayerName(String plrName, boolean asc) {
        if (WarnHelper.getWarnCountByPlayerName(plrName) <= 0) {
            return WarnInfo.getErrorList(-4);
        }
        return WarnInfo.getByName(plrName, asc);
    }
}
