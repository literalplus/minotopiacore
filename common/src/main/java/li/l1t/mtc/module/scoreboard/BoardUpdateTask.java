/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
        runTaskTimerAsynchronously(plugin, period);
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
