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

import li.l1t.common.sql.SafeSql;
import li.l1t.common.util.task.ImprovedBukkitRunnable;
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

    public synchronized void queueSave(PlayerStats playerStats) {
        if (!saveQueue.contains(playerStats)) {
            saveQueue.add(playerStats);
        }
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
        if (saveQueue.size() <= MAX_WRITES_PER_EXECUTION) {
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
