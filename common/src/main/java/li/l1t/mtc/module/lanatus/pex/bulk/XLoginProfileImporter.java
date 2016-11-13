/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.pex.bulk;

import li.l1t.common.sql.sane.SaneSql;
import li.l1t.mtc.hook.XLoginHook;

import java.util.UUID;

/**
 * Creates XLogin profiles for users which have their unique id known, but do not have an xLogin profile.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-13-11
 */
class XLoginProfileImporter {
    private static final String XLOGIN_TABLE_NAME = "mt_main.xlogin_data";
    private final XLoginHook xLogin;
    private final SaneSql sql;

    public XLoginProfileImporter(XLoginHook xLogin, SaneSql sql) {
        this.xLogin = xLogin;
        this.sql = sql;
    }

    public boolean isKnownToXLogin(UUID playerId) {
        return xLogin.getProfile(playerId) != null;
    }

    public void createXLoginProfile(UUID playerId, String playerName) {
        sql.updateRaw(
                "INSERT INTO " + XLOGIN_TABLE_NAME + " SET " +
                        "uuid = ?, username = ?, reg_date = NULL",
                playerId.toString(), playerName
        );
    }
}
