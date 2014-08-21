package me.minotopia.mitoscb;

import io.github.xxyy.common.sql.SqlConnectable;
import io.github.xxyy.mtc.MTC;

public class SqlConsts2 implements SqlConnectable {

    @Override
    public final String getSqlDb() {
        if (MTC.instance() == null) {
            return "";
        }
        return MTC.instance().getConfig().getString("sql2.db");
    }

    @Override
    public final String getSqlHost() {
        if (MTC.instance() == null) {
            return "jdbc:mysql://localhost:3306/";
        }
        return MTC.instance().getConfig().getString("sql2.host");
    }

    @Override
    public final String getSqlPwd() {
        if (MTC.instance() == null) {
            return "";
        }
        return MTC.instance().getConfig().getString("sql2.password");
    }

    public final String getSqlTable() {
        if (MTC.instance() == null) {
            return "";
        }
        return MTC.instance().getConfig().getString("sql2.table");
    }

    @Override
    public final String getSqlUser() {
        if (MTC.instance() == null) {
            return "";
        }
        return MTC.instance().getConfig().getString("sql2.user");
    }
}
