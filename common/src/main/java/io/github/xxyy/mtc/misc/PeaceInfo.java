/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.misc;

import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.MTC;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class PeaceInfo {
    public static final ConcurrentHashMap<String, PeaceInfo> CACHE = new ConcurrentHashMap<>();

    public String plrName;
    public List<String> peacedPlrs = null;
    public int errCode = 1; //no error

    private PeaceInfo(int errCode, String plrName) {
        this.errCode = errCode;
        this.plrName = plrName;
        this.peacedPlrs = new ArrayList<>();
    }

    private PeaceInfo(String plrName, List<String> peacedPlrs) {
        this.plrName = plrName;
        this.peacedPlrs = peacedPlrs;
    }

    private PeaceInfo(String plrName, String peacedPlrs) {
        this(plrName, new ArrayList<>(Arrays.asList(peacedPlrs.split(",")))); //returned list is fixed-size
    }

    public void create() {
        MTC.instance().ssql.safelyExecuteUpdate("INSERT INTO mtc_peace SET player_name=?, peace_players=?",
                this.plrName, CommandHelper.CSCollection(this.peacedPlrs, ""));
        this.errCode = 2;
        PeaceInfo.CACHE.put(this.plrName, this);
    }

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
        PeaceInfo.CACHE.put(this.plrName, this);
    }

    public void nullify() {
        MTC.instance().getSql().safelyExecuteUpdate("DELETE FROM mtc_peace WHERE player_name=?", this.plrName);
        PeaceInfo.CACHE.remove(this.plrName);
    }

    public static PeaceInfo get(String plrName) {
        if (PeaceInfo.CACHE.containsKey(plrName)) {
            return PeaceInfo.CACHE.get(plrName);
        }
        PeaceInfo rtrn = PeaceInfo.fetch(plrName);
        PeaceInfo.CACHE.put(plrName, rtrn);
        return rtrn;
    }

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

    public static boolean isInPeaceWith(String checkName, String targetName) {
        PeaceInfo pi = PeaceInfo.get(checkName);
        return pi.errCode >= 0 && pi.peacedPlrs.contains(targetName);
    }

    public static void revokeRequest(String senderName, String targetName) {
        MTC.instance().getSql().safelyExecuteUpdate("DELETE FROM mtc_peace_requests WHERE " +
                "sender_name=? AND receiver_name=?", senderName, targetName);
    }

    public static void sendRequest(String senderName, String targetName) {
        MTC.instance().getSql().safelyExecuteUpdate("INSERT INTO mtc_peace_requests SET " +
                "sender_name=?, receiver_name=?", senderName, targetName);
    }

    private static PeaceInfo fetch(String plrName) {
        SafeSql sql = MTC.instance().getSql();
        ResultSet rs = sql.safelyExecuteQuery("SELECT peace_players FROM mtc_peace WHERE player_name=?", plrName);
        if (rs == null) {
            return new PeaceInfo(-2, plrName);
        }
        try {
            if (!rs.next()) {
                return new PeaceInfo(-4, plrName);
            }
            return new PeaceInfo(plrName, rs.getString("peace_players"));
        } catch (SQLException e) {
            e.printStackTrace();
            return new PeaceInfo(-3, plrName);
        }
    }
}
