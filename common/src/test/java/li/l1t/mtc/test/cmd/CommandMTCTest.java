/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.test.cmd;

import com.google.common.collect.ImmutableList;
import li.l1t.common.test.util.MockHelper;
import li.l1t.mtc.MTC;
import li.l1t.mtc.misc.cmd.CommandMTC;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests some testable parts of the /mtc command.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 5.7.14
 */
public class CommandMTCTest {
    private static final Server SERVER = MockHelper.mockServer();
    private static CommandMTC commandMTC;
    private static Player fakeSender;
    private static Command fakeCommand = mock(Command.class);

    @BeforeClass
    public static void init() {
        MTC mtc = mock(MTC.class);
        FileConfiguration cfg = mock(FileConfiguration.class);
        when(cfg.getBoolean(contains("enable"))).thenReturn(true);
        when(mtc.getConfig()).thenReturn(cfg);
        commandMTC = new CommandMTC(mtc);

        fakeSender = MockHelper.mockPlayer(UUID.randomUUID(), "sender");
        when(fakeSender.hasPermission(any(String.class))).thenReturn(true);
    }

    @Test
    @SuppressWarnings("unchecked")
    //Mockito being weird with Server#getOnlinePlayers()Ljava.util.Collection; and generics
    public void testFakeMessage() {
        Player otherPlayer = MockHelper.mockPlayer(UUID.randomUUID(), "other");
        when(otherPlayer.hasPermission(any(String.class))).thenReturn(false);

        when((Collection<Player>) SERVER.getOnlinePlayers()).thenReturn(ImmutableList.of(otherPlayer));
        commandMTC.catchCommand(fakeSender, null, fakeCommand, "mtc", new String[]{"fm", "&6wowe"});

//        verify(fakeSender).sendMessage(eq("§7(/mtc fm|" + fakeSender.getName() + ")§f §6wowe")); //Can't really check JSON messages easily
        verify(otherPlayer).sendMessage(eq("§6wowe"));
    }
}
