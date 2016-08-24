/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.globalmute;

import li.l1t.common.util.StringHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

/**
 * A command to toggle global mute, with an optional reason.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public class CommandGlobalMute implements CommandExecutor {
    private final GlobalMuteModule module;

    public CommandGlobalMute(GlobalMuteModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String reason = StringHelper.varArgsString(args, 0, true);
        module.toggleGlobalMute(reason);
        Bukkit.broadcastMessage(formatBroadcastMessage());
        return true;
    }

    @Nonnull
    private String formatBroadcastMessage() {
        return module.getPlugin().getChatPrefix() +
                "GlobalMute wurde " + formatActivatedState() + formatReasonSuffix();
    }

    @Nonnull
    private String formatActivatedState() {
        return module.isGlobalMute() ? "aktiviert." : "deaktiviert.";
    }

    private String formatReasonSuffix() {
        if (module.getGlobalMuteReason().isEmpty()) {
            return "";
        }
        return " §aGrund: " + module.getGlobalMuteReason();
    }
}
