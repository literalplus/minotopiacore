/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub.ui.text;

import li.l1t.common.command.BukkitExecution;
import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.common.util.StringHelper;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.command.MTCExecutionExecutor;
import li.l1t.mtc.hook.XLoginHook;
import li.l1t.mtc.module.nub.LocalProtectionManager;
import li.l1t.mtc.module.nub.NubConfig;
import li.l1t.mtc.module.nub.NubModule;
import li.l1t.mtc.module.nub.api.NubProtection;
import li.l1t.mtc.module.nub.api.ProtectionService;
import li.l1t.mtc.module.nub.service.SimpleProtectionService;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Executes the /nub command which provides management tools to players and administrators.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-09
 */
public class NubCommand extends MTCExecutionExecutor {
    private final Map<UUID, String> pendingConfirmations = new HashMap<>();
    private final ProtectionService service;
    private final LocalProtectionManager manager;
    private final MTCPlugin plugin;
    private final NubConfig config;
    private final XLoginHook xLogin;

    @InjectMe
    public NubCommand(SimpleProtectionService service, LocalProtectionManager manager, MTCPlugin plugin, NubConfig config, XLoginHook xLogin) {
        this.service = service;
        this.manager = manager;
        this.plugin = plugin;
        this.config = config;
        this.xLogin = xLogin;
    }

    @Override
    public boolean execute(BukkitExecution exec) throws UserException, InternalException {
        if (exec.hasArg(0)) {
            switch (exec.arg(0).toLowerCase()) {
                case "status":
                    return handleStatus(exec, exec.player().getUniqueId());
                case "cancel":
                    return handleCancelOwn(exec);
                case "intro":
                    return handleIntro(exec);
                case "outro":
                    return handleOutro(exec);
                case "ostatus":
                    return handleStatus(exec, argumentProfile(exec.arg(1), exec.sender(), "nub ostatus").getUniqueId());
                case "ocancel":
                    return handleCancelOther(exec, argumentProfile(exec.arg(1), exec.sender(), "nub ocancel").getUniqueId());
                case "opause":
                    return handlePause(exec, argumentProfile(exec.arg(1), exec.sender(), "nub opause").getUniqueId());
                case "ostart":
                    return handleStart(exec, argumentProfile(exec.arg(1), exec.sender(), "nub ostart").getUniqueId());
                default:
                    return sendUsageTo(exec);
            }
        } else {
            sendUsageTo(exec);
            handleStatus(exec, exec.player().getUniqueId());
        }
        return true;
    }

    private XLoginHook.Profile argumentProfile(String input, CommandSender sender, String command) {
        return xLogin.findSingleMatchingProfileOrFail(
                input, sender, profile -> String.format("/%s %s", command, profile.getUniqueId())
        );
    }

    private boolean handleIntro(BukkitExecution exec) {
        Integer minutesLeft = manager.getProtection(exec.senderId())
                .map(NubProtection::getMinutesLeft)
                .orElse(0);
        config.getIntro().sendTo(exec.sender(), minutesLeft);
        return true;
    }

    private boolean handleOutro(BukkitExecution exec) {
        if (manager.hasProtection(exec.senderId())) {
            throw new UserException("Du bist noch geschützt. Du wirst das Outro sehen, sobald dein Schutz ausläuft.");
        }
        config.getOutro().sendTo(exec.sender(), 0);
        return true;
    }

    private boolean handleStatus(BukkitExecution exec, UUID playerId) {
        service.showProtectionStatusTo(exec.player(), playerId);
        if (exec.hasPermission(NubModule.ADMIN_PERMISSION)) {
            respondStatusActions(exec, playerId);
        }
        return true;
    }

    private void respondStatusActions(BukkitExecution exec, UUID playerId) {
        exec.respond(
                resultLineBuilder().append("Aktionen: ")
                        .append("[Abbrechen] ", ChatColor.DARK_RED).hintedCommand("/nub ocancel " + playerId)
                        .append("[Pausieren] ", ChatColor.DARK_AQUA).hintedCommand("/nub opause " + playerId)
                        .append("[Aktualisieren]", ChatColor.DARK_GREEN).hintedCommand("/nub ostatus " + playerId)
        );
    }

    private Player getPlayerOrFail(UUID playerId) {
        Player player = plugin.getServer().getPlayer(playerId);
        if (player == null) {
            throw new UserException("Dieser Spieler ist nicht online.");
        }
        return player;
    }

    private void requireIsProtected(Player player, String message) {
        requireIsProtected(player.getUniqueId(), message);
    }

