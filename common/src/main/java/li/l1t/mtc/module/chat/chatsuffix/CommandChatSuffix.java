/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.chatsuffix;

import li.l1t.common.util.CommandHelper;
import li.l1t.common.util.StringHelper;
import li.l1t.mtc.hook.XLoginHook;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command to manage chat suffixes.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
class CommandChatSuffix implements CommandExecutor {
    private final ChatSuffixModule module;

    public CommandChatSuffix(ChatSuffixModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            return sendHelpTo(sender);
        }
        return handleExecution(sender, args);
    }

    private boolean sendHelpTo(CommandSender sender) {
        sender.sendMessage("§6 »»» §a/chatfarbe §6«««");
        sender.sendMessage("§a/cf show §6Zeigt deine Chatfarbe");
        sender.sendMessage("§a/cf set [Chatfarbe...] §6Setzt deine Chatfarbe");
        if (sender.hasPermission("mtc.chatfarbe.player")) {
            sender.sendMessage("§a/cf showother [Name|UUID] §6Zeigt die Chatfarbe eines anderen");
            sender.sendMessage("§a/cf setother [Name|UUID] §6Setzt die Chatfarbe eines anderen");
        }
        return true;
    }

    private boolean handleExecution(CommandSender sender, String[] args) {
        switch (args[0].toLowerCase()) {
            case "show":
                return handleShow(sender);
            case "showother":
                return handleShowOther(sender, args);
            case "set":
                return handleSet(sender, args);
            case "setother":
                return handleSetOther(sender, args);
            default:
                return handleUnknownAction(sender);
        }
    }

    private boolean handleUnknownAction(CommandSender sender) {
        sender.sendMessage("§c§lFehler: §cUnbekannte Aktion.");
        sendHelpTo(sender);
        return true;
    }

    private boolean handleShow(CommandSender sender) {
        if (CommandHelper.kickConsoleFromMethod(sender, "chatfarbe")) {
            return true;
        }
        showOwnChatSuffixTo((Player) sender);
        return true;
    }

    private void showOwnChatSuffixTo(Player player) {
        String suffix = module.getRepository().findChatSuffixById(player.getUniqueId());
        sendFormattedTo(player, "Deine Chatfarbe: §f%stext", suffix);
    }

    private void sendFormattedTo(CommandSender sender, String format, Object... params) {
        sender.sendMessage(module.formatMessage(format, params));
    }

    private boolean handleShowOther(CommandSender sender, String[] args) {
        if (checkModifyOthersPermission(sender) || checkArgumentCount(sender, args, 2)) {
            return true;
        }
        XLoginHook.Profile profile = module.getPlugin().getXLoginHook().getBestProfile(args[1]);
        if (profile == null) {
            sender.sendMessage("§c§lFehler: §cKein solcher Spieler bekannt.");
            return true;
        }
        showOthersChatSuffixTo(sender, profile);
        return true;
    }

    private void showOthersChatSuffixTo(CommandSender receiver, XLoginHook.Profile other) {
        String suffix = module.getRepository().findChatSuffixById(other.getUniqueId());
        if (isSamePlayer(receiver, other)) {
            showOwnChatSuffixTo(((Player) receiver));
        } else {
            sendFormattedTo(receiver, "Chatfarbe von %s: §f%stext", other.getName(), suffix);
        }
    }

    private boolean isSamePlayer(CommandSender receiver, XLoginHook.Profile other) {
        return receiver instanceof Player && ((Player) receiver).getUniqueId().equals(other.getUniqueId());
    }

    private boolean checkArgumentCount(CommandSender sender, String[] args, int expected) {
        if (args.length < expected) {
            sender.sendMessage("§c§lFehler: §cZu wenige Argumente.");
            return true;
        }
        return false;
    }

    private boolean checkModifyOthersPermission(CommandSender sender) {
        if (!sender.hasPermission("mtc.chatfarbe.player")) {
            sender.sendMessage("§c§lFehler: §cDu hast keine Berechtigung, die Chatfarbe anderer zu ändern.");
            return true;
        }
        return false;
    }

    private boolean handleSet(CommandSender sender, String[] args) {
        String newSuffix = StringHelper.varArgsString(args, 1, true);
        if (CommandHelper.kickConsoleFromMethod(sender, "chatfarbe") ||
                suffixExceedsMaxLength(sender, newSuffix)) {
            return true;
        }
        Player player = (Player) sender;
        module.getRepository().saveChatSuffix(player.getUniqueId(), newSuffix);
        showOwnChatSuffixTo(player);
        return true;
    }

    private boolean suffixExceedsMaxLength(CommandSender sender, String newSuffix) {
        if (newSuffix.length() > 20) {
            sender.sendMessage("§c§lFehler: §cEine Chatfarbe darf maximal 20 Zeichen haben. (ist: " + newSuffix.length() + ")");
            return true;
        }
        return false;
    }

    private boolean handleSetOther(CommandSender sender, String[] args) {
        if (checkModifyOthersPermission(sender) || checkArgumentCount(sender, args, 2)) {
            return true;
        }
        String newSuffix = StringHelper.varArgsString(args, 2, true);
        if (suffixExceedsMaxLength(sender, newSuffix)) {
            return true;
        }
        XLoginHook.Profile profile = module.getPlugin().getXLoginHook().getBestProfile(args[1]);
        if (profile == null) {
            sender.sendMessage("§c§lFehler: §cKein solcher Spieler bekannt.");
            return true;
        }
        module.getRepository().saveChatSuffix(profile.getUniqueId(), newSuffix);
        sendFormattedTo(sender, "Neue Chatfarbe von %s: §f%stext", profile.getName(), newSuffix);
        return true;
    }
}
