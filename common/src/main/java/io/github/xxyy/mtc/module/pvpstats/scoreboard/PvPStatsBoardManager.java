/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.pvpstats.scoreboard;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.EnumWrappers;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.lib.packet.WrapperPlayServerScoreboardDisplayObjective;
import io.github.xxyy.mtc.lib.packet.WrapperPlayServerScoreboardObjective;
import io.github.xxyy.mtc.lib.packet.WrapperPlayServerScoreboardScore;
import io.github.xxyy.mtc.module.pvpstats.PvPStatsModule;
import io.github.xxyy.mtc.module.pvpstats.model.PlayerStats;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.lang.reflect.InvocationTargetException;
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
public class PvPStatsBoardManager {
    private static final NumberFormat KD_FORMAT = new DecimalFormat("#,##");
    private ProtocolManager protocolManager;
    private final Set<UUID> objectiveExistingPlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public static final String OBJECTIVE_NAME = "pvpstats-side";
    private PvPStatsModule module;

    public void enable(PvPStatsModule module) {
        this.module = module;
        protocolManager = ProtocolLibrary.getProtocolManager();
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
            clearObjective(plr, OBJECTIVE_NAME);
        }

        if (plr.getScoreboard() == null) {
            plr.setScoreboard(module.getPlugin().getServer().getScoreboardManager().getMainScoreboard());
        }

        createIntObjective(plr, OBJECTIVE_NAME,
                CommandHelper.sixteenCharColorize(plr.getName(), "§e"),
                DisplaySlot.SIDEBAR);
    }

    private void clearObjective(Player plr, String objectiveName) {
        WrapperPlayServerScoreboardObjective packet = new WrapperPlayServerScoreboardObjective();
        packet.setName(objectiveName);
        packet.setMode(WrapperPlayServerScoreboardObjective.Mode.REMOVE_OBJECTIVE);
        try {
            protocolManager.sendServerPacket(plr, packet.getHandle());
        } catch (InvocationTargetException e) {
            e.printStackTrace(); //idk
        }
    }

    private void createIntObjective(Player plr, String objectiveName, String title, DisplaySlot displaySlot) {
        WrapperPlayServerScoreboardObjective createPacket = new WrapperPlayServerScoreboardObjective();
        createPacket.setName(objectiveName);
        createPacket.setMode(WrapperPlayServerScoreboardObjective.Mode.ADD_OBJECTIVE);
        createPacket.setDisplayName(title);
        createPacket.setHealthDisplay("INTEGER");

        WrapperPlayServerScoreboardDisplayObjective displayPacket = new WrapperPlayServerScoreboardDisplayObjective();
        displayPacket.setPosition(convertDisplaySlot(displaySlot));
        displayPacket.setScoreName(objectiveName);

        try {
            protocolManager.sendServerPacket(plr, createPacket.getHandle());
            protocolManager.sendServerPacket(plr, displayPacket.getHandle());
        } catch (InvocationTargetException e) {
            e.printStackTrace(); //idk
        }
    }

    private void updateScore(Player plr, String objectiveName, String scoreName, int value) {
        WrapperPlayServerScoreboardScore packet = new WrapperPlayServerScoreboardScore();
        packet.setObjectiveName(objectiveName);
        packet.setScoreboardAction(EnumWrappers.ScoreboardAction.CHANGE);
        packet.setScoreName(scoreName);
        packet.setValue(value);
        try {
            protocolManager.sendServerPacket(plr, packet.getHandle());
        } catch (InvocationTargetException e) {
            e.printStackTrace(); //idk
        }
    }

    private int convertDisplaySlot(DisplaySlot displaySlot) {
        switch (displaySlot) {
            case PLAYER_LIST:
                return 0;
            case SIDEBAR:
                return 1;
            case BELOW_NAME:
                return 2;
            default:
                throw new AssertionError(displaySlot.name());
        }
    }

    public PvPStatsModule getModule() {
        return module;
    }
}
