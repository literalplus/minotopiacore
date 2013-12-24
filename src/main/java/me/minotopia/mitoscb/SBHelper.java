package me.minotopia.mitoscb;

import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.minotopiacore.ConfigHelper;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.helper.StatsHelper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public final class SBHelper {

    private SBHelper() {
        throw new AssertionError("SBHelper may not be initialised.");
    }
    private static final HashMap<String, Scoreboard> pBoardCache = new HashMap<>();
    private static boolean selectMoney = true;
    private static boolean selectStats = true;
    private static final String QUERY_BOTH =
            "SELECT mtc_stats.kills AS killz, mtc_stats.deaths AS deathz, ni176987_1_DB.balance AS money FROM mtc_stats "
            + "INNER JOIN ni176987_1_DB ON mtc_stats.user_name=ni176987_1_DB.username WHERE mtc_stats.user_name = ?";
    private static final String QUERY_MONEY = "SELECT balance AS money FROM ni176987_1_DB WHERE username=?";
    private static final String QUERY_STATS = "SELECT kills AS killz, deaths AS deathz FROM mtc_stats WHERE user_name=?";

    public static String getPVPQuery(boolean fetchStats, boolean fetchMoney) {
        if (fetchStats && fetchMoney) {
            return SBHelper.QUERY_BOTH;
        } else if (fetchStats) {
            return SBHelper.QUERY_STATS;
        } else if (fetchMoney) {
            return SBHelper.QUERY_MONEY;
        } else {
            throw new AssertionError("SOMETHING WENT WRONG WITH PRECHECKS.");
        }
    }

    public static void init() {
        SBHelper.selectMoney = (MTC.getEcon() == null);
        SBHelper.selectStats = !ConfigHelper.isStatsEnabled();
    }

    private static Scoreboard getScoreboard(final Player plr) {
        Scoreboard brd;
        if (SBHelper.pBoardCache.containsKey(plr.getName())) {
            brd = SBHelper.pBoardCache.get(plr.getName());
        } else {
            brd = Bukkit.getScoreboardManager().getNewScoreboard();
            SBHelper.pBoardCache.put(plr.getName(), brd);
        }
        return brd;
    }

    private static Objective prepareObjt(String plrName, final Scoreboard brd, final String name) {
        String brdName = name.substring(0, 1) + ((plrName.length() > 15) ? plrName.substring(0, 15) : plrName);
        Objective objt = brd.getObjective(brdName);
        if (objt == null) {
            objt = brd.registerNewObjective(brdName, "dummy");
        }
        if (objt.getDisplaySlot() != DisplaySlot.SIDEBAR) {
            objt.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        objt.setDisplayName("§a" + plrName + " §6| §8" + name);
        return objt;
    }

    private static void setFakeScore(final Objective objt, final String name, final int score) {
        Score scrMoney = objt.getScore(Bukkit.getOfflinePlayer(name));
        scrMoney.setScore(score);
    }

    private static void setScoresPVP(final Player plr, final Scoreboard brd, final int plrCount) {
        String plrName = plr.getName();
        Objective objt = SBHelper.prepareObjt(plrName, brd, "PvP");

        //fetching
        int kills = -1337;
        int deaths = -1337;
        int money = -4201;
        boolean fetchStats = true;
        boolean fetchMoney = true;
        if (!SBHelper.selectStats) {
            int tempKills = StatsHelper.getRealKills(plrName);
            int tempDeaths = StatsHelper.getRealDeaths(plrName);
            if (tempDeaths >= 0 && tempKills >= 0) {
                fetchStats = false;
                deaths = tempDeaths;
                kills = tempKills;
            }
        }
        if (!SBHelper.selectMoney) {
            if (MTC.getEcon().hasAccount(plrName) || MTC.getEcon().createPlayerAccount(plrName)) {
                money = (int) MTC.getEcon().getBalance(plrName);
                fetchMoney = false;
            }
        }
        if (fetchStats || fetchMoney) {
            SafeSql sql = MTC.instance().getSql();
            if (ConfigHelper.isScBReverseSql()) { //TODO remove?
                sql = MTC.instance().ssql2;
            }
            if (sql == null) {
                return;
            }
            ResultSet rs = sql.safelyExecuteQuery(SBHelper.getPVPQuery(fetchStats, fetchMoney), plrName);
            try {
                if (rs == null || !rs.next()) {
                    return;
                }
                if (fetchStats) {
                    kills = rs.getInt("killz");
                    deaths = rs.getInt("deathz");
                }
                if (fetchMoney) {
                    money = rs.getInt("money");
                }

            } catch (SQLException e) {
                sql.formatAndPrintException(e, "[MiToScB]SQL error when trying to get player data2!");
                return;
            }
        }

        //displaying
        if (ConfigHelper.isScBDisplayPlayerCount()) {
            SBHelper.setFakeScore(objt, "§9Spieler:", plrCount);
        }
        SBHelper.setFakeScore(objt, "§7Kills:", kills);
        SBHelper.setFakeScore(objt, "§7Deaths:", deaths);
        SBHelper.setFakeScore(objt, "§7Geld:", money);
    }

    private static void setScoresTM(final Player plr, final Scoreboard brd, final int plrCount) {
        Objective objt = SBHelper.prepareObjt(plr.getName(), brd, "TM");

        SafeSql sql = MTC.instance().ssql2;
        if (ConfigHelper.isScBReverseSql()) {
            sql = MTC.instance().getSql();
        }
        if (sql == null) {
            return;
        }
        int passes, karma, points;

        ResultSet rs = sql.safelyExecuteQuery(
                "SELECT t1.karma, t1.tpoints, t1.tests, t2.passes_amount AS traitor, t3.points\n"
                + "       FROM games.game_users t2\n"
                + "       LEFT JOIN tomt_users AS t1 ON t1.name = t2.username\n"
                + "       LEFT JOIN habakuk.td_users AS t3 ON t3.username = t2.username\n"
                + "       WHERE t1.name=?", plr.getName());
        if (rs == null) {
            return;
        }
        try {
            if (!rs.next()) {
                passes = 0;
                karma = 100;
                points = 0;
            } else {
                karma = rs.getInt("karma");
                passes = rs.getInt("traitor");
                points = rs.getInt("points");
            }
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "[MiToScB]SQL error when trying to fetch player data!");
            return;
        }

        if (ConfigHelper.isScBDisplayPlayerCount()) {
            SBHelper.setFakeScore(objt, "§9Spieler:", plrCount); //TODO make configurable - store lang in static String and fill in init -> performance
        }
        SBHelper.setFakeScore(objt, "§7Karma:", karma);
        SBHelper.setFakeScore(objt, "§7Pässe:", passes);
        SBHelper.setFakeScore(objt, "§6TD§7Punkte:", points);
    }

    public static final class RunnableUpdateBoards implements Runnable {

        @Override
        public void run() {
            final Player[] plrs = Bukkit.getOnlinePlayers();
            final int plrCount = plrs.length;
            if (plrCount < 0) {
                return;
            }

            if (MTC.instance().pvpMode) {
                for (Player plr : plrs) {
                    final Scoreboard brd = SBHelper.getScoreboard(plr);
                    SBHelper.setScoresPVP(plr, brd, plrCount);
                    plr.setScoreboard(brd);
                }
            } else {
                for (Player plr : plrs) {
                    final Scoreboard brd = SBHelper.getScoreboard(plr);
                    SBHelper.setScoresTM(plr, brd, plrCount);
                    plr.setScoreboard(brd);
                }
            }
            if (MTC.instance().cycle) {
                MTC.instance().pvpMode ^= MTC.instance().pvpMode;
            }
        }
    }
}
