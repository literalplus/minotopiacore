/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.chat.cmdspy;

import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.BiPredicate;

/**
 * A CommandSpyFilter impl that allows for multiple subscribers and keeps them internally as a set of their UUIDs.
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
    public void notifyOnMatch(String command, Player sender) {
        if (matches(command, sender)) {
            notifySubscribers(command, sender);
        }
    }

    @Override
    public void addSubscriber(Player newSubscriber) {
        getSubscribers().add(newSubscriber.getUniqueId());
        subscriberPlayers.put(newSubscriber, null);
    }

    @Override
    public boolean removeSubscriber(UUID uuid) {
        if(getSubscribers().remove(uuid)) {
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
