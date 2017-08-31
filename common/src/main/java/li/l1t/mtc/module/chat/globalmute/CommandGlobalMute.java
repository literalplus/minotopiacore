/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
