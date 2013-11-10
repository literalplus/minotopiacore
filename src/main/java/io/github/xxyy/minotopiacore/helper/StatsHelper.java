package io.github.xxyy.minotopiacore.helper;

import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.minotopiacore.ConfigHelper;
import io.github.xxyy.minotopiacore.Const;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.clan.ClanHelper;
import io.github.xxyy.minotopiacore.clan.ClanInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


public class StatsHelper {
    public static volatile HashMap<String, Integer> killMap = new HashMap<>();
    public static volatile HashMap<String, Integer> deathMap = new HashMap<>();
    
    public static volatile HashMap<String, Integer> killQueue = new HashMap<>();
    public static volatile HashMap<String, Integer> deathQueue = new HashMap<>();
    
    public static void a(){}
    
    public static void cacheModification(String plrName, boolean isKill){
        if(isKill){
            if(StatsHelper.killQueue.containsKey(plrName)){
                StatsHelper.killQueue.put(plrName, StatsHelper.killQueue.get(plrName) + 1);
            }else{
                StatsHelper.killQueue.put(plrName, 1);
            }
        }else{
            if(StatsHelper.deathQueue.containsKey(plrName)){
                StatsHelper.deathQueue.put(plrName, StatsHelper.deathQueue.get(plrName) + 1);
            }else{
                StatsHelper.deathQueue.put(plrName, 1);
            }
        }
    }
    
    public static void createEntry(String plrName){
        SafeSql sql = MTC.instance().ssql;
        sql.safelyExecuteUpdate("INSERT INTO "+sql.dbName+"."+Const.TABLE_STATS+"" +
                " SET user_name=?", plrName);
        StatsHelper.killMap.put(plrName, 0);
        StatsHelper.deathMap.put(plrName, 0);        
    }
    
    public static void createEntry(String plrName, String initialColumn, int initialValue){
        SafeSql sql = MTC.instance().ssql;
        sql.safelyExecuteUpdate("INSERT INTO "+sql.dbName+"."+Const.TABLE_STATS+"" +
        		" SET user_name=?"+((initialColumn.isEmpty()) ? ", "+initialColumn+"="+initialValue : ""), plrName);
        StatsHelper.killMap.put(plrName, ((initialColumn.equals("kills")) ? initialValue : 0));
        StatsHelper.deathMap.put(plrName, ((initialColumn.equals("deaths")) ? initialValue : 0));        
    }
    
    public static void createEntryBoth(String plrName, int kills, int deaths){
        SafeSql sql = MTC.instance().ssql;
        sql.safelyExecuteUpdate("INSERT INTO "+sql.dbName+"."+Const.TABLE_STATS+"" +
                " SET user_name=?, kills="+kills+", deaths="+deaths, plrName);
        StatsHelper.killMap.put(plrName, kills);
        StatsHelper.deathMap.put(plrName, deaths);        
    }
    
