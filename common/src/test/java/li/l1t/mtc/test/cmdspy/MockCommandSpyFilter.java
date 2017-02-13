/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.test.cmdspy;

import li.l1t.mtc.chat.cmdspy.CommandSpyFilter;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

/**
 * Mock implementation of a commandspy filter for tests.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 05/09/15
 */
public class MockCommandSpyFilter implements CommandSpyFilter {
    private final Collection<UUID> subscribers;
    private final boolean canSubscribe;

    public MockCommandSpyFilter(Collection<UUID> subscribers, boolean canSubscribe) {
        this.subscribers = subscribers;
        this.canSubscribe = canSubscribe;
    }


    @Override
    public boolean matches(String command, Player sender) {
        return false;
    }

    @Override
    public boolean notifyOnMatch(String command, Player sender) {
        return false;
    }

    @Override
    public Collection<UUID> getSubscribers() {
        return subscribers;
    }

    @Override
    public void addSubscriber(Player newSubscriber) {
        subscribers.add(newSubscriber.getUniqueId());
    }

    @Override
    public boolean removeSubscriber(UUID uuid) {
        return subscribers.remove(uuid);
    }

    @Override
    public boolean canSubscribe() {
        return canSubscribe;
    }
}
