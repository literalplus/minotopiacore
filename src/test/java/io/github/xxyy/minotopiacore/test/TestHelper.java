package io.github.xxyy.minotopiacore.test;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.mockito.Mockito;

import java.util.UUID;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

public class TestHelper {
    private TestHelper() {

    }

    public static Server mockServer() {
        Server server = mock(Server.class);

        when(server.getName()).thenReturn("Spagt");
        when(server.getBukkitVersion()).thenReturn("fuk of bukite");
        when(server.getVersion()).thenReturn("infinity");
        when(server.getLogger()).thenReturn(Logger.getLogger(Server.class.getName()));
        when(server.getConsoleSender()).thenAnswer(invocation -> loggerSender(mock(ConsoleCommandSender.class), server.getLogger()));

        return server;
    }

    public static Player mockPlayer(final UUID uuid, final String name) {
        Player plr = mock(Player.class);
        when(plr.getUniqueId()).thenReturn(uuid);
        when(plr.getName()).thenReturn(name);
        return plr;
    }

    public static CommandSender printlnSender(CommandSender sender) {
        CommandSender rtrn = spy(sender);
        Mockito.doAnswer((invocation) -> {System.out.println(invocation.getArguments()[0]); return null;})
                .when(rtrn).sendMessage(any(String.class));
        return rtrn;
    }

    public static CommandSender loggerSender(CommandSender sender, Logger logger) {
        CommandSender rtrn = spy(sender);
        Mockito.doAnswer((invocation) -> {logger.info((String) invocation.getArguments()[0]); return null;})
                .when(rtrn).sendMessage(any(String.class));
        return rtrn;
    }
}
