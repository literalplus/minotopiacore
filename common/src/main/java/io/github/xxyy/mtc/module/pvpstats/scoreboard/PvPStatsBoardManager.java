/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.pvpstats.scoreboard;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.module.pvpstats.PvPStatsModule;
import io.github.xxyy.mtc.module.pvpstats.model.PlayerStats;
import io.github.xxyy.mtc.util.ScoreboardHelper;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages Scoreboards shown by the PvP Stats module, handling displaying and updating.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-04
 */
public class PvPStatsBoardManager extends ScoreboardHelper {
    private static final NumberFormat KD_FORMAT = new DecimalFormat("#,##");
    private final Set<UUID> objectiveExistingPlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public static final String OBJECTIVE_NAME = "pvpstats-side";
    private final PvPStatsModule module;

    public PvPStatsBoardManager(PvPStatsModule module) {
        super(module.getPlugin());
        this.module = module;
    }

    public void enable() {
        module.getPlugin().getServer().getPluginManager()
                .registerEvents(new PvPStatsBoardListener(this), module.getPlugin());
        module.getPlugin().getServer().getScheduler().runTaskAsynchronously(
                module.getPlugin(),
                () -> new ArrayList<>(module.getPlugin().getServer().getOnlinePlayers())
                        .forEach(this::updateScoreboard)
        );
    }

    public void cleanUp(Player plr) {
        objectiveExistingPlayers.remove(plr.getUniqueId());
    }

    public void updateScoreboard(Player plr) {
        updateScoreboard(plr, module.getRepository().find(plr)); //no worries, it's cached
    }

    public void updateScoreboard(Player plr, PlayerStats data) {
        prepareData(plr);
        updateScore(plr, data);
    }

    private void updateScore(Player plr, PlayerStats data) {
        updateScore(plr, OBJECTIVE_NAME, "§9Kills:", 6);
        updateScore(plr, OBJECTIVE_NAME, "§0§e" + data.getKills(), 5); //color codes so that equal values don't get overridden
        updateScore(plr, OBJECTIVE_NAME, "§9Deaths:", 4);
        updateScore(plr, OBJECTIVE_NAME, "§1§e" + data.getDeaths(), 3);
        updateScore(plr, OBJECTIVE_NAME, "§9K/D:", 2);
        updateScore(plr, OBJECTIVE_NAME, "§2§e" + KD_FORMAT.format(data.getKDRatio()), 1);
    }

    private void prepareData(Player plr) {
        if (!objectiveExistingPlayers.add(plr.getUniqueId())) { //may produce a client NPE on reload - Mojang logs & discards that
            removeObjective(plr, OBJECTIVE_NAME);
        }

        if (plr.getScoreboard() == null) {
            plr.setScoreboard(module.getPlugin().getServer().getScoreboardManager().getMainScoreboard());
        }

        createIntObjective(plr, OBJECTIVE_NAME,
                CommandHelper.sixteenCharColorize(plr.getName(), "§e"),
                DisplaySlot.SIDEBAR);
    }

    public PvPStatsModule getModule() {
        return module;
    }
}
