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

package li.l1t.mtc.misc.cmd;

import li.l1t.common.util.CommandHelper;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.helper.MTCHelper;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public final class CommandRandom extends MTCCommandExecutor {

    private final MTCPlugin plugin;

    public CommandRandom(MTCPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
        if (!CommandHelper.checkPermAndMsg(sender, "mtc.random", label)) {
            return true;
        }

        Collection<? extends Player> plrs = new ArrayList<>(Bukkit.getOnlinePlayers());

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "-p":
                    if (args.length < 2) {
                        return MTCHelper.sendLoc("XU-rdmhelp", sender, false);
                    }
                    Bukkit.getScheduler().runTaskLater(plugin, new RunnableAnnounceChoice(args[1]),
                            plugin.getConfig().getLong("random.tickdelay", 100));
                    return true;
                case "-m":
                    if (args.length < 2) {
                        return MTCHelper.sendLoc("XU-rdmhelp", sender, false);
                    }
                    plrs.removeIf(target -> !target.hasPermission(args[1]));
                    break;
                default:
                    return MTCHelper.sendLoc("XU-rdmhelp", sender, false);
            }
        }

        if (plrs.size() == 0) {
            return MTCHelper.sendLoc("XU-nordmplrs", sender, true);
        }
        Player chosenOne = plrs.stream()
                .skip(RandomUtils.nextInt(plrs.size()))
                .findFirst().get();

        Bukkit.getScheduler().runTaskLater(plugin, new RunnableAnnounceChoice(chosenOne.getName()),
                plugin.getConfig().getLong("random.tickdelay", 100));

        return true;
    }

    private class RunnableAnnounceChoice implements Runnable {
        private final String plrName;

        RunnableAnnounceChoice(String plrName) {
            Bukkit.broadcastMessage(MTCHelper.locArgs("XU-randomplr", "CONSOLE", true, plrName));
            this.plrName = plrName;
        }

        @Override
        public void run() {
            Bukkit.broadcastMessage(MTCHelper.locArgs("XU-chosenone", "CONSOLE", true, this.plrName));
        }

    }

}
