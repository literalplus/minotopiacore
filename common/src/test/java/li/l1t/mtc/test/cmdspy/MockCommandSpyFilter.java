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
