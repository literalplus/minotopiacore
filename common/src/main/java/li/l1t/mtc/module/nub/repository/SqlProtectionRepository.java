/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub.repository;

import com.google.common.base.Preconditions;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.nub.api.NoSuchProtectionException;
import li.l1t.mtc.module.nub.api.NubProtection;
import li.l1t.mtc.module.nub.api.ProtectionRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * A protection repository connected to a SQL data source.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-08
 */
public class SqlProtectionRepository implements ProtectionRepository {
    public static final String TABLE_NAME = "mt_main.mtc_nub_protection";
    private final JdbcProtectionFetcher fetcher;
    private final JdbcProtectionWriter writer;

    @InjectMe
    public SqlProtectionRepository(SaneSql sql) {
        this.fetcher = new JdbcProtectionFetcher(new JdbcProtectionCreator(), sql);
        this.writer = new JdbcProtectionWriter(sql);
    }

    @Override
    public Optional<NubProtection> findProtectionFor(UUID playerId) {
        Preconditions.checkNotNull(playerId, "playerId");
        return fetcher.findByPlayerId(playerId);
    }

    @Override
    public void deleteProtection(NubProtection protection) {
        Preconditions.checkNotNull(protection, "protection");
        writer.deleteByPlayerId(protection.getPlayerId());
    }

    @Override
    public void saveProtection(NubProtection protection) throws NoSuchProtectionException {
        Preconditions.checkNotNull(protection, "protection");
        if (!findProtectionFor(protection.getPlayerId()).isPresent()) {
            throw new NoSuchProtectionException(protection.getPlayerId());
        } else {
            saveIfValidOrDeleteIfExpired(protection);
        }
    }

    @Override
    public NubProtection createProtection(UUID playerId, int durationMinutes) {
        SqlProtection protection = new SqlProtection(playerId, durationMinutes);
        saveIfValidOrDeleteIfExpired(protection);
        return protection;
    }

    private void saveIfValidOrDeleteIfExpired(NubProtection protection) {
        if (protection.isExpired()) {
            deleteProtection(protection);
        } else {
            writer.createOrUpdate(protection);
        }
    }
}
