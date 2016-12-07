/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk.repository;

import li.l1t.common.sql.sane.AbstractSqlConnected;
import li.l1t.common.sql.sane.SaneSql;

import java.time.Instant;
import java.util.UUID;

/**
 * Makes perks available, either temporarily or permanently, via a JDBC data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
class JdbcAvailablePerksWriter extends AbstractSqlConnected {
    JdbcAvailablePerksWriter(SaneSql saneSql) {
        super(saneSql);
    }

    public void makeAvailablePermanently(UUID playerId, UUID perkId) {
        insertNewRow(playerId, perkId, null);
    }

    public void makeAvailableUntil(UUID playerId, UUID perkId, Instant expiryInstant) {
        insertNewRow(playerId, perkId, expiryInstant);
    }

    private int insertNewRow(UUID playerId, UUID perkId, Instant expiryInstant) {
        return sql().updateRaw(buildInsert(), perkId.toString(), playerId.toString(), expiryInstant, expiryInstant);
    }

    private String buildInsert() {
        return "INSERT INTO " + SqlPerkRepository.AVAILABLE_TABLE_NAME + " " +
                "SET product_id=?, player_id=?, validuntil=? " +
                "ON DUPLICATE KEY UPDATE validuntil=?";
    }
}
