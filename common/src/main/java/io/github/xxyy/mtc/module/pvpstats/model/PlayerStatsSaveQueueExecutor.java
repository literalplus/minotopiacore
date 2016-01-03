/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.pvpstats.model;

import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.common.util.task.ImprovedBukkitRunnable;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

/**
 * Handles batch saving of players' PvP stats.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-03
 */
class PlayerStatsSaveQueueExecutor extends ImprovedBukkitRunnable {
    private static final int MAX_WRITES_PER_EXECUTION = 100;
    private static final long EXECUTION_DELAY_TICKS = 40L;
    private static final long MAX_IDLE_EXECUTIONS = 15; //30s
    private Queue<PlayerStats> saveQueue = new ConcurrentLinkedQueue<>();
    private final SafeSql sql;
    private final Plugin plugin;
    private final PlayerStatsRepository repository;
    private int taskId = -1;
    private int idleExecutions = 0;

    public PlayerStatsSaveQueueExecutor(Plugin plugin, PlayerStatsRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
        this.sql = repository.getSql();
    }

    public void queueSave(PlayerStats playerStats) {
        saveQueue.add(playerStats);
        if (taskId == -1 && plugin.isEnabled()) {
            taskId = runTaskTimerAsynchronously(plugin, EXECUTION_DELAY_TICKS, EXECUTION_DELAY_TICKS).getTaskId();
        }
    }

    @Override
    public void run() {
        if (saveQueue.isEmpty()) {
            idleExecutions++;
            if (idleExecutions >= MAX_IDLE_EXECUTIONS) {
                tryCancel();
                taskId = -1;
            }
            return;
        }

        idleExecutions = 0;
        Collection<PlayerStats> toSave;
        if (saveQueue.size() < MAX_WRITES_PER_EXECUTION) {
            toSave = new ArrayList<>(saveQueue);
            saveQueue.clear();
        } else {
            toSave = new ArrayList<>(MAX_WRITES_PER_EXECUTION);
            for (int i = 0; i < MAX_WRITES_PER_EXECUTION; i++) {
                toSave.add(saveQueue.remove());
            }
        }

        execute(toSave);
    }

    public boolean flush() {
        boolean success = execute(new ArrayList<>(saveQueue));
        saveQueue.clear();
        return success;
    }

    private boolean execute(Collection<PlayerStats> data) {
        try {
            sql.executeBatchUpdate(
                    "INSERT INTO " + repository.getDatabaseTable() + " SET uuid=?,kills=?,deaths=? " +
                            "ON DUPLICATE KEY UPDATE kills=?,deaths=?",
                    data,
                    ps -> new Object[]{ps.getUniqueId().toString(), ps.getKills(), ps.getDeaths(),
                            ps.getKills(), ps.getDeaths()}
            );
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't save PvP Stats data:", e);
            return false;
        }
    }
}
