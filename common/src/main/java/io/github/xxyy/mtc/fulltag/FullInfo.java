package io.github.xxyy.mtc.fulltag;

import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.common.util.ToShortStringable;
import io.github.xxyy.mtc.MTC;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class FullInfo implements ToShortStringable {
    public int id = -1;
    public String senderName = "UNKNOWN";
    public String receiverName = "UNKNWN";//yes, this is spelled as intended.
    public long timestamp = 0; //UNIX seconds (conversion!)
    public String comment = "/* Kein Kommentar. */";
    public boolean thorns = false;
    public int x = -42001;
    public int y = -42;
    public int z = -42002;
    public boolean inEnderchest = false;
    public long lastseen = 0;//UNIX seconds (conversion!)
    public String lastCode = "initialized";//where it was last seen
    public byte partId = -1;//0=CHEST; 1=LEG; 2=BOOTS; 3=LEGS; 4=SWORD
    public String lastOwnerName = "UNCHANGED"; //TODO uuid

    /**
     * errCode MUST be nagative...else basically everything will fail.
     *
     * @param errCode new ID
     */
    private FullInfo(int errCode) { //REFACTOR This should be something on its own
        this.id = errCode;
    }

    private FullInfo(int id, String senderName, String receiverName, long timestamp,
                     String comment, boolean thorns, int x, int y, int z, boolean inEnderChest,
                     long lastSeen, String lastCode, byte partId, String lastOwnerName) { //REFACTOR do we need that many params?
        this.id = id;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.timestamp = timestamp;
        this.thorns = thorns;
        this.x = x;
        this.y = y;
        this.z = z;
        this.inEnderchest = inEnderChest;
        this.lastseen = lastSeen;
        this.comment = comment;
        this.lastCode = lastCode;
        this.partId = partId;
        this.lastOwnerName = lastOwnerName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FullInfo)) {
            return false;
        }
        FullInfo other = (FullInfo) obj;
        return this.id == other.id;
    }

    public void flush() {
        SafeSql sql = MTC.instance().getSql();
        if (sql == null) {
            System.out.println("Tried flush FullInfo before reload was complete!");
            return;
        }
        sql.safelyExecuteUpdate("UPDATE " + sql.dbName + ".mtc_fulls SET sender_name=?, " +
                        "receiver_name=?, timestamp=" + this.timestamp + ", comment=?, " +
                        "thorns=" + this.thorns + ",x=" + this.x + ",y=" + this.y + ",z=" + this.z + ",enderchest=" + this.inEnderchest + ", " +
                        "lastseen=" + this.lastseen + ", lastCode=?, part=" + this.partId + ",lastowner=? WHERE id=" + this.id, this.senderName, this.receiverName,
                this.comment, this.lastCode, this.lastOwnerName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.id;
        return result;
    }

    public void nullify() {
        SafeSql sql = MTC.instance().ssql;
        if (sql == null) {
            System.out.println("Tried nullify FullInfo before reload was complete!");
            return;
        }
        sql.executeUpdate("DELETE FROM " + sql.dbName + ".mtc_fulls WHERE id=" + this.id + " LIMIT 1");
    }

    public String toLogString() {
        return "#" + this.id + "=" + this.partId + "|" + this.senderName + "->" + this.receiverName + "@op:" + this.lastCode;
    }

    @Override
    public String toShortString() {
        return "§3#§5" + this.id + "§3|§a" + this.senderName + "§3->§b" + this.receiverName + "§3@§6" + (new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(this.timestamp * 1000) + "§3");
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{FullInfo id=" + this.id + ",sender=" + this.senderName + ",rec=" + this.receiverName + "," +
                "comment=" + this.comment + ",thorns=" + this.thorns + ",part=" + FullInfo.getPartNameById(this.partId) + ",lastowner=" + this.lastOwnerName +
                ",lastCode='" + this.lastCode + "',lastseen=" + this.lastseen + "}";
    }

    public static FullInfo create(String senderName, String receiverName,
                                  String comment, boolean thorns, int x, int y, int z, String lastCode, byte partId) {
        SafeSql sql = MTC.instance().getSql();
        if (sql == null) {
            System.out.println("Tried to create FullInfo before reload was complete!");
            return new FullInfo(-2);
        }
        long currTimestamp = ((Calendar.getInstance().getTimeInMillis() / 1000));
        sql.safelyExecuteUpdate("INSERT INTO " + sql.dbName + ".mtc_fulls SET sender_name=?, receiver_name=?," +
                "timestamp=" + currTimestamp + ",comment=?," +
                "thorns=" + thorns + ",x=" + x + ",y=" + y + ",z=" + z + ",enderchest=false, lastseen=timestamp," +
                "lastCode=?,part=" + partId + ",lastowner=receiver_name", senderName, receiverName, comment, lastCode);
        ResultSet rs = sql.executeQuery("SELECT LAST_INSERT_ID()");
        if (rs == null) {
            System.out.println("rs == null -> db down? FullInfo-create");
            return new FullInfo(-3);
        }
        try {
            rs.next();
            int id = rs.getInt(1);
            return new FullInfo(id, senderName, receiverName, currTimestamp, comment, thorns, x, y, z,
                    false, currTimestamp, lastCode, partId, receiverName);
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "FULLINFO-create");
        }
        return new FullInfo(-4);
    }

    public static FullInfo getById(int id) {
        if (id < 0) {
            return new FullInfo(-11);
        }
        SafeSql sql = MTC.instance().getSql();
        if (sql == null) {
            System.out.println("Tried to get FullInfo by id before reload was complete!");
            return new FullInfo(-5);
        }
        ResultSet rs = sql.safelyExecuteQuery("SELECT * FROM " + sql.dbName + ".mtc_fulls WHERE id=?", id);
        if (rs == null) {
            System.out.println("rs == null -> db down? FullInfo-getById");
            return new FullInfo(-3);
        }
        try {
            if (!rs.isBeforeFirst()) {
                return new FullInfo(-10);
            }
            rs.next();
            return new FullInfo(id, rs.getString("sender_name"), rs.getString("receiver_name"), rs.getLong("timestamp"),
                    rs.getString("comment"), rs.getBoolean("thorns"), rs.getInt("x"), rs.getInt("y"), rs.getInt("z"),
                    rs.getBoolean("enderchest"), rs.getLong("lastseen"), rs.getString("lastCode"), rs.getByte("part"), rs.getString("lastowner"));
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "FULLINFO-getById");
        }
        return new FullInfo(-4);
    }

    public static String getPartNameById(int partId) {
        switch (partId) {
            case 0:
                return "DIAMOND_CHEST";
            case 1:
                return "DIAMOND_LEGGINGS";
            case 2:
                return "DIAMOND_BOOTS";
            case 3:
                return "DIAMOND_HELMET";
            case 4:
                return "DIAMOND_SWORD";
            default:
                return "{UNKNOWN_ITEM id=" + partId + "}";
        }
    }
}
