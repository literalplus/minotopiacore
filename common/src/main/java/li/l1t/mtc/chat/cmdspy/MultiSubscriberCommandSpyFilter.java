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

import java.text.MessageFormat;
import java.util.*;
import java.util.function.BiPredicate;

/**
 * A CommandSpyFilter impl that allows for multiple subscribers and keeps them internally as a set
 * of their UUIDs.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 19.6.14
 */
public class MultiSubscriberCommandSpyFilter extends SimpleCommandSpyFilter {
    private final Set<UUID> subscribers = new HashSet<>();
    private final Map<Player, Void> subscriberPlayers = new WeakHashMap<>(); //values not used
    private final String notificationFormat;

    public MultiSubscriberCommandSpyFilter(String notificationFormat, BiPredicate<String, Player> predicate) {
        super(predicate);
        MessageFormat.format(notificationFormat, "testPlayerName", "cmdspy"); //Throw exception immediately if pattern is invalid -> tests :)
        this.notificationFormat = notificationFormat;
    }

    public void notifySubscribers(String command, Player sender) {
        getOnlineSubscribers()
                .forEach(plr -> sendNotification(MessageFormat.format(getNotificationFormat(), sender.getName(), command), plr));
    }

    protected Collection<Player> getOnlineSubscribers() {
        return subscriberPlayers.keySet();
    }

    protected void sendNotification(String notificationMessage, Player plr) {
        plr.sendMessage(notificationMessage);
    }

    @Override
    public boolean notifyOnMatch(String command, Player sender) {
        if (matches(command, sender)) {
            notifySubscribers(command, sender);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void addSubscriber(Player newSubscriber) {
        getSubscribers().add(newSubscriber.getUniqueId());
        subscriberPlayers.put(newSubscriber, null);
    }

    @Override
    public boolean removeSubscriber(UUID uuid) {
        if (getSubscribers().remove(uuid)) {
            subscriberPlayers.keySet().removeIf(plr -> uuid.equals(plr.getUniqueId()));
            return true;
        }
        return false;
    }

    public Set<UUID> getSubscribers() {
        return subscribers;
    }

    protected String getNotificationFormat() {
        return notificationFormat;
    }
}
