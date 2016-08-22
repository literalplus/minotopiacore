/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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