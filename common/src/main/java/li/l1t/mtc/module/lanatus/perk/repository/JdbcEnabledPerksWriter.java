/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk.repository;

import li.l1t.common.sql.sane.AbstractSqlConnected;
import li.l1t.common.sql.sane.SaneSql;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Enables and disables perks via a JDBC data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
class JdbcEnabledPerksWriter extends AbstractSqlConnected {
    JdbcEnabledPerksWriter(SaneSql saneSql) {
        super(saneSql);
    }

    public void enablePlayerPerk(UUID playerId, UUID perkId) {
        insertWithPlayerIdAndProductId(playerId, perkId);
    }

    private int insertWithPlayerIdAndProductId(UUID playerId, UUID perkId) {
        return sql().updateRaw(buildInsert(), perkId.toString(), playerId.toString());
    }

    private String buildInsert() {
        return "INSERT IGNORE INTO " + SqlPerkRepository.ENABLED_TABLE_NAME + " " +
                "SET product_id=?, player_id=?";
    }


    public void disablePlayerPerk(UUID playerId, UUID perkId) {
        deleteByPlayerIdAndPerkId(playerId, perkId);
    }

    private int deleteByPlayerIdAndPerkId(UUID playerId, UUID perkId) {
        return sql().updateRaw(buildDeleteByProductIdAndPlayerId(), perkId.toString(), playerId.toString());
    }

    private String buildDeleteByProductIdAndPlayerId() {
        return buildDelete("WHERE product_id=? AND player_id=?");
    }

    @NotNull
    private String buildDelete(String whereClause) {
        return "DELETE FROM " + SqlPerkRepository.ENABLED_TABLE_NAME + " " + whereClause;
    }
}
