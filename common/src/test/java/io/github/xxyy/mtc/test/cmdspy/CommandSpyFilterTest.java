package io.github.xxyy.mtc.test.cmdspy;

import io.github.xxyy.mtc.chat.cmdspy.CommandSpyFilter;
import io.github.xxyy.mtc.chat.cmdspy.CommandSpyFilters;
import io.github.xxyy.mtc.chat.cmdspy.RegExCommandSpyFilter;
import io.github.xxyy.mtc.test.TestHelper;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

import static org.mockito.Mockito.when;

/**
 * Tests functionality of CommandSpy.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 30.6.14
 */
public class CommandSpyFilterTest {
    private static final Server SERVER = TestHelper.mockServer();
    private UUID targetId = UUID.randomUUID();
    private UUID otherId = UUID.randomUUID();

    @Test
    public void testGlobalFilter() {
        Player fakeSpy = TestHelper.mockPlayer(targetId, "spy");
        when(SERVER.getOnlinePlayers()).thenReturn(new Player[]{fakeSpy});
        CommandSpyFilters.unsubscribeFromAll(targetId); //Security measure
        Assert.assertTrue("Couldn't inject fake player to global filter!", CommandSpyFilters.toggleGlobalFilter(fakeSpy));

        CommandSpyFilter globalFilter = CommandSpyFilters.getSubscribedFilters(targetId)
                .findAny().get();

        Assert.assertTrue("Global filter didn't match!",
                globalFilter.matches("/any-command I enter should match this filter, even then it's as long as this öne?!öäüß", fakeSpy)); //additional slashes shouldn't matter
    }

    @Test
    public void testPlayerFilter() {
        Player fakeSpy = TestHelper.mockPlayer(targetId, "spy");
        Player fakeTarget = TestHelper.mockPlayer(otherId, "target"); //Need that one online or the filter will be destroyed
        when(SERVER.getOnlinePlayers()).thenReturn(new Player[]{fakeSpy, fakeTarget});
        CommandSpyFilters.unsubscribeFromAll(targetId); //Security measure

        CommandSpyFilter playerFilter = CommandSpyFilters.playerFilter(otherId);

        Assert.assertTrue("Player filter didn't match target!",
                playerFilter.matches("any-command I enter should match this filter, even then it's as long as this öne?!öäüß", fakeTarget));
        Assert.assertFalse("Player filter matched invalid message!",
                playerFilter.matches("This shouldn't match!!", fakeSpy));
    }

    @Test
     public void testTextFilter() {
        Player fakeSpy = TestHelper.mockPlayer(targetId, "spy");
        when(SERVER.getOnlinePlayers()).thenReturn(new Player[]{fakeSpy});
        CommandSpyFilters.unsubscribeFromAll(targetId); //Security measure

        String targetString = "inert string";
        CommandSpyFilter stringFilter = CommandSpyFilters.stringFilter(targetString);

        Assert.assertTrue("String filter didn't match target!",
                stringFilter.matches(targetString, fakeSpy));
        Assert.assertFalse("String filter matched wrong message",
                stringFilter.matches("Some random message", fakeSpy));
    }

    @Test
    public void testToggleTextFilter() {
        Player fakeSpy = TestHelper.mockPlayer(targetId, "spy");
        when(SERVER.getOnlinePlayers()).thenReturn(new Player[]{fakeSpy});
        CommandSpyFilters.unsubscribeFromAll(targetId); //Security measure

        String targetString = "inert string";
        Assert.assertTrue("Couldn't add target to new string filter", CommandSpyFilters.toggleStringFilter(targetString, fakeSpy));
        Assert.assertFalse("String filter wasn't removed on second toggle!", CommandSpyFilters.toggleStringFilter(targetString, fakeSpy));
    }

    @Test
    public void testRegexFilter() {
        Player fakeSpy = TestHelper.mockPlayer(targetId, "spy");
        when(SERVER.getOnlinePlayers()).thenReturn(new Player[]{fakeSpy});
        CommandSpyFilters.unsubscribeFromAll(targetId); //Security measure

        String regex = "(.+?)woa\\2";
        String rawRegex = "("+regex+")\\s*";
        CommandSpyFilter stringFilter = CommandSpyFilters.stringFilter("!r" + regex);
        Assert.assertTrue(stringFilter instanceof RegExCommandSpyFilter);

        String matching = " something woa something ";
        String notMatching = "something that contains woa but doesn't even match";

        Assert.assertTrue(matching.matches(rawRegex));
        Assert.assertFalse(notMatching.matches(rawRegex));

        Assert.assertTrue("RegEx filter didn't match target!",
                stringFilter.matches(matching, fakeSpy));
        Assert.assertFalse("RegEx filter matched wrong message",
                stringFilter.matches(notMatching, fakeSpy));
    }

    @Test
    public void testToggleRegExFilter() {
        Player fakeSpy = TestHelper.mockPlayer(targetId, "spy");
        when(SERVER.getOnlinePlayers()).thenReturn(new Player[]{fakeSpy});
        CommandSpyFilters.unsubscribeFromAll(targetId); //Security measure

        String targetString = "!r(.+?)woa\\2";
        Assert.assertTrue("Couldn't add target to new RegEx filter", CommandSpyFilters.toggleStringFilter(targetString, fakeSpy));
        Assert.assertFalse("RegEx filter wasn't removed on second toggle!", CommandSpyFilters.toggleStringFilter(targetString, fakeSpy));
    }
}
