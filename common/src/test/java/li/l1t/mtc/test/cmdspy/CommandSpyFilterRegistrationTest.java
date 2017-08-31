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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import li.l1t.common.test.util.MockHelper;
import li.l1t.common.test.util.mokkit.MockServer;
import li.l1t.mtc.chat.cmdspy.CommandSpyFilter;
import li.l1t.mtc.chat.cmdspy.CommandSpyFilters;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the registration of CommandSpy filters.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2.7.14
 */
@SuppressWarnings("unchecked")
//Mockito being weeeeeird with Server#getOnlinePlayers()Ljava.util.Collection; and generics
@PrepareForTest(Bukkit.class)
public class CommandSpyFilterRegistrationTest {
    private MockServer server = MockHelper.mockServer();
    private UUID targetId = UUID.randomUUID();
    private UUID offlineId = UUID.randomUUID();
    private Collection<Player> playersWithTarget = ImmutableList.of(MockHelper.mockPlayer(targetId, "test"));

    @BeforeClass
    public static void init() {
        MockHelper.mockServer();
    }

    @Test
    public void removeDeadFiltersTest1() {
        CommandSpyFilter filter = mock(CommandSpyFilter.class);
        server.setOnlinePlayers(Collections.emptyList());
        when(filter.getSubscribers()).thenReturn(Lists.newArrayList());
        when(filter.canSubscribe()).thenReturn(true);

        CommandSpyFilters.registerFilter(filter);
        Assert.assertTrue("Filter registration failed!", CommandSpyFilters.getActiveFilters().contains(filter));

        CommandSpyFilters.removeDeadFilters();
        Assert.assertFalse("Dead filter illegally persisted!", CommandSpyFilters.getActiveFilters().contains(filter));
    }

    @Test
    public void removeDeadFiltersTest2() {
        CommandSpyFilter filter = new MockCommandSpyFilter(Lists.newArrayList(targetId, offlineId), true);
        server.setOnlinePlayers(playersWithTarget);

        CommandSpyFilters.registerFilter(filter);
        Assert.assertTrue("Filter registration failed!", CommandSpyFilters.getActiveFilters().contains(filter));

        CommandSpyFilters.removeDeadFilters();
        Assert.assertTrue("Non-empty filter illegally removed!", CommandSpyFilters.getActiveFilters().contains(filter));

        server.setOnlinePlayers(Collections.emptyList());
        CommandSpyFilters.removeDeadFilters();
        Assert.assertFalse("Dead filter illegally persisted!", CommandSpyFilters.getActiveFilters().contains(filter));
    }

    @Test
    public void removeDeadFiltersTest3() {
        CommandSpyFilter filter = new MockCommandSpyFilter(Sets.newHashSet(), false);
        server.setOnlinePlayers(Collections.emptyList());

        CommandSpyFilters.registerFilter(filter);
        Assert.assertTrue("Filter registration failed!", CommandSpyFilters.getActiveFilters().contains(filter));

        CommandSpyFilters.removeDeadFilters();
        Assert.assertTrue("Permanent filter (aka. canSubscribe() -> false) illegally removed!",
                CommandSpyFilters.getActiveFilters().contains(filter));
    }

    @Test
    public void removeOfflineSubscribersTest() {
        CommandSpyFilter filter = new MockCommandSpyFilter(Lists.newArrayList(targetId, offlineId), true);
        server.setOnlinePlayers(playersWithTarget);

        CommandSpyFilters.registerFilter(filter);
        Assert.assertTrue("Filter registration failed!", CommandSpyFilters.getActiveFilters().contains(filter));

        CommandSpyFilters.removeOfflineSubscribers(filter);
        Assert.assertTrue("Online UUID illegally removed!", filter.getSubscribers().contains(targetId));
        Assert.assertFalse("Offline UUID illegally persisted!", filter.getSubscribers().contains(offlineId));
    }

    @Test
    public void getSubscribedFiltersTest() {
        Collection<UUID> subscribersList = Lists.newArrayList(targetId, offlineId); //Need this so that writes persist and tests pass
        CommandSpyFilter filter = new MockCommandSpyFilter(subscribersList, true);
        server.setOnlinePlayers(playersWithTarget);

        CommandSpyFilters.registerFilter(filter);
        Assert.assertTrue("Filter registration failed!", CommandSpyFilters.getActiveFilters().contains(filter));

        CommandSpyFilters.removeOfflineSubscribers(filter);
        Assert.assertTrue("Online UUID illegally removed!", filter.getSubscribers().contains(targetId));
        Assert.assertFalse("Offline UUID illegally persisted!", filter.getSubscribers().contains(offlineId));

        CommandSpyFilters.unsubscribeFromAll(targetId);
        Assert.assertFalse("Unsubscription failed!", filter.getSubscribers().contains(targetId));
    }

    @Test
    public void unsubscribeFromAllTest() {
        CommandSpyFilter filter = new MockCommandSpyFilter(Lists.newArrayList(targetId), true);
        server.setOnlinePlayers(playersWithTarget);

        CommandSpyFilters.registerFilter(filter);
        Assert.assertTrue("Filter registration failed!", CommandSpyFilters.getActiveFilters().contains(filter));

        CommandSpyFilters.unsubscribeFromAll(targetId);
        Assert.assertFalse("Unsubscription failed!", filter.getSubscribers().contains(targetId));
    }
}
