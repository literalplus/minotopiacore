/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.peace;

import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.MTC;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @deprecated breaks so many code style rulez
 */
@Deprecated
public class LegacyPeaceInfo {
    @Deprecated
    public static final ConcurrentHashMap<String, LegacyPeaceInfo> CACHE = new ConcurrentHashMap<>();

    @Deprecated
    public String plrName;
    @Deprecated
    public List<String> peacedPlrs = null;
    @Deprecated
    public int errCode = 1; //no error

    @Deprecated
    private LegacyPeaceInfo(int errCode, String plrName) {
        this.errCode = errCode;
        this.plrName = plrName;
        this.peacedPlrs = new ArrayList<>();
    }

    @Deprecated
    private LegacyPeaceInfo(String plrName, List<String> peacedPlrs) {
        this.plrName = plrName;
        this.peacedPlrs = peacedPlrs;
    }

    @Deprecated
    private LegacyPeaceInfo(String plrName, String peacedPlrs) {
        this(plrName, new ArrayList<>(Arrays.asList(peacedPlrs.split(",")))); //returned list is fixed-size
    }

    @Deprecated
    public void create() {
        MTC.instance().ssql.safelyExecuteUpdate("INSERT INTO mtc_peace SET player_name=?, peace_players=?",
                this.plrName, CommandHelper.CSCollection(this.peacedPlrs, ""));
        this.errCode = 2;
        LegacyPeaceInfo.CACHE.put(this.plrName, this);
    }

    @Deprecated
    public void flush() {
        if (this.peacedPlrs.isEmpty()) {
            this.nullify();
            return;
        }
        if (this.errCode < 0) {
            this.create();
            return;
        }
        MTC.instance().getSql().safelyExecuteUpdate("UPDATE mtc_peace SET peace_players=? WHERE player_name=?",
                CommandHelper.CSCollection(this.peacedPlrs, ""), this.plrName);
        LegacyPeaceInfo.CACHE.put(this.plrName, this);
    }

    @Deprecated
    public void nullify() {
        MTC.instance().getSql().safelyExecuteUpdate("DELETE FROM mtc_peace WHERE player_name=?", this.plrName);
        LegacyPeaceInfo.CACHE.remove(this.plrName);
    }

    @Deprecated
    public static LegacyPeaceInfo get(String plrName) {
        if (LegacyPeaceInfo.CACHE.containsKey(plrName)) {
            return LegacyPeaceInfo.CACHE.get(plrName);
        }
        LegacyPeaceInfo rtrn = LegacyPeaceInfo.fetch(plrName);
        LegacyPeaceInfo.CACHE.put(plrName, rtrn);
        return rtrn;
    }

    @Deprecated
    public static boolean hasRequest(String checkName, String targetName) {
        SafeSql sql = MTC.instance().getSql();
        ResultSet rs = sql.safelyExecuteQuery("SELECT EXISTS( SELECT 1 FROM mtc_peace_requests WHERE sender_name=? AND receiver_name=?)", //REFACTOR
                checkName, targetName); //REFACTOR
        try {
            return rs != null && rs.next() && rs.getBoolean(1);
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "");
            return false;
        }
    }

    @Deprecated
    public static boolean isInPeaceWith(String checkName, String targetName) {
        LegacyPeaceInfo pi = LegacyPeaceInfo.get(checkName);
        return pi.errCode >= 0 && pi.peacedPlrs.contains(targetName);
    }

    @Deprecated
    public static void revokeRequest(String senderName, String targetName) {
        MTC.instance().getSql().safelyExecuteUpdate("DELETE FROM mtc_peace_requests WHERE " +
                "sender_name=? AND receiver_name=?", senderName, targetName);
    }

    @Deprecated
    public static void sendRequest(String senderName, String targetName) {
        MTC.instance().getSql().safelyExecuteUpdate("INSERT INTO mtc_peace_requests SET " +
                "sender_name=?, receiver_name=?", senderName, targetName);
    }

    @Deprecated
    private static LegacyPeaceInfo fetch(String plrName) {
        SafeSql sql = MTC.instance().getSql();
        ResultSet rs = sql.safelyExecuteQuery("SELECT peace_players FROM mtc_peace WHERE player_name=?", plrName);
        if (rs == null) {
            return new LegacyPeaceInfo(-2, plrName);
        }
        try {
            if (!rs.next()) {
                return new LegacyPeaceInfo(-4, plrName);
            }
            return new LegacyPeaceInfo(plrName, rs.getString("peace_players"));
        } catch (SQLException e) {
            e.printStackTrace();
            return new LegacyPeaceInfo(-3, plrName);
        }
    }
}
