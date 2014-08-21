package io.github.xxyy.mtc.bans;

import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.mtc.MTC;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;


public class BanInfo {
    public int id = -1;
    public String plrName = "UNKNOWN";
    public String bannedByName = "CONSOLE";
    public long banTimestamp = 0;
    public long banExpiryTimestamp = 0;
    public byte genericReasonId = 0;
    public String reason = "Deine Mudda ist fett. (AKA unbekannter Grund)";

    public BanInfo() {
    }

    public BanInfo(int id) {
        this.id = id;
    }//error codes...use negative values

    public BanInfo(int id, String plrName, String bannedByName, long banTimestamp, long banExpiryTimestamp, byte genericReasonId, String reason) {
        this.id = id;
        this.plrName = plrName;
        this.bannedByName = bannedByName;
        this.banTimestamp = banTimestamp;
        this.banExpiryTimestamp = banExpiryTimestamp;
        this.genericReasonId = genericReasonId;
        this.reason = reason;
    }

    /**
     * Saves modifications to database.
     *
     * @return whether data was sent.
     */
    public boolean flush() {
        SafeSql sql = MTC.instance().getSql();
        if (sql == null) //no msg
        {
            return false;
        }
        if (this.id < 0) {
            return false;
        }
        long currentTimestamp = (Calendar.getInstance().getTimeInMillis() / 1000L);/* convert to unix time */
        return sql.safelyExecuteUpdate("UPDATE " + sql.dbName + ".mtc_bans SET user_name=?,banned_by_name=?" +
                        ",time_banned=" + currentTimestamp +
                        ",time_expiry=" + this.banExpiryTimestamp + ",generic_reason=" + this.genericReasonId + ",reason=? WHERE id = " + this.id,
                this.plrName, this.bannedByName, this.reason) >= 0;
    }

    /**
     * Removes the ban; i.e. unbans the user
     */
    public boolean nullify() {
        SafeSql sql = MTC.instance().getSql();
        return sql != null &&
                this.id >= 0 &&
                sql.executeUpdate("DELETE FROM " + sql.dbName + ".mtc_bans WHERE id=" + this.id);
    }

    /**
     * creates a new baninfo..AKA bans an user
     *
     * @param plrName            name of banned player
     * @param bannedByName       name of player who banned
     * @param reason             reason
     * @param genericReasonId    nyi
     * @param banExpiryTimestamp expiry timestamp, unix time
     * @return BanInfo...if id < 0, then error
     */
    public static BanInfo create(String plrName, String bannedByName, String reason, byte genericReasonId, long banExpiryTimestamp) {
        SafeSql sql = MTC.instance().ssql;
        if (sql == null) //no msg
        {
            return new BanInfo(-3);
        }
        long currentTimestamp = (Calendar.getInstance().getTimeInMillis() / 1000L);/* convert to unix time */
        boolean suc = sql.safelyExecuteUpdate("INSERT INTO " + sql.dbName + ".mtc_bans SET user_name=?,banned_by_name=?" +
                ",time_banned=" + currentTimestamp +
                ",time_expiry=" + banExpiryTimestamp + ",generic_reason=" + genericReasonId + ",reason=?", plrName, bannedByName, reason) >= 0;
        if (!suc) {
            return new BanInfo(-3);
        }
        ResultSet rs = sql.executeQuery("SELECT LAST_INSERT_ID()");
        if (rs == null) {
            System.out.println("§4[MTC] rs == null -> db down? (1)");
            return new BanInfo(-3);
        }
        int id;
        try {
            rs.next();
            id = rs.getInt(1);
        } catch (SQLException e) {
            return new BanInfo(-3);
        }
//		APIHelper.sendUpdateToAPI(id, (byte)2, "1");
        return new BanInfo(id, plrName, bannedByName, currentTimestamp, banExpiryTimestamp, genericReasonId, reason);
    }

    /**
     * gets baninfo by id
     *
     * @param id Id
     * @return BanInfo
     */
    public static BanInfo getById(int id) {
        SafeSql sql = MTC.instance().ssql;
        if (sql == null) //no msg
        {
            return new BanInfo(-3);
        }
        ResultSet rs = sql.executeQuery("SELECT * FROM " + sql.dbName + ".mtc_bans WHERE id='" + id + "'");//no need to escape cause of type
        if (rs == null) {
            System.out.println("§4[MTC] rs == null -> db down? (1)");
            return new BanInfo(-3);
        }
        try {
            if (!rs.isBeforeFirst()) {
                return new BanInfo(-2); //->error
            }
            rs.next();
            String plrName = rs.getString("user_name");
            String bannedByName = rs.getString("banned_by_name");
            long banTimestamp = rs.getLong("time_banned");
            long banExpiryTimestamp = rs.getLong("time_expiry");
            byte genericReasonId = rs.getByte("generic_reason");
            String reason = rs.getString("reason");
            return new BanInfo(id, plrName, bannedByName, banTimestamp, banExpiryTimestamp, genericReasonId, reason);
        } catch (SQLException e) {
            e.printStackTrace();
            sql.formatAndPrintException(e, "[MTC] Bans-5");
        }
        return new BanInfo(-3);
    }

    /**
     * gets baninfo by name
     *
     * @param plrName player name
     * @return BanInfo
     */
    public static BanInfo getByName(String plrName) {
        SafeSql sql = MTC.instance().ssql;
        if (sql == null) //no msg
        {
            return new BanInfo(-3);
        }
        ResultSet rs = sql.safelyExecuteQuery("SELECT * FROM " + sql.dbName + ".mtc_bans WHERE user_name=?", plrName);
        if (rs == null) {
            System.out.println("§4[MTC] rs == null -> db down? (1)");
            return new BanInfo(-3);
        }
        try {
            if (!rs.isBeforeFirst()) {
                return new BanInfo(-2); //->error
            }
            rs.next();
            int id = rs.getInt("id");
            String bannedByName = rs.getString("banned_by_name");
            long banTimestamp = rs.getLong("time_banned");
            long banExpiryTimestamp = rs.getLong("time_expiry");
            byte genericReasonId = rs.getByte("generic_reason");
            String reason = rs.getString("reason");
            return new BanInfo(id, plrName, bannedByName, banTimestamp, banExpiryTimestamp, genericReasonId, reason);
        } catch (SQLException e) {
            e.printStackTrace();
            sql.formatAndPrintException(e, "[MTC] Bans-1");
        }
        return new BanInfo(-3);
    }
}
