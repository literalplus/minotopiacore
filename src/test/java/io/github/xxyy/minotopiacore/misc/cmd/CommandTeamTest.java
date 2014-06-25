package io.github.xxyy.minotopiacore.misc.cmd;

import io.github.xxyy.minotopiacore.TestHelper;
import io.github.xxyy.minotopiacore.hook.PexHook;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommandTeamTest {
    private static Server SERVER = TestHelper.mockServer();

    @BeforeClass
    public static void setUpClass() throws Exception {
        Bukkit.setServer(SERVER);
    }

    @Test
    public void testOnlineCheck() {
        List<CommandTeam.TeamGroup> groups = new ArrayList<>();
        UUID fakeUserId = UUID.randomUUID();
        PexHook.User usr = mockUser(fakeUserId, "somename");
        groups.add(new CommandTeam.TeamGroup(mockGroup(Arrays.asList(usr))));

        List<CommandTeam.TeamMember> allMembers = new LinkedList<>();
        groups.stream().forEach((grp) -> allMembers.addAll(grp.getMembers()));

        Player[] players = {mockPlayer(fakeUserId, "somename"), mockPlayer(UUID.randomUUID(), "aigkbuedshg")};
        when(SERVER.getOnlinePlayers()).thenReturn(players);

        Arrays.asList(SERVER.getOnlinePlayers()).parallelStream()
                .forEach((plr) -> allMembers.stream()
                        .forEach((member) -> member.checkMatch(plr)
                        )); //Only loop through online players once

        CommandTeam.TeamMember target = groups.stream().findAny().get()
                .getMembers().stream().filter(mem -> mem.getUuid().equals(fakeUserId)).findAny().get();
        Assert.assertNotNull("Couldn't find target member!", target);
        Assert.assertTrue("Online target not marked as online!", target.isLastOnline());
    }

    private PexHook.Group mockGroup(List<PexHook.User> users) {
        PexHook.Group rtrn = mock(PexHook.Group.class);
        when(rtrn.getName()).thenReturn("test");
        when(rtrn.getPrefix()).thenReturn("TST");
        when(rtrn.getUsers()).thenReturn(users);
        return rtrn;
    }

    private Player mockPlayer(final UUID uuid, final String name) {
        Player plr = mock(Player.class);
        when(plr.getUniqueId()).thenReturn(uuid);
        when(plr.getName()).thenReturn(name);
        return plr;
    }

    private PexHook.User mockUser(UUID uuid, String name) {
        return new PexHook.User() {
            @Override
            public String getIdentifier() {
                return uuid.toString();
            }

            @Override
            public UUID getUniqueId() {
                return uuid;
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }
}
