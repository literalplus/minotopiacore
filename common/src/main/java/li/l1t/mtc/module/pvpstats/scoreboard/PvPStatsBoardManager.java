/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.pvpstats.scoreboard;

import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.pvpstats.model.PlayerStats;
import li.l1t.mtc.module.pvpstats.model.PlayerStatsRepository;
import li.l1t.mtc.module.scoreboard.CommonScoreboardProvider;
import li.l1t.mtc.module.scoreboard.MapBoardItem;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Manages Scoreboards shown by the PvP Stats module, handling displaying and updating.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-04
 */
public class PvPStatsBoardManager {
    private static final NumberFormat KD_FORMAT = new DecimalFormat("#,##");
    private final CommonScoreboardProvider scoreboardProvider;
    private final PlayerStatsRepository statsRepository;
    private final MapBoardItem killsItem = new MapBoardItem("pstats-kills", "§6Kills:");
    private final MapBoardItem deathsItem = new MapBoardItem("pstats-kills", "§6Deaths:");
    private final MapBoardItem kdRatioItem = new MapBoardItem("pstats-kills", "§6K/D:");

    @InjectMe
    public PvPStatsBoardManager(CommonScoreboardProvider scoreboardProvider, PlayerStatsRepository statsRepository) {
        this.scoreboardProvider = scoreboardProvider;
        this.statsRepository = statsRepository;
    }

    public void enable() {
        scoreboardProvider.registerBoardItem(killsItem);
        scoreboardProvider.registerBoardItem(deathsItem);
        scoreboardProvider.registerBoardItem(kdRatioItem);
    }

    public void updateAll(Player plr) {
        updateAll(plr, statsRepository.find(plr)); //no worries, it's cached
    }

    public void updateAll(Player player, PlayerStats data) {
        killsItem.setValue(player, data.getKills());
        deathsItem.setValue(player, data.getDeaths());
        kdRatioItem.setValue(player, KD_FORMAT.format(data.getKDRatio()));
        scoreboardProvider.updateScoreboardFor(player);
    }
}
