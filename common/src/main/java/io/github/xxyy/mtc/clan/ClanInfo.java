/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.clan;

import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.mtc.Const;
import io.github.xxyy.mtc.MTC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;


public class ClanInfo {

    public int id = -1;
    public String name = "Aperture Science";
    public String prefix = "APER";
    public String leaderName = "GLaDOS";
    public Location base = new Location(Bukkit.getWorlds().get(0), 0, 70, 0);
    public String motd = "Neurotoxin is the new black!";
    public int money = -142;
    public int level = -1;
    public int kills = -23;
    public int deaths = -48;

    ///////////////////////////////////////////////////////////////////////////////////////////

    private ClanInfo(int errCode) {
        this.id = errCode;
    }

    private ClanInfo(int id, String name, String prefix, String leaderName, Location base,
                     String motd, int money, int level, int kills, int deaths) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.leaderName = leaderName;
        this.base = base;
        this.motd = motd;
        this.money = money;
        this.level = level;
        this.kills = kills;
        this.deaths = deaths;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a ClanInfo and sets all non-given values to default. (as specified in SQL)
     *
     * @param name   Name for new clan
     * @param prefix Chatprefix
     * @param leader Who created the clan. Location will also be fetched from this player.
     * @return see {@link ClanInfo#create(String, String, String, Location, String, int, int, int, int)}
     * @see ClanInfo#create(String, String, String, Location, String, int, int, int, int)
     */
    public static ClanInfo create(String name, String prefix, Player leader) {
        return ClanInfo.create(name, prefix, leader.getName(), leader.getLocation(),
                "Hier könnte eine MotD stehen!", 0, 0, 0, 0);
    }

    /**
     * Creates a new ClanInfo and inserts values into db.
     *
     * @param name       Name for new clan
     * @param prefix     Chatprefix
     * @param leaderName New clan leader's name
     * @param base       base location. Normally current location of leader (will be set later)
     * @param motd       MotD..will be shown at login, changeable
     * @param money      Amount of money in clan bank
     * @param level      Clan level. &lt; 255
     * @param kills      Kills by all members, but only since creation of clan
     * @param deaths     Kills of all members, but only since creation of clan
     * @return on success, new ClanInfo | else, a ClanInfo with a NEGATIVE id.
     */
    public static ClanInfo create(String name, String prefix, String leaderName, Location base,
                                  String motd, int money, int level, int kills, int deaths) {
        SafeSql sql = MTC.instance().getSql();
        if (sql == null) {
            return new ClanInfo(-2);
        }
        int rows = sql.safelyExecuteUpdate("INSERT INTO " + sql.dbName + "." + Const.TABLE_CLANS + " " +
                        "SET `name`=?, `prefix`=?, `leader_name`=?,\n " +
                        "base_x=" + base.getBlockX() + ", \n" +
                        "base_y=" + base.getBlockY() + ", \n" +
                        "base_z=" + base.getBlockZ() + ", \n" +
                        "base_yaw=" + (int) base.getYaw() + ", \n" +
                        "base_pitch=" + (int) base.getPitch() + ", \n" +
                        "base_world=?, motd=?, bank_amount=" + money + ", \n" +
                        "level=" + level + ", kills=" + kills + ", deaths=" + deaths,
                name, prefix, leaderName, base.getWorld().getName(), motd);
        if (rows < 1) {
            return new ClanInfo(-3);
        }
        ResultSet rs = sql.executeQuery("SELECT LAST_INSERT_ID()"); //REFACTOR
        if (rs == null) {
            return new ClanInfo(-4);
        }
        ClanInfo rtrn;
        try {
            if (!rs.isBeforeFirst()) {
                return new ClanInfo(-5);
            }
            rs.next();
            rtrn = new ClanInfo(rs.getInt(1), name, prefix, leaderName, base, motd,
                    money, level, kills, deaths);
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "ClanInfo.create()");
            rtrn = new ClanInfo(-6);
        }
        ClanHelper.cacheById.put(rtrn.id, rtrn);
        ClanHelper.cacheByName.put(rtrn.name, rtrn);
        return rtrn;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    protected static ClanInfo getById(int id) {
        if (id < 0) {
            return new ClanInfo(id);
        }
        SafeSql sql = MTC.instance().getSql();
        if (sql == null) {
            return new ClanInfo(-2);
        }
        ResultSet rs = sql.safelyExecuteQuery("SELECT * FROM " + sql.dbName + "." + Const.TABLE_CLANS + " WHERE id=?", id); //REFACTOR
        try {
            if (rs == null || !rs.isBeforeFirst()) {
                return new ClanInfo(-3);
            }
            rs.next();
            return new ClanInfo(rs.getInt("id"), rs.getString("name"), rs.getString("prefix"),
                    rs.getString("leader_name"),
                    new Location(Bukkit.getWorld(rs.getString("base_world")),
                            rs.getInt("base_x"), rs.getInt("base_y"),
                            rs.getInt("base_z"), rs.getInt("base_pitch"),
                            rs.getInt("base_yaw")),
                    rs.getString("motd"), rs.getInt("bank_amount"),
                    rs.getShort("level"), rs.getInt("kills"), rs.getInt("deaths"));
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "ClanInfo.getById()");
            return new ClanInfo(-4);
        }
    }

