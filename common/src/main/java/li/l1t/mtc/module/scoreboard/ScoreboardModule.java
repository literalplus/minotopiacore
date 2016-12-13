/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.scoreboard;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.MTCModuleAdapter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Registers listeners for the scorebaord provider API.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-14
 */
public class ScoreboardModule extends MTCModuleAdapter {
    @InjectMe
    private CommonScoreboardProvider scoreboardProvider;

    public ScoreboardModule() {
        super("ScoreboardAPI", true);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(),
                () -> getPlugin().getServer().getOnlinePlayers()
                        .forEach(scoreboardProvider::updateScoreboardFor)
        );
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
