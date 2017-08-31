/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
