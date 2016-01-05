/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.util;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.base.Preconditions;
import io.github.xxyy.mtc.lib.packet.WrapperPlayServerScoreboardDisplayObjective;
import io.github.xxyy.mtc.lib.packet.WrapperPlayServerScoreboardObjective;
import io.github.xxyy.mtc.lib.packet.WrapperPlayServerScoreboardScore;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;

import java.lang.reflect.InvocationTargetException;

/**
 * <p>Utility class for simulating Scoreboards at packet level. Note that this requires ProtocolLib whose presence should
 * be checked using {@link io.github.xxyy.mtc.hook.ProtocolLibHook} before instantiating this class.</p>
 * <p>This uses the player's current Scoreboard or the server's main Scoreboard, if the player has a null Scoreboard.</p>
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-05
 */
public class ScoreboardHelper {
    private final Plugin plugin;
    private final ProtocolManager protocolManager;

    /**
     * Constructs a new Scoreboard helper.
     *
     * @param plugin the plugin associated with this helper
     */
    public ScoreboardHelper(Plugin plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    /**
     * Removes an Objective. Note that this logs a NPE on the client if it doesn't know about
     * that Objective yet. No other behaviour has been observed for that case.
     *
     * @param plr           the player to send to
     * @param objectiveName the objective to forget about, limit is 40 characters
     */
    public void removeObjective(Player plr, String objectiveName) {
        Preconditions.checkNotNull(objectiveName, "objectiveName");
        Preconditions.checkArgument(objectiveName.length() <= 40, "objectiveName may not be longer than 40 characters: %s", objectiveName);
        checkScoreboard(plr);

        WrapperPlayServerScoreboardObjective packet = new WrapperPlayServerScoreboardObjective();
        packet.setName(objectiveName);
        packet.setMode(WrapperPlayServerScoreboardObjective.Mode.REMOVE_OBJECTIVE);
        try {
            protocolManager.sendServerPacket(plr, packet.getHandle());
        } catch (InvocationTargetException e) {
            e.printStackTrace(); //idk
        }
    }

    /**
     * Creates an integer based Objective. Note that this disconnects the client if it already has an Objective with
     * that name.
     *
     * @param plr           the player to send to
     * @param objectiveName the objective to create, limit is 40 characters
     * @param title         the display name of the objective, limit is 40 characters
     * @param displaySlot   the display slot for the objective
     */
    public void createIntObjective(Player plr, String objectiveName, String title, DisplaySlot displaySlot) {
        createObjective(plr, objectiveName, title, displaySlot, "INTEGER");
    }

    /**
     * Creates a hearts based Objective. Note that this disconnects the client if it already has an Objective with
     * that name. Such an Objective shows the current amount of health (not hearts, actually) a player has.
     *
     * @param plr           the player to send to
     * @param objectiveName the objective to create, limit is 40 characters
     * @param title         the display name of the objective, limit is 40 characters
     * @param displaySlot   the display slot for the objective
     */
    public void createHeartsObjective(Player plr, String objectiveName, String title, DisplaySlot displaySlot) {
        createObjective(plr, objectiveName, title, displaySlot, "HEARTS");
    }

    /**
     * Adds a score to the Scoreboard. If the score already exists, it is updated. If the objective does not exist,
     * the client is disconnected.
     *
     * @param plr           the player to send to
     * @param objectiveName the objective the score belongs to, limit is 40 characters
     * @param scoreName     the (display) name of the score, limit is 40 characters
     * @param value         the integer value of the score
     */
    public void updateScore(Player plr, String objectiveName, String scoreName, int value) {
        sendUpdateScore(plr, objectiveName, scoreName, value, EnumWrappers.ScoreboardAction.CHANGE);
    }

    /**
     * Removes a score from the Scoreboard. If the objective does not exist, the client is disconnected.
     *
     * @param plr           the player to send to
     * @param objectiveName the objective the score belongs to, limit is 40 characters
     * @param scoreName     the (display) name of the score, limit is 40 characters
     */
    public void removeScore(Player plr, String objectiveName, String scoreName) {
        sendUpdateScore(plr, objectiveName, scoreName, 0, EnumWrappers.ScoreboardAction.REMOVE);
    }

    private void sendUpdateScore(Player plr, String objectiveName, String scoreName, int value,
                                 EnumWrappers.ScoreboardAction action) {
        Preconditions.checkNotNull(objectiveName, "objectiveName");
        Preconditions.checkNotNull(scoreName, "scoreName");
        Preconditions.checkArgument(objectiveName.length() <= 40, "objectiveName may not be longer than 40 characters: %s", objectiveName);
        Preconditions.checkArgument(scoreName.length() <= 40, "scoreName may not be longer than 40 characters: %s", scoreName);
        checkScoreboard(plr);

        WrapperPlayServerScoreboardScore packet = new WrapperPlayServerScoreboardScore();
        packet.setObjectiveName(objectiveName);
        packet.setScoreboardAction(action);
        packet.setScoreName(scoreName);
        if (action != EnumWrappers.ScoreboardAction.REMOVE) {
            packet.setValue(value);
        }
        try {
            protocolManager.sendServerPacket(plr, packet.getHandle());
        } catch (InvocationTargetException e) {
            e.printStackTrace(); //idk
        }
    }

    private void createObjective(Player plr, String objectiveName, String title, DisplaySlot displaySlot,
                                 String healthDisplay) {
        Preconditions.checkNotNull(objectiveName, "objectiveName");
        Preconditions.checkNotNull(title, "title");
        Preconditions.checkNotNull(displaySlot, "displaySlot");
        Preconditions.checkArgument(objectiveName.length() <= 40, "objectiveName may not be longer than 40 characters: %s", objectiveName);
        Preconditions.checkArgument(title.length() <= 40, "title may not be longer than 40 characters: %s", title);
        checkScoreboard(plr);

        WrapperPlayServerScoreboardObjective createPacket = new WrapperPlayServerScoreboardObjective();
        createPacket.setName(objectiveName);
        createPacket.setMode(WrapperPlayServerScoreboardObjective.Mode.ADD_OBJECTIVE);
        createPacket.setDisplayName(title);
        createPacket.setHealthDisplay(healthDisplay);

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

    private void checkScoreboard(Player plr) {
        Preconditions.checkNotNull(plr, "plr");
        if (plr.getScoreboard() == null) {
            plr.setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());
        }
    }
}
