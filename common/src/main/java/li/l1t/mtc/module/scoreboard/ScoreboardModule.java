/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.scoreboard;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.TimeUnit;

/**
 * Registers listeners for the scorebaord provider API.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-14
 */
public class ScoreboardModule extends ConfigurableMTCModule {
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
    }

    @Override
    protected void reloadImpl() {
        configuration.addDefault(UPDATE_PERIOD_PATH, boardUpdatePeriodSeconds);
        long newPeriod = configuration.getLong(UPDATE_PERIOD_PATH);
        if(newPeriod != boardUpdatePeriodSeconds || updateTask.getTaskId() == -1) {
            boardUpdatePeriodSeconds = newPeriod;
            updateTask.restart(boardUpdatePeriodSeconds * 20L);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        scoreboardProvider.cleanUp(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        scoreboardProvider.updateScoreboardFor(event.getPlayer());
    }
}
