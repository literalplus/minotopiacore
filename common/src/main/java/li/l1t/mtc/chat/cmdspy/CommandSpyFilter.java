/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.chat.cmdspy;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

/**
 * Filters commands and send them to registered command spies.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 19.6.14
 */
public interface CommandSpyFilter {
    /**
     * Checks if this filter matches a given command
     *
     * @param command Command to be matched, without preceding slash.
     * @param sender  Who executed the command
     * @return Whether this filter matches the given arguments.
     */
    boolean matches(String command, Player sender);

    default boolean canSubscribe() {
        return true;
    }

    /**
     * Notifies this filter's subscribers if this filter matches given arguments.
     *
     * @param command Command to be matched, without preceding slash.
     * @param sender  Who executed that command
     */
    void notifyOnMatch(String command, Player sender);

    /**
     * @return A modifiable Collection of this filter's subscribers
     */ //REFACTOR: Exposing internal representation? Is this bad maybe?
    Collection<UUID> getSubscribers();

    void addSubscriber(Player newSubscriber);

    /**
     * Attempts to remove a subscriber by their unique id.
     *
     * @param uuid the unique id of the subscriber to remove
     * @return if a subscriber has been removed
     */
    boolean removeSubscriber(UUID uuid);

    default String niceRepresentation() {
        return getClass().getSimpleName();
    }
}
