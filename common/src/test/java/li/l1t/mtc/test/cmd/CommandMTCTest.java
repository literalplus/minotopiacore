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

package li.l1t.mtc.test.cmd;

import li.l1t.common.test.util.MockHelper;
import li.l1t.mtc.MTC;
import li.l1t.mtc.misc.cmd.CommandMTC;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.*;

/**
 * Tests some testable parts of the /mtc command.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 5.7.14
 */
public class CommandMTCTest {
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

        MockHelper.mockServer().setOnlinePlayers(otherPlayer);
        commandMTC.catchCommand(fakeSender, null, fakeCommand, "mtc", new String[]{"fm", "&6wowe"});

//        verify(fakeSender).sendMessage(eq("§7(/mtc fm|" + fakeSender.getName() + ")§f §6wowe")); //Can't really check JSON messages easily
        verify(otherPlayer).sendMessage(eq("§6wowe"));
    }
}
