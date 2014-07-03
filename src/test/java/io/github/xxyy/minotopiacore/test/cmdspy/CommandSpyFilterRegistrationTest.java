package io.github.xxyy.minotopiacore.test.cmdspy;

import com.google.common.collect.Lists;
import io.github.xxyy.minotopiacore.chat.cmdspy.CommandSpyFilter;
import io.github.xxyy.minotopiacore.chat.cmdspy.CommandSpyFilters;
import io.github.xxyy.minotopiacore.test.TestHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.Collection;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Tests the registration of CommandSpy filters.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2.7.14
 */
@PrepareForTest(Bukkit.class)
public class CommandSpyFilterRegistrationTest {
    private UUID targetId = UUID.randomUUID();
    private UUID offlineId = UUID.randomUUID();
    private Player[] playersWithTarget = new Player[]{TestHelper.mockPlayer(targetId, "test")};

    @BeforeClass
    public static void init() {
        TestHelper.mockServer();
    }

    @Test
    public void removeDeadFiltersTest1() {
        CommandSpyFilter filter = mock(CommandSpyFilter.class);
        when(Bukkit.getServer().getOnlinePlayers()).thenReturn(new Player[0]);
        when(filter.getSubscribers()).thenReturn(Lists.newArrayList());

        CommandSpyFilters.registerFilter(filter);
        Assert.assertTrue("Filter registration failed!", CommandSpyFilters.getActiveFilters().contains(filter));

        CommandSpyFilters.removeDeadFilters();
        Assert.assertFalse("Dead filter illegally persisted!", CommandSpyFilters.getActiveFilters().contains(filter));
    }

    @Test
    public void removeDeadFiltersTest2() {
        CommandSpyFilter filter = mock(CommandSpyFilter.class);
        when(Bukkit.getServer().getOnlinePlayers()).thenReturn(playersWithTarget);
        when(filter.getSubscribers()).thenReturn(Lists.newArrayList(targetId, offlineId));

        CommandSpyFilters.registerFilter(filter);
        Assert.assertTrue("Filter registration failed!", CommandSpyFilters.getActiveFilters().contains(filter));

        CommandSpyFilters.removeDeadFilters();
        Assert.assertTrue("Non-empty filter illegally removed!", CommandSpyFilters.getActiveFilters().contains(filter));

        when(Bukkit.getServer().getOnlinePlayers()).thenReturn(new Player[0]);
        CommandSpyFilters.removeDeadFilters();
        Assert.assertFalse("Dead filter illegally persisted!", CommandSpyFilters.getActiveFilters().contains(filter));
    }

    @Test
    public void removeOfflineSubscribersTest() {
        CommandSpyFilter filter = mock(CommandSpyFilter.class);
        when(Bukkit.getServer().getOnlinePlayers()).thenReturn(playersWithTarget);
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
        when(Bukkit.getServer().getOnlinePlayers()).thenReturn(playersWithTarget);
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
        when(Bukkit.getServer().getOnlinePlayers()).thenReturn(playersWithTarget);
        when(filter.getSubscribers()).thenReturn(Lists.newArrayList(targetId));
        when(filter.canSubscribe()).thenReturn(true);

        CommandSpyFilters.registerFilter(filter);
        Assert.assertTrue("Filter registration failed!", CommandSpyFilters.getActiveFilters().contains(filter));

        CommandSpyFilters.unsubscribeFromAll(targetId);
        Assert.assertFalse("Unsubscription failed!", filter.getSubscribers().contains(targetId));
    }
}