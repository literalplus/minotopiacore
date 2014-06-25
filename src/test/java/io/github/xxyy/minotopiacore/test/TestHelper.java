package io.github.xxyy.minotopiacore.test;

import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
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

    public static Player mockPlayer(final UUID uuid, final String name) {
        Player plr = mock(Player.class);
        when(plr.getUniqueId()).thenReturn(uuid);
        when(plr.getName()).thenReturn(name);
        return plr;
    }
}