    private void requireIsProtected(UUID playerId, String message) {
        if (!manager.hasProtection(playerId)) {
            throw new UserException(message);
        }
    }

    private boolean handleCancelOther(BukkitExecution exec, UUID playerId) {
        requireAdminPermission(exec);
        Player player = getPlayerOrFail(playerId);
        requireIsProtected(player, "Dieser Spieler ist nicht geschützt.");
        service.cancelProtection(player);
        exec.respond(MessageType.RESULT_LINE_SUCCESS, "Der Schutz von §p%s§s wurde aufgehoben.", player.getName());
        return true;
    }

    private void requireAdminPermission(BukkitExecution exec) {
        exec.requirePermission(NubModule.ADMIN_PERMISSION);
    }

    private boolean handleCancelOwn(BukkitExecution exec) {
        requireIsProtected(exec.player(), "Du bist nicht durch N.u.b. geschützt.");
        return handleCancelInternal(exec, exec.player());
    }

    private boolean handleCancelInternal(BukkitExecution exec, Player player) {
        if (!pendingConfirmations.containsKey(player.getUniqueId())) {
            exec.respond(MessageType.WARNING, "Wenn du fortfährst, wird dein N.u.b.-Schutz");
            exec.respond(MessageType.WARNING, "beendet! Das kann nicht rückgängig gemacht werden.");
            exec.respond(MessageType.RESULT_LINE, "Damit kannst du zwar andere Spieler sofort schlagen,");
            exec.respond(MessageType.RESULT_LINE, "aber kannst auch sofort selbst geschlagen werden.");
            exec.respond(MessageType.RESULT_LINE, "Dadurch verlierst du deinen Startvorteil!");
            exec.respond(MessageType.RESULT_LINE, "§cFahre nur fort, wenn du weißt, was du tust.");
            exec.respond(MessageType.RESULT_LINE, "Um deinen Schutz unwiderruflich zu beenden,");
            String confirmationCode = StringHelper.alphanumericString(4, false).toUpperCase();
            pendingConfirmations.put(player.getUniqueId(), confirmationCode);
            exec.respond(MessageType.RESULT_LINE, "tippe §s/nub cancel %s", confirmationCode);
        } else if (!exec.hasArg(1)) {
            throw new UserException("Bitte gib den Bestätigungscode an oder tippe /nub cancel abort für einen neuen Code");
        } else if (exec.arg(1).equalsIgnoreCase("abort")) {
            pendingConfirmations.remove(player.getUniqueId());
            exec.respond(MessageType.RESULT_LINE_SUCCESS, "Beendigung abgebrochen.");
        } else {
            String inputCode = exec.arg(1);
            if (pendingConfirmations.remove(player.getUniqueId(), inputCode.toUpperCase())) {
                service.cancelProtection(player);
            } else {
                throw new UserException("Falscher Bestätigungscode!");
            }
        }
        return true;
    }

    private boolean handleStart(BukkitExecution exec, UUID playerId) {
        requireAdminPermission(exec);
        Player player = getPlayerOrFail(playerId);
        service.startProtection(player);
        exec.respond(MessageType.RESULT_LINE_SUCCESS, "%s ist jetzt geschützt.", player.getName());
        return true;
    }

    private boolean handlePause(BukkitExecution exec, UUID playerId) {
        requireAdminPermission(exec);
        requireIsProtected(playerId, "Dieser Spieler ist nicht geschützt.");
        Player player = getPlayerOrFail(playerId);
        service.pauseProtection(player);
        exec.respond(MessageType.RESULT_LINE_SUCCESS, "Der Schutz von %s ist jetzt pausiert.", player.getName());
        return true;
    }

    private boolean sendUsageTo(BukkitExecution exec) {
        exec.respondUsage("status", "", "Zeigt den Status deines Schutzes");
        exec.respondUsage("cancel", "", "Bricht deinen Schutz permanent ab");
        exec.respondUsage("intro", "", "Zeigt das Intro nochmal");
        exec.respondUsage("outro", "", "Zeigt das Outro nochmal");
        if (exec.hasPermission(NubModule.ADMIN_PERMISSION)) {
            exec.respondUsage("ostatus", "<Spieler|UUID>", "Zeigt den Status eines Schutzes");
            exec.respondUsage("ocancel", "<Spieler|UUID>", "Bricht den Schutz ab");
            exec.respondUsage("opause", "<Spieler|UUID>", "Pausiert den Schutz");
            exec.respondUsage("ostart", "<Spieler|UUID>", "Startet neuen Schutz");
        }
        return true;
    }
}
