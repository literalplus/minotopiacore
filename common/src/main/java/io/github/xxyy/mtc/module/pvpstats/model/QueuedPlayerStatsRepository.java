/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.pvpstats.model;

import io.github.xxyy.common.shared.uuid.UUIDRepository;
import io.github.xxyy.common.sql.SpigotSql;
import io.github.xxyy.mtc.module.pvpstats.PvPStatsModule;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A proxy for a connection between the PvP Stats application model and an underlying database that queues save calls
 * and executes them asynchronously in batches, in insertion order. All read calls are forwarded to the proxied
 * repository immediately.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-03
 */
public class QueuedPlayerStatsRepository implements PlayerStatsRepository {
    private final PlayerStatsRepository proxied;
    private final PlayerStatsSaveQueueExecutor queueExecutor;

    public QueuedPlayerStatsRepository(PlayerStatsRepository proxied, PvPStatsModule module) {
        this(proxied, module.getPlugin());
    }

    protected QueuedPlayerStatsRepository(PlayerStatsRepository proxied, Plugin plugin) {
        this.proxied = proxied;
        this.queueExecutor = new PlayerStatsSaveQueueExecutor(plugin, proxied);
    }


    @Override
    public void save(PlayerStats playerStats) {
        queueExecutor.queueSave(playerStats);
    }

    @Override
    public void cleanup() {
        queueExecutor.flush();
        proxied.cleanup();
    }

    @Override
    public PlayerStats findByUniqueId(UUID uuid) throws IllegalStateException {
        return proxied.findByUniqueId(uuid);
    }

    @Override
    public PlayerStats find(OfflinePlayer plr) throws IllegalStateException {
        return proxied.find(plr);
    }

    @Override
    public PlayerStats findByName(String name) throws IllegalStateException, UUIDRepository.UnknownKeyException {
        return proxied.findByName(name);
    }

    @Override
    public PlayerStats findByUniqueId(UUID uuid, @Nullable String plrName) throws IllegalStateException {
        return proxied.findByUniqueId(uuid, plrName);
    }

    @Override
    public CompletableFuture<List<PlayerStats>> findTopKillers(int limit) {
        return proxied.findTopKillers(limit);
    }

    @Override
    public CompletableFuture<List<PlayerStats>> findWhoDiedMost(int limit) {
        return proxied.findWhoDiedMost(limit);
    }

    @Override
    public void setDatabaseTable(String database, String table) {
        proxied.setDatabaseTable(database, table);
    }

    @Override
    public SpigotSql getSql() {
        return proxied.getSql();
    }

    @Override
    public String getDatabaseTable() {
        return proxied.getDatabaseTable();
    }
}
