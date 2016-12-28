/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub.repository;

import li.l1t.common.exception.DatabaseException;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.common.sql.sane.result.QueryResult;
import li.l1t.common.sql.sane.util.AbstractJdbcFetcher;
import li.l1t.common.sql.sane.util.JdbcEntityCreator;
import li.l1t.mtc.module.nub.api.NubProtection;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/**
 * Fetches N.u.b. protections from a JDBC SQL data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-08
 */
class JdbcProtectionFetcher extends AbstractJdbcFetcher<NubProtection> {
    JdbcProtectionFetcher(JdbcEntityCreator<? extends NubProtection> creator, SaneSql saneSql) {
        super(creator, saneSql);
    }

    public Optional<NubProtection> findByPlayerId(UUID playerId) {
        try (QueryResult qr = selectByPlayerId(playerId)) {
            if (qr.rs().next()) {
                return Optional.of(creator.createFromCurrentRow(qr.rs()));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw DatabaseException.wrap(e);
        }
    }

    private QueryResult selectByPlayerId(UUID playerId) {
        return sql().query(buildSelect("WHERE player_id = ?"), playerId.toString());
    }

    @Override
    protected String buildSelect(String whereClause) {
        return "SELECT player_id, minutesleft FROM " + SqlProtectionRepository.TABLE_NAME + " " + whereClause;
    }
}
