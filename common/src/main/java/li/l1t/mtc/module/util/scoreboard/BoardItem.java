/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.util.scoreboard;

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
     *
     * @return the display name of this item for given player
     */
    String getDisplayName(Player player);

    /**
     * @param player the player to display to
     *
     * @return the current value of this item for given player
     */
    String getValue(Player player);

    /**
     * @param player the player to operate on
     *
     * @return whether this item should be shown to given player
     */
    boolean isVisibleTo(Player player);
}
