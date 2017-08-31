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

package li.l1t.mtc.listener;

import li.l1t.common.util.CommandHelper;
import li.l1t.mtc.ConfigHelper;
import li.l1t.mtc.MTC;
import li.l1t.mtc.helper.MTCHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


public final class MainCommandListener implements Listener {
    private final MTC plugin;

    public MainCommandListener(MTC plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCmdBoatOrSpy(PlayerCommandPreprocessEvent e) {

        String plrName = e.getPlayer().getName();
        String cmd = e.getMessage();
        cmd = cmd.substring(1, cmd.contains(" ") ? cmd.indexOf(' ') : cmd.length());

        if (e.getPlayer().isInsideVehicle() && ConfigHelper.isProhibitCmdsInBoats() &&
                !ConfigHelper.getVehicleAllowedCmds().contains(cmd)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(MTC.chatPrefix + "Du kannst in einem Vehikel keine Befehle ausführen!");
            return;
        }

        if (plugin.getLogoutHandler().isFighting(e.getPlayer().getUniqueId()) && !ConfigHelper.getFightAllowedCmds().contains(cmd) &&
                plugin.getWorldGuardHook().isPvP(e.getPlayer().getLocation())) {
            MTCHelper.sendLocArgs("XU-fightcmd", e.getPlayer(), true, CommandHelper.CSCollection(ConfigHelper.getFightAllowedCmds(), "nichts :/"));
            e.setCancelled(true);
            return;
        }
        if ((cmd.equalsIgnoreCase("stop") && e.getPlayer().hasPermission("bukkit.command.stop"))
                || (cmd.equalsIgnoreCase("restart") && e.getPlayer().hasPermission("bukkit.command.restart"))) {
            plugin.getLogoutHandler().clearFighters(); //TODO what is this doing here?
        }
    }
}
