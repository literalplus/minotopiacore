package io.github.xxyy.minotopiacore.test.cmdspy;

import io.github.xxyy.minotopiacore.chat.cmdspy.CommandSpyFilter;
import io.github.xxyy.minotopiacore.chat.cmdspy.CommandSpyFilters;
import io.github.xxyy.minotopiacore.test.TestHelper;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

/**
 * Tests functionality of CommandSpy.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 30.6.14
 */
public class CommandSpyFilterTest {
    private static final Server SERVER = TestHelper.mockServer();
    private UUID targetId = UUID.randomUUID();

    @Test
    public void testGlobalFilter() {
        Player fakeSpy = (Player) TestHelper.loggerSender(TestHelper.mockPlayer(targetId, "spy"), Logger.getLogger(getClass().getName()));
        when(SERVER.getOnlinePlayers()).thenReturn(new Player[]{fakeSpy});
        CommandSpyFilters.unsubscribeFromAll(targetId); //Security measure
        Assert.assertTrue("Couldn't inject fake player to global filter!", CommandSpyFilters.toggleGlobalFilter(fakeSpy));

        CommandSpyFilter globalFilter = CommandSpyFilters.getSubscribedFilters(targetId)
                .findAny().get();

        Assert.assertTrue(globalFilter.matches("any-command I enter should match this filter, even then it's as long as this öne?!öäüß", fakeSpy));
    }
}
