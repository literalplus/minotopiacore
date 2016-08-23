/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.mute;

import li.l1t.common.chat.ComponentSender;
import li.l1t.common.chat.XyComponentBuilder;
import li.l1t.common.util.CommandHelper;
import li.l1t.common.util.StringHelper;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.exception.UserException;
import li.l1t.mtc.hook.XLoginHook;
import li.l1t.mtc.module.chat.mute.api.Mute;
import li.l1t.mtc.module.chat.mute.api.MuteManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.Instant;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A command to mute a player, with mandatory reason and duration.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
class CommandMute implements CommandExecutor {
    private final XLoginHook xLoginHook;
    private final MuteManager muteManager;

    CommandMute(XLoginHook xLoginHook, MuteManager muteManager) {
        this.xLoginHook = xLoginHook;
        this.muteManager = muteManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3 || args[0].equalsIgnoreCase("help")) {
            sendHelpTo(sender);
            return true;
        }
        XLoginHook.Profile profile = findSingleMatchingProfileOrFail(args[0], sender, args);
        Instant expiryTime = parseExpiryTimeFromOrFail(args[1]);
        String reason = StringHelper.varArgsString(args, 2, true);
        Mute mute = muteManager.getMuteFor(profile);
        mute.update(CommandHelper.getSenderId(sender), expiryTime, reason);
        muteManager.saveMute(mute);
        notifySender(sender, profile);
        notifyAllPlayers(profile, reason, sender);
        notifyTeamMembers(sender, profile);
        return true;
    }

    private void sendHelpTo(CommandSender sender) {
        sender.sendMessage("§a/mute [Spieler] [Dauer] [Grund...] §6Muted einen Spieler");
        sender.sendMessage("§6Beispiele für Dauer: §a15m§6,§a 7d§6,§a5h");
    }

    private XLoginHook.Profile findSingleMatchingProfileOrFail(String input, CommandSender sender, String[] args) {
        return xLoginHook.findSingleMatchingProfileOrFail(
                input, sender, selectProfileCommandBuilder(args)
        );
    }

    private Function<XLoginHook.Profile, String> selectProfileCommandBuilder(String[] args) {
        return key -> "/mute " + key.getUniqueId() +
                " " + Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
    }

    private Instant parseExpiryTimeFromOrFail(String arg) {
        long millisecondsDuration = tryParseTimePeriod(arg);
        return Instant.now().plusMillis(millisecondsDuration);
    }

    private long tryParseTimePeriod(String arg) {
        try {
            return StringHelper.parseTimePeriod(arg);
        } catch (IllegalArgumentException e) {
            throw UserException.wrap(e, "Ungültige Mutedauer: " + e.getMessage());
        }
    }

    private void notifySender(CommandSender sender, XLoginHook.Profile profile) {
        MessageType.RESULT_LINE_SUCCESS.sendTo(sender, "Du hast %s gemuted.", profile.getName());
    }

    private void notifyAllPlayers(XLoginHook.Profile profile, String reason, CommandSender sender) {
        MessageType.BROADCAST.broadcast(sender.getServer(),
                "§s%s §pwurde gemuted. Grund: §s%s", profile.getName(), reason
        );
    }

    private void notifyTeamMembers(CommandSender sender, XLoginHook.Profile profile) {
        sender.getServer().getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("mtc.spy"))
                .forEach(plr -> ComponentSender.sendTo(
                        new XyComponentBuilder("[", ChatColor.DARK_GRAY)
                                .append("MTC", ChatColor.GOLD).bold(true)
                                .append("] ", ChatColor.DARK_GRAY).bold(false)
                                .append(profile.getName(), ChatColor.GREEN)
                                .append(" wurde gemuted von ", ChatColor.GOLD)
                                .append(sender.getName(), ChatColor.GREEN)
                                .append(". ", ChatColor.GOLD)
                                .append("[Mehr...]", ChatColor.GREEN).italic(true)
                                .hintedCommand("/muteinfo " + profile.getUniqueId())
                                .create(),
                        sender
                ));
    }
}
