/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.pvpstats.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import io.github.xxyy.common.shared.uuid.UUIDRepository;
import io.github.xxyy.common.sql.SpigotSql;
import io.github.xxyy.mtc.hook.XLoginHook;
import io.github.xxyy.mtc.module.pvpstats.PvPStatsModule;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * A caching wrapper for a {@link PlayerStatsRepositoryImpl}, providing a cache for find operations with a single result.
 * Cache entries expire after a set period of time and are saved if any changes have been made.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-03
 */
public class CachedPlayerStatsRepository implements PlayerStatsRepository {
    private final PlayerStatsRepository proxied;
    private final XLoginHook xLoginHook;
    private final Cache<UUID, PlayerStats> statsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .removalListener(this::onRemove)
            .build();

    public CachedPlayerStatsRepository(PlayerStatsRepository proxied, PvPStatsModule module) {
        this(proxied, module.getPlugin().getXLoginHook());
    }

    protected CachedPlayerStatsRepository(PlayerStatsRepository proxied, XLoginHook xLoginHook) {
        this.proxied = proxied;
        this.xLoginHook = xLoginHook;
    }

    @Override
    public PlayerStats findByUniqueId(UUID uuid) throws IllegalStateException {
        return findByUniqueId(uuid, null);
    }

    @Override
    public PlayerStats find(OfflinePlayer plr) throws IllegalStateException {
        return findByUniqueId(plr.getUniqueId(), plr.getName());
    }

    @Override
    public PlayerStats findByName(String name) throws IllegalStateException, UUIDRepository.UnknownKeyException {
        XLoginHook.Profile profile = xLoginHook.getBestProfile(name);
        if (profile == null) {
            throw new UUIDRepository.UnknownKeyException();
        }
        return findByUniqueId(profile.getUniqueId(), profile.getName());
    }

    @Override
    public PlayerStats findByUniqueId(UUID uuid, @Nullable String plrName) throws IllegalStateException {
        PlayerStats playerStats = statsCache.getIfPresent(uuid);
        if (playerStats == null) {
            return proxied.findByUniqueId(uuid, plrName);
        }
        return playerStats;
    }

    @Override
    public CompletableFuture<List<PlayerStats>> findTopKillers(int limit) {
        return proxied.findTopKillers(limit); //makes little sense to cache this
    }

    @Override
    public CompletableFuture<List<PlayerStats>> findWhoDiedMost(int limit) {
        return proxied.findWhoDiedMost(limit); //makes little sense to cache this
    }

    @Override
    public int getDeathsRank(PlayerStats playerStats) {
        return proxied.getDeathsRank(playerStats);
    }

    @Override
    public int getKillsRank(PlayerStats playerStats) {
        return proxied.getKillsRank(playerStats);
    }

    @Override
    public void save(PlayerStats playerStats) {
        proxied.save(playerStats);
    }

    @Override
    public void setDatabaseTable(String database, String table) {
        proxied.setDatabaseTable(database, table);
    }

    @Override
    public void cleanup() {
        statsCache.asMap().values().forEach(ps -> {
            if (ps != null && ps.isDirty()) {
                save(ps);
            }
        });
        statsCache.asMap().clear();
        proxied.cleanup();
    }

    @Override
    public String getDatabaseTable() {
        return proxied.getDatabaseTable();
    }

    @Override
    public SpigotSql getSql() {
        return proxied.getSql();
    }

    private void onRemove(RemovalNotification<UUID, PlayerStats> notification) {
        PlayerStats playerStats = notification.getValue();
        if (playerStats != null && playerStats.isDirty()) {
            save(playerStats);
        }
    }
}
