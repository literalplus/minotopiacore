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

package li.l1t.mtc.module.chat.mute.api;

import li.l1t.mtc.hook.XLoginHook;
import org.bukkit.entity.Player;

/**
 * Manages the metadata of mutes.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-23
 */
public interface MuteManager {
    /**
     * Checks whether a player is currently muted.
     *
     * @param profile the profile representing the player
     * @return whether a mute currently exists
     */
    boolean isCurrentlyMuted(XLoginHook.Profile profile);

    /**
     * Checks whether a player is currently muted.
     *
     * @param player the player
     * @return whether a mute currently exists
     */
    boolean isCurrentlyMuted(Player player);

    /**
     * Retrieves a previously stored mute for given player. If no mute is currently recorded for
     * that player, retrieves a new mute for that player that has not yet been persisted into the
     * manager's data store.
     *
     * @param profile the profile representing the player
     * @return the mute
     */
    Mute getMuteFor(XLoginHook.Profile profile);

    /**
     * Saves a mute's metadata to this manager's data store.
     *
     * @param mute the mute to save
     */
    void saveMute(Mute mute);

    /**
     * Removes a mute from the data store.
     *
     * @param profile the profile to remove the mute of
     * @return whether the mute existed
     */
    boolean removeMute(XLoginHook.Profile profile);
}
