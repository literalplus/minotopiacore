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

package li.l1t.mtc.module.pvpstats;

import li.l1t.mtc.ConfigHelper;
import li.l1t.mtc.clan.ClanHelper;
import li.l1t.mtc.clan.ClanInfo;
import li.l1t.mtc.module.pvpstats.model.PlayerStats;
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
    private final PlayerStatsModule module;

    StatsDeathListener(PlayerStatsModule module) {
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
