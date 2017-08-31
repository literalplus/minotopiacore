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

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.TimeUnit;

/**
 * Registers listeners for the scorebaord provider API.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-14
 */
public class ScoreboardModule extends ConfigurableMTCModule implements Listener {
    private static final String UPDATE_PERIOD_PATH = "board-auto-refresh-interval-seconds";
    private long boardUpdatePeriodSeconds = TimeUnit.MINUTES.toSeconds(5);
    private final CommonScoreboardProvider scoreboardProvider;
    @InjectMe
    private BoardUpdateTask updateTask;

    @InjectMe(failSilently = true)
    public ScoreboardModule(CommonScoreboardProvider scoreboardProvider) {
        super("ScoreboardAPI", "modules/scoreboard.prov.yml", ClearCacheBehaviour.RELOAD, true);
        this.scoreboardProvider = scoreboardProvider;
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        registerCommand(inject(ScoreboardCommand.class), "scb");
        registerListener(this);
    }

    @Override
    protected void reloadImpl() {
        configuration.addDefault(UPDATE_PERIOD_PATH, boardUpdatePeriodSeconds);
        configuration.trySave();
        long newPeriod = configuration.getLong(UPDATE_PERIOD_PATH);
        if (newPeriod != boardUpdatePeriodSeconds || updateTask.getTaskId() == -1) {
            boardUpdatePeriodSeconds = newPeriod;
            updateTask.restart(boardUpdatePeriodSeconds * 20L);
        }
    }

    @Override
    public void disable(MTCPlugin plugin) {
        super.disable(plugin);
        plugin.getServer().getOnlinePlayers().forEach(scoreboardProvider::hideBoardFor);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        scoreboardProvider.cleanUp(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin,
                () -> scoreboardProvider.updateScoreboardFor(event.getPlayer()),
                10L);
    }
}
