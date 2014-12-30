/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.test.cmdspy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import io.github.xxyy.common.test.util.MockHelper;
import io.github.xxyy.mtc.chat.cmdspy.CommandSpyFilter;
import io.github.xxyy.mtc.chat.cmdspy.CommandSpyFilters;

import java.util.Collection;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the registration of CommandSpy filters.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2.7.14
 */
@SuppressWarnings("unchecked") //Mockito being weeeeeird with Server#getOnlinePlayers()Ljava.util.Collection; and generics
@PrepareForTest(Bukkit.class)
public class CommandSpyFilterRegistrationTest {
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
        when(Bukkit.getServer().getOnlinePlayers()).thenReturn(ImmutableList.of());
        when(filter.getSubscribers()).thenReturn(Lists.newArrayList());
        when(filter.canSubscribe()).thenReturn(true);

        CommandSpyFilters.registerFilter(filter);
        Assert.assertTrue("Filter registration failed!", CommandSpyFilters.getActiveFilters().contains(filter));

        CommandSpyFilters.removeDeadFilters();
        Assert.assertFalse("Dead filter illegally persisted!", CommandSpyFilters.getActiveFilters().contains(filter));
    }

    @Test
     public void removeDeadFiltersTest2() {
        CommandSpyFilter filter = mock(CommandSpyFilter.class);
        when((Collection<Player>) Bukkit.getServer().getOnlinePlayers()).thenReturn(playersWithTarget);
        when(filter.getSubscribers()).thenReturn(Lists.newArrayList(targetId, offlineId));
        when(filter.canSubscribe()).thenReturn(true);

        CommandSpyFilters.registerFilter(filter);
        Assert.assertTrue("Filter registration failed!", CommandSpyFilters.getActiveFilters().contains(filter));

        CommandSpyFilters.removeDeadFilters();
        Assert.assertTrue("Non-empty filter illegally removed!", CommandSpyFilters.getActiveFilters().contains(filter));

        when(Bukkit.getServer().getOnlinePlayers()).thenReturn(ImmutableList.of());
        CommandSpyFilters.removeDeadFilters();
        Assert.assertFalse("Dead filter illegally persisted!", CommandSpyFilters.getActiveFilters().contains(filter));
    }

    @Test
    public void removeDeadFiltersTest3() {
        CommandSpyFilter filter = mock(CommandSpyFilter.class);
        when(Bukkit.getServer().getOnlinePlayers()).thenReturn(ImmutableList.of());
        when(filter.getSubscribers()).thenReturn(Sets.newHashSet());
        when(filter.canSubscribe()).thenReturn(false);

        CommandSpyFilters.registerFilter(filter);
        Assert.assertTrue("Filter registration failed!", CommandSpyFilters.getActiveFilters().contains(filter));

        CommandSpyFilters.removeDeadFilters();
        Assert.assertTrue("Permanent filter (aka. canSubscribe() -> false) illegally removed!", CommandSpyFilters.getActiveFilters().contains(filter));
    }

    @Test
    public void removeOfflineSubscribersTest() {
        CommandSpyFilter filter = mock(CommandSpyFilter.class);
        when((Collection<Player>) Bukkit.getServer().getOnlinePlayers()).thenReturn(playersWithTarget);
        when(filter.getSubscribers()).thenReturn(Lists.newArrayList(targetId, offlineId));

        CommandSpyFilters.registerFilter(filter);
        Assert.assertTrue("Filter registration failed!", CommandSpyFilters.getActiveFilters().contains(filter));

        CommandSpyFilters.removeOfflineSubscribers(filter);
        Assert.assertTrue("Online UUID illegally removed!", filter.getSubscribers().contains(targetId));
        Assert.assertFalse("Offline UUID illegally persisted!", filter.getSubscribers().contains(offlineId));
    }

    @Test
    public void getSubscribedFiltersTest() {
        CommandSpyFilter filter = mock(CommandSpyFilter.class);
        when((Collection<Player>) Bukkit.getServer().getOnlinePlayers()).thenReturn(playersWithTarget);
        Collection<UUID> subscribersList = Lists.newArrayList(targetId, offlineId); //Need this so that writes persist and tests pass
        when(filter.getSubscribers()).thenReturn(subscribersList);
        when(filter.canSubscribe()).thenReturn(true);

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
        CommandSpyFilter filter = mock(CommandSpyFilter.class);
        when((Collection<Player>) Bukkit.getServer().getOnlinePlayers()).thenReturn(playersWithTarget);
        when(filter.getSubscribers()).thenReturn(Lists.newArrayList(targetId));
        when(filter.canSubscribe()).thenReturn(true);

        CommandSpyFilters.registerFilter(filter);
        Assert.assertTrue("Filter registration failed!", CommandSpyFilters.getActiveFilters().contains(filter));

        CommandSpyFilters.unsubscribeFromAll(targetId);
        Assert.assertFalse("Unsubscription failed!", filter.getSubscribers().contains(targetId));
    }
}
