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

package li.l1t.mtc.module.scoreboard;

import org.bukkit.entity.Player;

/**
 * Represents a single item in the common score board sidebar.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-10
 */
public interface BoardItem {
    /**
     * @return the internal, static identifier of this board item
     */
    String getIdentifier();

    /**
     * @param player the player to display to
     * @return the display name of this item for given player
     */
    String getDisplayName(Player player);

    /**
     * @param player the player to display to
     * @return the current value of this item for given player
     */
    String getValue(Player player);

    /**
     * @param player the player to operate on
     * @return whether this item should be shown to given player
     */
    boolean isVisibleTo(Player player);

    /**
     * Cleans up any data this item may have stored for given player.
     *
     * @param player the player whose data to discard
     */
    void cleanUp(Player player);
}
