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

package li.l1t.mtc.api;

import org.bukkit.plugin.Plugin;

import java.util.UUID;

/**
 * <p> Manages disabling certain PvP checks for players which are in minigames. </p><p> Get an
 * instance using Bukkit's Services API: {@code getServer().getServicesManager().load(PlayerGameManager.class);}
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
     * @throws java.lang.IllegalStateException if the player is already registered by another
     *                                         plugin
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
