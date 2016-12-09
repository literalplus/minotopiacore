/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub.repository;

import li.l1t.common.sql.sane.AbstractSqlConnected;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.mtc.module.nub.api.NubProtection;

import java.util.UUID;

/**
 * Writes N.u.b. protection data to an underlying JDBC SQL data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-08
 */
class JdbcProtectionWriter extends AbstractSqlConnected {
    JdbcProtectionWriter(SaneSql saneSql) {
        super(saneSql);
    }

    public void deleteByPlayerId(UUID playerId) {
        sql().updateRaw("DELETE FROM " + SqlProtectionRepository.TABLE_NAME + " WHERE player_id = ?", playerId.toString());
    }

    public void createOrUpdate(NubProtection protection) {
        insertOrUpdateProtection(protection.getMinutesLeft(), protection.getPlayerId());
    }

    private int insertOrUpdateProtection(int minutesLeft, UUID playerId) {
        return sql().updateRaw("INSERT INTO " +
                        SqlProtectionRepository.TABLE_NAME + " " +
                        "SET player_id = ?, minutesleft = ? " +
                        "ON DUPLICATE KEY UPDATE minutesleft = ?",
                playerId.toString(), minutesLeft, minutesLeft);
    }
}