    protected static ClanInfo getByName(String name) {
        SafeSql sql = MTC.instance().getSql();
        if (sql == null) {
            return new ClanInfo(-2);
        }
        ResultSet rs = sql.safelyExecuteQuery("SELECT * FROM " + sql.dbName + "." + Const.TABLE_CLANS + " WHERE name=?", name); //REFACTOR
        try {
            if (rs == null || !rs.isBeforeFirst()) {
                return new ClanInfo(-3);
            }
            rs.next();
            return new ClanInfo(rs.getInt("id"), rs.getString("name"), rs.getString("prefix"),
                    rs.getString("leader_name"),
                    new Location(Bukkit.getWorld(rs.getString("base_world")),
                            rs.getInt("base_x"), rs.getInt("base_y"),
                            rs.getInt("base_z"), rs.getInt("base_pitch"),
                            rs.getInt("base_yaw")),
                    rs.getString("motd"), rs.getInt("bank_amount"),
                    rs.getShort("level"), rs.getInt("kills"), rs.getInt("deaths"));
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "ClanInfo.getByName()");
            return new ClanInfo(-4);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    protected static ClanInfo getByPlayerName(String plrName) {
        ClanMemberInfo cmi = ClanHelper.getMemberInfoByPlayerName(plrName);
        if (cmi == null) {
            return new ClanInfo(-7);
        }
        if (cmi.clanId < 0) {
            return new ClanInfo(cmi.clanId);
        }
        return ClanHelper.getClanInfoById(cmi.clanId);
    }

    protected static ClanInfo getByPrefix(String prefix) {
        SafeSql sql = MTC.instance().getSql();
        if (sql == null) {
            return new ClanInfo(-2);
        }
        ResultSet rs = sql.safelyExecuteQuery("SELECT * FROM " + sql.dbName + "." + Const.TABLE_CLANS + " WHERE prefix=?", prefix); //REFACTOR
        try {
            if (rs == null || !rs.isBeforeFirst()) {
                return new ClanInfo(-3);
            }
            rs.next();
            return new ClanInfo(rs.getInt("id"), rs.getString("name"), rs.getString("prefix"),
                    rs.getString("leader_name"),
                    new Location(Bukkit.getWorld(rs.getString("base_world")),
                            rs.getInt("base_x"), rs.getInt("base_y"),
                            rs.getInt("base_z"), rs.getInt("base_pitch"),
                            rs.getInt("base_yaw")),
                    rs.getString("motd"), rs.getInt("bank_amount"),
                    rs.getShort("level"), rs.getInt("kills"), rs.getInt("deaths"));
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "ClanInfo.getByPrefix()");
            return new ClanInfo(-4);
        }
    }

    /**
     * Writes values of this {@link ClanInfo} into the corresponding row
     * in MySQL. Safe.
     */
    public void flush() {
        SafeSql sql = MTC.instance().ssql;
        if (sql == null) {
            return;
        }
        sql.safelyExecuteUpdate("UPDATE " + sql.dbName + "." + Const.TABLE_CLANS + " " +
                        "SET name=?, prefix=?, leader_name=?," +
                        "base_x=" + this.base.getBlockX() + "," +
                        "base_y=" + this.base.getBlockY() + "," +
                        "base_z=" + this.base.getBlockZ() + "," +
                        "base_yaw=" + (int) this.base.getYaw() + "," +
                        "base_pitch=" + (int) this.base.getPitch() + "," +
                        "base_world=?, motd=?, bank_amount=" + this.money + "," +
                        "level=" + this.level + ", kills=" + this.kills + "," +
                        "deaths=" + this.deaths +
                        " WHERE id=" + this.id, this.name, this.prefix, this.leaderName,
                this.base.getWorld().getName(), this.motd);
    }

    public void nullify() {
        SafeSql sql = MTC.instance().getSql();
        ClanHelper.getAllMemberNames(this.id).stream()
                .filter(ClanHelper.memberCache::containsKey)
                .forEach(ClanHelper.memberCache::remove);

        if (ClanHelper.cacheById.containsKey(this.id)) {//will always be in both maps
            ClanHelper.cacheById.remove(this.id);
            ClanHelper.cacheByName.remove(this.name);
        }
        sql.safelyExecuteUpdate("DELETE FROM " + sql.dbName + "." + Const.TABLE_CLANS + " WHERE id=? LIMIT 1", this.id);
        sql.safelyExecuteUpdate("DELETE FROM " + sql.dbName + "." + Const.TABLE_CLAN_MEMBERS + " WHERE clan_id=?", this.id);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    
/*
CREATE TABLE `mtc_clans` (
    `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `prefix` VARCHAR(10) NOT NULL,
    `leader_name` VARCHAR(30) NOT NULL,
    `base_x` INT(10) NOT NULL DEFAULT '0',
    `base_y` INT(10) NOT NULL DEFAULT '70',
    `base_z` INT(10) NOT NULL DEFAULT '0',
    `base_yaw` INT(10) NOT NULL DEFAULT '0',
    `base_pitch` INT(10) NOT NULL DEFAULT '0',
    `base_world` VARCHAR(50) NOT NULL DEFAULT 'world',
    `searching` INT(2) UNSIGNED NOT NULL DEFAULT '0' COMMENT 'boolean',
    `motd` VARCHAR(255) NOT NULL DEFAULT 'Hier könnte Ihre MotD stehen!',
    `bank_amount` INT(11) NOT NULL DEFAULT '0',
    `level` SMALLINT(6) NOT NULL DEFAULT '0',
    `kills` INT(11) NOT NULL DEFAULT '0',
    `deaths` INT(11) NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `name` (`name`)
)
COLLATE='utf8_general_ci'
ENGINE=MyISAM
AUTO_INCREMENT=13;
*/

}
