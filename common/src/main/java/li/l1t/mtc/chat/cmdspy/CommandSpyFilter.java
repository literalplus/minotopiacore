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
     * @return whether given command was matched
     */
    boolean notifyOnMatch(String command, Player sender);

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
