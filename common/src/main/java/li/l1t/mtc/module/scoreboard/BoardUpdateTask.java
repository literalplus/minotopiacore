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

package li.l1t.mtc.module.scoreboard;

import li.l1t.common.util.task.ImprovedBukkitRunnable;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import org.bukkit.plugin.Plugin;

/**
 * Periodically updates the information displayed in players' scoreboards.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-14
 */
public class BoardUpdateTask extends ImprovedBukkitRunnable {
    private final Plugin plugin;
    private final CommonScoreboardProvider scoreboardProvider;

    @InjectMe
    public BoardUpdateTask(MTCPlugin plugin, CommonScoreboardProvider scoreboardProvider) {
        this.plugin = plugin;
        this.scoreboardProvider = scoreboardProvider;
    }

    public void start(long period) {
        runTaskTimerAsynchronously(plugin, 20L, period);
    }

    public void restart(long newPeriod) {
        tryCancel();
        start(newPeriod);
    }

    @Override
    public void run() {
        if(scoreboardProvider.getItems().isEmpty()) {
            return;
        }
        plugin.getServer().getOnlinePlayers()
                .forEach(scoreboardProvider::updateScoreboardFor);
    }
}
