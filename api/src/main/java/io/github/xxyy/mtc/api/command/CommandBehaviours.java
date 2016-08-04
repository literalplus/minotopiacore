/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.api.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.xxyy.common.util.CommandHelper;

/**
 * Static utility class providing some commonly used behaviours
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-12-30
 */
public class CommandBehaviours {
    private CommandBehaviours() {

    }

    /**
     * Creates a behaviour that checks for a defined permission before command execution and issues a warning message
     * if the sender does not have the required permission, localised using XYC.
     *
     * @param permission the permission required for execution
     * @return the behaviour
     * @see CommandHelper#checkPermAndMsg(CommandSender, String, String)
     */
    public static CommandBehaviour permissionChecking(String permission) {
        return (sender, label, cmd, args) -> checkPermissionWithMessage(sender, permission);
    }

    /**
     * <p>
     * Creates a behaviour that checks for a defined permission before command execution and issues a warning message
     * if the sender does not have the required permission, localised using XYC. If the first argument ({@code args[0]}
     * is specified, it is appended to the permission.
     * </p>
     * <p>
     * Example: For {@code permission} = 'mtc.cmd' and execution '/&lt;command&gt; fm', the checked permission
     * would be 'mtc.cmd.fm'. For '/&lt;command&gt;' it would be 'mtc.cmd'.
     * </p>
     * <p>
     * Note that behaviours are called before execution, so this always shows a permission error message for
     * sub commands the sender does not have permission for, even if the do not exist.
     * </p>
     *
     * @param permission the base permission required for execution
     * @return the behaviour
     * @see CommandHelper#checkPermAndMsg(CommandSender, String, String)
     */
    public static CommandBehaviour subPermissionChecking(String permission) {
        return (sender, label, cmd, args) -> {
            if (args.length == 0) {
                return checkPermissionWithMessage(sender, permission);
            } else {
                return checkPermissionWithMessage(sender, permission + '.' + args[0]);
            }
        };
    }

    /**
     * Creates a behaviour that only allows players to execute a command.
     *
     * @return the behaviour
     */
    public static CommandBehaviour playerOnly() {
        return (sender, label, cmd, args) -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cNur Spieler können diesen Befehl verwenden!");
                return false;
            } else {
                return true;
            }
        };
    }

    private static boolean checkPermissionWithMessage(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(String.format("§cDu darfst diesen Befehl nicht ausführen! §6(%s)", permission));
            return false;
        } else {
            return true;
        }
    }
}
