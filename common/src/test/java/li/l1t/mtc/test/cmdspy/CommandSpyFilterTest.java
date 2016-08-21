/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.test.cmdspy;

import li.l1t.common.test.util.MockHelper;
import li.l1t.lib.guava17.collect.ImmutableList;
import li.l1t.mtc.chat.cmdspy.CommandSpyFilter;
import li.l1t.mtc.chat.cmdspy.CommandSpyFilters;
import li.l1t.mtc.chat.cmdspy.RegExCommandSpyFilter;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.mockito.Mockito.when;

/**
 * Tests functionality of CommandSpy.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 30.6.14
 */
@SuppressWarnings("unchecked") //Weird casts with Server#getOnlinePlayers()Ljava.lang.Collection;
public class CommandSpyFilterTest {
    private static final Server SERVER = MockHelper.mockServer();
    private UUID targetId = UUID.randomUUID();
    private UUID otherId = UUID.randomUUID();

    @Test
    public void testGlobalFilter() {
        Player fakeSpy = MockHelper.mockPlayer(targetId, "spy");
        when((Collection<Player>) SERVER.getOnlinePlayers()).thenReturn(ImmutableList.of(fakeSpy));
        CommandSpyFilters.unsubscribeFromAll(targetId); //Security measure
        Assert.assertTrue("Couldn't inject fake player to global filter!", CommandSpyFilters.toggleGlobalFilter(fakeSpy));

        CommandSpyFilter globalFilter = CommandSpyFilters.getSubscribedFilters(targetId)
                .findAny().get();

        Assert.assertTrue("Global filter didn't match!",
                globalFilter.matches("/any-command I enter should match this filter, even then it's as long as this öne?!öäüß", fakeSpy)); //additional slashes shouldn't matter
    }

    @Test
    public void testPlayerFilter() {
        Player fakeSpy = MockHelper.mockPlayer(targetId, "spy");
        Player fakeTarget = MockHelper.mockPlayer(otherId, "target"); //Need that one online or the filter will be destroyed
        when((Collection<Player>) SERVER.getOnlinePlayers()).thenReturn(Arrays.asList(fakeSpy, fakeTarget));
        CommandSpyFilters.unsubscribeFromAll(targetId); //Security measure

        CommandSpyFilter playerFilter = CommandSpyFilters.playerFilter(otherId);

        Assert.assertTrue("Player filter didn't match target!",
                playerFilter.matches("any-command I enter should match this filter, even then it's as long as this öne?!öäüß", fakeTarget));
        Assert.assertFalse("Player filter matched invalid message!",
                playerFilter.matches("This shouldn't match!!", fakeSpy));
    }

    @Test
    public void testTextFilter() {
        Player fakeSpy = MockHelper.mockPlayer(targetId, "spy");
        when((Collection<Player>) SERVER.getOnlinePlayers()).thenReturn(Collections.singletonList(fakeSpy));
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
        Player fakeSpy = MockHelper.mockPlayer(targetId, "spy");
        when((Collection<Player>) SERVER.getOnlinePlayers()).thenReturn(Collections.singletonList(fakeSpy));
        CommandSpyFilters.unsubscribeFromAll(targetId); //Security measure

        String targetString = "inert string";
        Assert.assertTrue("Couldn't add target to new string filter", CommandSpyFilters.toggleStringFilter(targetString, fakeSpy));
        Assert.assertFalse("String filter wasn't removed on second toggle!", CommandSpyFilters.toggleStringFilter(targetString, fakeSpy));
    }

    @Test
    public void testRegexFilter() {
        Player fakeSpy = MockHelper.mockPlayer(targetId, "spy");
        when((Collection<Player>) SERVER.getOnlinePlayers()).thenReturn(Collections.singletonList(fakeSpy));
        CommandSpyFilters.unsubscribeFromAll(targetId); //Security measure

        String regex = "(open)?inv(see)?";
        String rawRegex = "(" + regex + ")\\s*";
        CommandSpyFilter stringFilter = CommandSpyFilters.stringFilter("!r" + regex);
        Assert.assertTrue(stringFilter instanceof RegExCommandSpyFilter);

        String matching = "invsee chris301234";
        String notMatching = "shop kaufen xT0Bi";

        Assert.assertTrue(Pattern.compile(rawRegex).matcher(matching).find());
        Assert.assertFalse(Pattern.compile(rawRegex).matcher(notMatching).find());

        Assert.assertTrue("RegEx filter didn't match target!",
                stringFilter.matches(matching, fakeSpy));
        Assert.assertFalse("RegEx filter matched wrong message",
                stringFilter.matches(notMatching, fakeSpy));
    }

    @Test
    public void testToggleRegExFilter() {
        Player fakeSpy = MockHelper.mockPlayer(targetId, "spy");
        when((Collection<Player>) SERVER.getOnlinePlayers()).thenReturn(Collections.singletonList(fakeSpy));
        CommandSpyFilters.unsubscribeFromAll(targetId); //Security measure

        String targetString = "!r(.+?)woa\\2";
        Assert.assertTrue("Couldn't add target to new RegEx filter", CommandSpyFilters.toggleStringFilter(targetString, fakeSpy));
        Assert.assertFalse("RegEx filter wasn't removed on second toggle!", CommandSpyFilters.toggleStringFilter(targetString, fakeSpy));
    }
}
