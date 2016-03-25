/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.pvpstats;

import io.github.xxyy.mtc.ConfigHelper;
import io.github.xxyy.mtc.clan.ClanHelper;
import io.github.xxyy.mtc.clan.ClanInfo;
import io.github.xxyy.mtc.module.pvpstats.model.PlayerStats;
import org.bukkit.entity.Player;
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
class StatsDeathListener implements Listener {
    private final PvPStatsModule module;

    StatsDeathListener(PvPStatsModule module) {
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStatsDeath(PlayerDeathEvent evt) {
        Player victim = evt.getEntity();
        module.getPlugin().getServer().getScheduler().runTaskAsynchronously(module.getPlugin(), () -> {
            PlayerStats victimStats = module.getRepository().find(victim);
            victimStats.addDeaths(1);
            Player killer = victim.getKiller();
            module.getRepository().save(victimStats); //Only adds to queue

            if (killer != null) {
                PlayerStats killerStats = module.getRepository().find(killer);
                killerStats.addKills(1);
                module.getRepository().save(killerStats); //Only adds to queue

                if (module.isFeatureEnabled("title.killer")) {
                    module.getTitleManagerHook().sendTitle(killer, "", "§6Du hast §e" + killerStats.getKills() + "§6 Kills!");
                }
            }

            if (ConfigHelper.isClanEnabled()) {
                module.getPlugin().getServer().getScheduler().runTask(module.getPlugin(), () -> {
                    incrementClanDeaths(victim);
                    if (killer != null) {
                        incrementClanKills(killer);
                    }
                });
            }

            if (module.isFeatureEnabled("title.victim")) {
                module.getTitleManagerHook().sendTitle(victim, "", "§6Du hast §e" + victimStats.getDeaths() + "§6 Deaths!");
            }
        });
    }

    private void incrementClanKills(Player plr) {
        ClanInfo ci = ClanHelper.getClanInfoByPlayerName(plr.getName());
        if (ci.id > 0) {
            ci.kills += 1;
            ci.flush();
        }
    }

    private void incrementClanDeaths(Player plr) {
        ClanInfo ci = ClanHelper.getClanInfoByPlayerName(plr.getName());
        if (ci.id > 0) {
            ci.deaths += 1;
            ci.flush();
        }
    }
}
