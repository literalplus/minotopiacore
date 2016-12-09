/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub.api;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A service that allows to manage N.u.b. protections, emitting messages to notify players of modifications.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-08
 */
public interface ProtectionService {
    /**
     * Protects given player for the default duration and saves that protection to the database. Also notifies the
     * player of the changes.
     *
     * @param player the player to protect
     */
    void startProtection(Player player);

    /**
     * Immediately cancels given player's protection, if any, and removes if from the database. This does not allow
     * players to resume their protection. Also notifies the player whether their protection was cancelled.
     *
     * @param player the player whose protection to cancel
     *
     * @return whether the player was protected
     */
    boolean cancelProtection(Player player);

    /**
     * Pauses the protection of given player, saving the current amount of time left to the database, so that it may be
     * resumed later. Also notifies the player of the change.
     *
     * @param player the player whose protection to pause
     */
    void pauseProtection(Player player);

    /**
     * Resumes the paused protection of given player. Also notifies them of the change.
     *
     * @param player the player whose protection to resume
     *
     * @throws NoSuchProtectionException if given player does not have a paused protection
     */
    void resumeProtection(Player player) throws NoSuchProtectionException;

    /**
     * Resumes given player's protection if they have a paused protection, or otherwise starts a protection on them.
     *
     * @param player the player to operate on
     *
     * @see #startProtection(Player)
     * @see #resumeProtection(Player)
     */
    void startOrResumeProtection(Player player);

    /**
     * @param player the player whose protection to check
     *
     * @return whether given player is currently protected locally, not querying the database
     */
    boolean hasProtection(Player player);

    /**
     * @param player the player to examine
     *
     * @return whether given player is eligible for protection either through a paused protection or through not having
     * played on the server before
     */
    boolean isEligibleForProtection(Player player);

    /**
     * Expires given protection, notifying given player of the event.
     *
     * @param player     the player to notify
     * @param protection the protection to expire
     *
     * @throws IllegalArgumentException if given protection does not belong to given player or given protection has not
     *                                  expired yet
     */
    void expireProtection(Player player, NubProtection protection);

    void showProtectionStatusTo(CommandSender sender, NubProtection protection);

    void showOwnProtectionStatusTo(Player player);
}
