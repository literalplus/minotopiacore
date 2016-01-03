/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.pvpstats;

import io.github.xxyy.mtc.module.pvpstats.model.PlayerStats;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Listens for death events and passes them to the PvP Stats module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-03
 */
public class StatsDeathListener implements Listener {
    private final PvPStatsModule module;

    public StatsDeathListener(PvPStatsModule module) {
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStatsDeath(PlayerDeathEvent evt) {
        PlayerStats victimStats = module.getRepository().find(evt.getEntity());
        victimStats.addDeaths(1);
        if (evt.getEntity().getKiller() != null) {
            PlayerStats killerStats = module.getRepository().find(evt.getEntity().getKiller());
            killerStats.addKills(1);
        }
    }
}
