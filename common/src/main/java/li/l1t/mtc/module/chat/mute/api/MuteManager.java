/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
