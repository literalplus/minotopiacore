package io.github.xxyy.minotopiacore;

import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;

import java.util.logging.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestHelper {
    private TestHelper() {

    }

    public static Server mockServer() {
        Server server = mock(Server.class);

        when(server.getName()).thenReturn("Spagt");
        when(server.getBukkitVersion()).thenReturn("fuk of bukite");
        when(server.getVersion()).thenReturn("infinity");
        when(server.getLogger()).thenReturn(Logger.getLogger(Server.class.getName()));
        when(server.getConsoleSender()).thenAnswer(invocation -> mock(ConsoleCommandSender.class));

        return server;
    }
}