    public static void fetchStats(String plrName){
        SafeSql sql = MTC.instance().ssql;
        boolean fetchKills = !StatsHelper.killMap.containsKey(plrName);
        boolean fetchDeaths = !StatsHelper.deathMap.containsKey(plrName);
        if(!fetchKills && !fetchDeaths) return;
        String rows = "kills, deaths";
        if(!fetchKills) {
            rows = "deaths";
        } else if(!fetchDeaths) {
            rows = "kills";
        }
        ResultSet rs = sql.safelyExecuteQuery("SELECT "+rows+" FROM "+sql.dbName+".mtc_stats WHERE user_name=?", plrName);
        try {
            if(rs == null || !rs.next()) return;
            if(fetchKills){
                StatsHelper.killMap.put(plrName, rs.getInt("kills"));
            }
            if(fetchDeaths){
                StatsHelper.deathMap.put(plrName, rs.getInt("deaths"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static synchronized void flushQueue(){
        if(!StatsHelper.killQueue.isEmpty()){
            Iterator<Entry<String, Integer>> killIt = StatsHelper.killQueue.entrySet().iterator();
            while(killIt.hasNext()){
                Entry<String, Integer> entry = killIt.next();
                String plrName = entry.getKey();
                if(StatsHelper.deathQueue.containsKey(entry.getKey())){
                    StatsHelper.addBoth(entry.getValue(), StatsHelper.deathQueue.get(plrName), plrName);
                    StatsHelper.deathQueue.remove(plrName);
                }else{
                    StatsHelper.addStats("kills",entry.getValue(),plrName);
                }
                killIt.remove();
            }
        }
        if(!StatsHelper.deathQueue.isEmpty()){
            Iterator<Entry<String, Integer>> deathIt = StatsHelper.deathQueue.entrySet().iterator();
            while(deathIt.hasNext()){
                Entry<String, Integer> entry = deathIt.next();
                StatsHelper.addStats("deaths",entry.getValue(),entry.getKey());
                deathIt.remove();
            }
        }
    }
    
    /**
     * Gets all deaths (incudling queue!) of a player.
     * @param plrName well...
     * @return count OR -1 on failure
     * @author xxyy98<xxyy98@gmail.com>
     */
    public static int getRealDeaths(String plrName){
        int rtrn = -1;
        if(StatsHelper.deathMap.containsKey(plrName)){
            rtrn = StatsHelper.deathMap.get(plrName) + 
                    ((StatsHelper.deathQueue.containsKey(plrName)) ? StatsHelper.deathQueue.get(plrName) : 0);
        }else{
            StatsHelper.fetchStats(plrName);
            if(StatsHelper.deathMap.containsKey(plrName)){
                rtrn = StatsHelper.deathMap.get(plrName); //at this point, there *can't* be any queue.
            }
        }
        return rtrn;
    }
    
    /**
     * Gets all kills (incudling queue!) of a player.
     * @param plrName well...
     * @return count OR -1 on failure
     * @author xxyy98<xxyy98@gmail.com>
     */
    public static int getRealKills(String plrName){
        int rtrn = -1;
        if(StatsHelper.killMap.containsKey(plrName)){
            rtrn = StatsHelper.killMap.get(plrName) + 
                    ((StatsHelper.killQueue.containsKey(plrName)) ? StatsHelper.killQueue.get(plrName) : 0);
        }else{
            StatsHelper.fetchStats(plrName);
            if(StatsHelper.killMap.containsKey(plrName)){
                rtrn = StatsHelper.killMap.get(plrName); //at this point, there *can't* be any queue.
            }
        }
        return rtrn;
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public static boolean hasStats(String plrName){
        if(StatsHelper.killMap.containsKey(plrName) || StatsHelper.deathMap.containsKey(plrName)) return true;
        SafeSql sql = MTC.instance().ssql;
        ResultSet rs = sql.safelyExecuteQuery("SELECT COUNT(*) as cnt FROM "+sql.dbName+"."+Const.TABLE_STATS+" WHERE user_name=?", plrName);
        try {
            return (rs != null && rs.next() && (rs.getInt("cnt") > 0));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static void addBoth(int killMod, int deathMod, String plrName){
        SafeSql sql = MTC.instance().ssql;
        if(!StatsHelper.hasStats(plrName)) {
            StatsHelper.createEntryBoth(plrName, killMod, deathMod);
        }
        sql.safelyExecuteUpdate("UPDATE "+sql.dbName+"."+Const.TABLE_STATS+" SET kills=kills+"+killMod+",deaths=deaths+"+deathMod+" WHERE user_name=?", plrName);
        if(StatsHelper.killMap.containsKey(plrName)){
            StatsHelper.killMap.put(plrName, StatsHelper.killMap.get(plrName) + killMod);
        }
        if(StatsHelper.deathMap.containsKey(plrName)){
            StatsHelper.deathMap.put(plrName, StatsHelper.deathMap.get(plrName) + deathMod);
        }
        if(ConfigHelper.isClanEnabled()){
            ClanInfo ci = ClanHelper.getClanInfoByPlayerName(plrName);
            if(ci.id > 0){
                ci.kills += killMod;
                ci.deaths += deathMod;
                ci.flush();
            }
        }
    }
    
    private static void addStats(String column, int operation, String plrName){
        SafeSql sql = MTC.instance().ssql;
        if(!StatsHelper.hasStats(plrName)) {
            StatsHelper.createEntry(plrName, column, operation);
        }
        sql.safelyExecuteUpdate("UPDATE "+sql.dbName+"."+Const.TABLE_STATS+" SET "+column+"="+column+"+"+operation+" WHERE user_name=?", plrName);
        if(column.equalsIgnoreCase("kills")){
            if(StatsHelper.killMap.containsKey(plrName)) {
                StatsHelper.killMap.put(plrName, StatsHelper.killMap.get(plrName) + operation);
            }
            if(ConfigHelper.isClanEnabled()){
                ClanInfo ci = ClanHelper.getClanInfoByPlayerName(plrName);
                if(ci.id > 0){
                    ci.kills += operation;
                    ci.flush();
                }
            }
        }
        if(column.equalsIgnoreCase("deaths")){
            if(StatsHelper.deathMap.containsKey(plrName)){
                StatsHelper.deathMap.put(plrName, StatsHelper.deathMap.get(plrName) + operation);
            }
            if(ConfigHelper.isClanEnabled()){
                ClanInfo ci = ClanHelper.getClanInfoByPlayerName(plrName);
                if(ci.id > 0){
                    ci.deaths += operation;
                    ci.flush();
                }
            }
        }
    }
}
