package io.github.xxyy.mtc.test;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

@SuppressWarnings("UnusedDeclaration")
public class TestHelper {
    private TestHelper() {

    }

    public static Server mockServer() {
        Server server = Bukkit.getServer();

        if (server == null) {
            server = mock(Server.class);
        } else {
            reset(server);
        }

        when(server.getName()).thenReturn("Spagt");
        when(server.getBukkitVersion()).thenReturn("fuk of bukite");
        when(server.getVersion()).thenReturn("infinity");
        when(server.getLogger()).thenReturn(Logger.getLogger(Server.class.getName()));
        when(server.getConsoleSender()).thenAnswer(invocation -> loggerSender(mock(ConsoleCommandSender.class), Bukkit.getServer().getLogger()));
        when(server.getPlayer(any(UUID.class))).then(id -> Arrays.asList(Bukkit.getServer().getOnlinePlayers()).stream()
                .filter(plr -> plr.getUniqueId().equals(id.getArguments()[0]))
                .findAny().orElse(null));

        if(Bukkit.getServer() == null) {
            Bukkit.setServer(server);
        }

        return server;
    }

    public static Player mockPlayer(final UUID uuid, final String name) {
        Player plr = mock(Player.class);
        when(plr.getUniqueId()).thenReturn(uuid);
        when(plr.getName()).thenReturn(name);
        return plr;
    }

    public static CommandSender printlnSender(CommandSender sender) {
        if (!Mockito.mockingDetails(sender).isMock()) {
            sender = spy(sender);
        }

        Mockito.doAnswer((invocation) -> {
            System.out.println(invocation.getArguments()[0]);
            return null;
        }).when(sender).sendMessage(any(String.class));

        return sender;
    }

    public static CommandSender loggerSender(CommandSender sender, Logger logger) {
        if (!Mockito.mockingDetails(sender).isMock()) {
            sender = spy(sender);
        }

        Mockito.doAnswer((invocation) -> {
            logger.info((String) invocation.getArguments()[0]);
            return null;
        }).when(sender).sendMessage(any(String.class));

        return sender;
    }
}
