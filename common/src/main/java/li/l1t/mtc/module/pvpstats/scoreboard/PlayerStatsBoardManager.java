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

package li.l1t.mtc.module.pvpstats.scoreboard;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.pvpstats.model.PlayerStats;
import li.l1t.mtc.module.pvpstats.model.PlayerStatsRepository;
import li.l1t.mtc.module.scoreboard.CommonScoreboardProvider;
import li.l1t.mtc.module.scoreboard.MapBoardItem;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Manages Scoreboards shown by the PvP Stats module, handling displaying and updating.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-04
 */
public class PlayerStatsBoardManager {
    private final CommonScoreboardProvider scoreboardProvider;
    private final PlayerStatsRepository statsRepository;
    private final Plugin plugin;
    private final MapBoardItem killsItem = new MapBoardItem("pstats-kills", "§6Kills:");
    private final MapBoardItem deathsItem = new MapBoardItem("pstats-deaths", "§6Deaths:");
    private final MapBoardItem kdRatioItem = new MapBoardItem("pstats-kd", "§6K/D:");

    @InjectMe
    public PlayerStatsBoardManager(CommonScoreboardProvider scoreboardProvider, PlayerStatsRepository statsRepository, MTCPlugin plugin) {
        this.scoreboardProvider = scoreboardProvider;
        this.statsRepository = statsRepository;
        this.plugin = plugin;
    }

    public void enable() {
        scoreboardProvider.registerBoardItem(killsItem);
        scoreboardProvider.registerBoardItem(deathsItem);
        scoreboardProvider.registerBoardItem(kdRatioItem);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin,
                () -> plugin.getServer().getOnlinePlayers().forEach(this::justSetAll)
        );
    }

    public void updateAll(Player plr) {
        updateAll(plr, statsRepository.find(plr)); //no worries, it's cached
    }

    public void justSetAll(Player plr) {
        justSetAll(plr, statsRepository.find(plr)); //no worries, it's cached
    }

    public void updateAll(Player player, PlayerStats data) {
        justSetAll(player, data);
        scoreboardProvider.updateScoreboardFor(player);
    }

    private void justSetAll(Player player, PlayerStats data) {
        killsItem.setValue(player, data.getKills());
        deathsItem.setValue(player, data.getDeaths());
        kdRatioItem.setValue(player, String.format("%.2f", data.getKDRatio()));
    }
}
