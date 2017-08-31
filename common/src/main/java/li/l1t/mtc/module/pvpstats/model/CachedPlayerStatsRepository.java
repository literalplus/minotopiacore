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

package li.l1t.mtc.module.pvpstats.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import li.l1t.common.shared.uuid.UUIDRepository;
import li.l1t.common.sql.SpigotSql;
import li.l1t.mtc.hook.XLoginHook;
import li.l1t.mtc.module.pvpstats.PlayerStatsModule;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * A caching wrapper for a {@link PlayerStatsRepositoryImpl}, providing a cache for find operations
 * with a single result. Cache entries expire after a set period of time and are saved if any
 * changes have been made.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-03
 */
public class CachedPlayerStatsRepository implements PlayerStatsRepository {
    private final PlayerStatsRepository proxied;
    private final XLoginHook xLoginHook;
    private final Cache<UUID, PlayerStats> statsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .removalListener(this::onRemove)
            .build();

    public CachedPlayerStatsRepository(PlayerStatsRepository proxied, PlayerStatsModule module) {
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
            playerStats = proxied.findByUniqueId(uuid, plrName);
        }
        statsCache.put(uuid, playerStats);
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
