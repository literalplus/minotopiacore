/*
 * Copyright (c) 2013-2014.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.api;

import org.bukkit.plugin.Plugin;

import java.util.UUID;

/**
 * <p>
 *     Manages disabling certain PvP checks for players which are in minigames.
 * </p><p>
 *     Get an instance using Bukkit's Services API:
 *     {@code getServer().getServicesManager().load(PlayerGameManager.class);}
 * </p>
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 21.8.14
 */
public interface PlayerGameManager {
    /**
     * Sets the in-game state for a player.
     *
     * @param inGame the in game state.
     * @param uuid   the uuid of the target player
     * @param plugin the plugin causing this state-change
     * @throws java.lang.IllegalStateException if the player is already registered by another plugin
     */
    void setInGame(boolean inGame, UUID uuid, Plugin plugin) throws IllegalStateException;

    /**
     * Gets the plugin which holds given player in game.
     *
     * @param uuid the uuid of the target player
     * @return the plugin holding given player or NULL if given player is not in a game
     */
    Plugin getProvidingPlugin(UUID uuid);

    /**
     * Checks whether a given player is registered to be in any game.
     *
     * @param uuid the uuid of the target player
     * @return whether a plugin has currently registered given player as in game
     */
    default boolean isInGame(UUID uuid) {
        return getProvidingPlugin(uuid) != null;
    }
}
