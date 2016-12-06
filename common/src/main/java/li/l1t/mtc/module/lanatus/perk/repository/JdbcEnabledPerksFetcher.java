/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk.repository;

import com.google.common.collect.ImmutableList;
import li.l1t.common.exception.DatabaseException;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.common.sql.sane.result.QueryResult;
import li.l1t.lanatus.sql.common.AbstractJdbcEntityCreator;
import li.l1t.lanatus.sql.common.AbstractJdbcFetcher;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

/**
 * Fetches enabled perks by player.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
class JdbcEnabledPerksFetcher extends AbstractJdbcFetcher<UUID> {
    JdbcEnabledPerksFetcher(SaneSql saneSql) {
        super(new UUIDEntityCreator(), saneSql);
    }

    public Collection<UUID> getEnabledPerksByPlayerId(UUID playerId) {
        ImmutableList.Builder<UUID> resultBuilder = ImmutableList.builder();
        try(QueryResult qr = sql().query(buildSelect("WHERE player_id = ?"), playerId.toString())) {
            while(qr.rs().next()) {
                resultBuilder.add(creator.createFromCurrentRow(qr.rs()));
            }
        } catch (SQLException e) {
            throw DatabaseException.wrap(e);
        }
        return resultBuilder.build();
    }

    @Override
    protected String buildSelect(String whereClause) {
        return "SELECT product_id FROM " + PerkRepository.ENABLED_TABLE_NAME + " "+whereClause;
    }

    private static class UUIDEntityCreator extends AbstractJdbcEntityCreator<UUID> {
        @Override
        public UUID createFromCurrentRow(ResultSet rs) throws SQLException {
            return uuid(rs, "product_id");
        }
    }
}
