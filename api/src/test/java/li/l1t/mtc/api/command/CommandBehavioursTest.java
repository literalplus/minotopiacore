/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommandBehavioursTest {

    public static final String SOME_PERMISSION_NODE = "some.permission.node";

    @Test
    public void testPermissionChecking() throws Exception {
        CommandBehaviour behaviour = CommandBehaviours.permissionChecking(SOME_PERMISSION_NODE);

        CommandSender permittedSender = permittedCommandSender(SOME_PERMISSION_NODE);
        CommandSender unpermittedSender = permittedCommandSender("some.other.node");

        Assert.assertThat("Permitted sender not allowed",
                behaviour.apply(permittedSender, null, null, null), is(true));
        Assert.assertThat("Not permitted sender falsely allowed",
                behaviour.apply(unpermittedSender, null, null, null), is(false));
    }

    @Test
    public void testSubPermissionChecking() throws Exception {
        CommandBehaviour behaviour = CommandBehaviours.subPermissionChecking(SOME_PERMISSION_NODE);

        CommandSender permittedSender = permittedCommandSender(SOME_PERMISSION_NODE);
        CommandSender unpermittedSender = permittedCommandSender("some.other.node");
        CommandSender subPermittedSender = permittedCommandSender(SOME_PERMISSION_NODE + ".sub");

        String[] emptyArgs = new String[0];
        String[] subArgs = new String[]{"sub", "more arguments"};
        String[] otherArgs = new String[]{"some", "different", "arguments"};

        Assert.assertThat("Permitted sender not allowed (empty arguments)",
                behaviour.apply(permittedSender, null, null, emptyArgs), is(true));
        Assert.assertThat("Not permitted sender falsely allowed (empty arguments)",
                behaviour.apply(unpermittedSender, null, null, emptyArgs), is(false));

        Assert.assertThat("Semi-permitted sender falsely allowed (sub arguments)",
                behaviour.apply(permittedSender, null, null, subArgs), is(false));
        Assert.assertThat("Not permitted sender falsely allowed (sub arguments)",
                behaviour.apply(unpermittedSender, null, null, subArgs), is(false));
        Assert.assertThat("Sub-permitted sender not allowed (sub arguments)",
                behaviour.apply(subPermittedSender, null, null, subArgs), is(true));

        Assert.assertThat("Semi-permitted sender falsely allowed (other arguments)",
                behaviour.apply(permittedSender, null, null, otherArgs), is(false));
        Assert.assertThat("Sub-permitted sender falsely allowed (other arguments)",
                behaviour.apply(subPermittedSender, null, null, otherArgs), is(false));
    }

    @Test
    public void testPlayerOnly() throws Exception {
        CommandBehaviour behaviour = CommandBehaviours.playerOnly();

        Assert.assertThat("Non-player sender falsely allowed",
                behaviour.apply(mock(CommandSender.class), null, null, null), is(false));
        Assert.assertThat("Player sender not allowed",
                behaviour.apply(mock(Player.class), null, null, null), is(true));
    }

    private CommandSender permittedCommandSender(String assignedPermission) {
        CommandSender commandSender = mock(CommandSender.class);
        when(commandSender.hasPermission(any(String.class))).thenAnswer(
                invocation -> invocation.getArguments()[0].equals(assignedPermission)
        );
        return commandSender;
    }
}
