package io.github.xxyy.minotopiacore.test.misc.cmd;

import io.github.xxyy.minotopiacore.misc.cmd.CommandTeam;
import io.github.xxyy.minotopiacore.test.TestHelper;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

public class CommandTeamTest {
    private static Server SERVER = TestHelper.mockServer();
    private static Player[] players;
    private static List<CommandTeam.TeamMember> members = new LinkedList<>();
    public static final Random RANDOM = new Random();

    @BeforeClass
    public static void setUpClass() throws Exception {
        if(Bukkit.getServer() == null) {
            Bukkit.setServer(SERVER);
        }

        //Fake players
        List<Player> playerList = new ArrayList<>(200);

        for (int i = 0; i < 175; i++) {
            playerList.add(TestHelper.mockPlayer(UUID.randomUUID(), "somename"));
        }

        for (int i = 0; i < 30; i++) {
            Player plr = TestHelper.mockPlayer(UUID.randomUUID(), "somename");
            if (RANDOM.nextBoolean()) {
                playerList.add(plr);
            }

            members.add(new CommandTeam.TeamMember(plr.getUniqueId(), plr.getName()));
        }

        players = playerList.toArray(new Player[playerList.size()]);

        System.out.println("Online players: " + players.length + "     Team members: " + members.size());
    }

    @Test
    public void testExecution() {

    }
}
