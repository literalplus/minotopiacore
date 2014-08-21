package io.github.xxyy.mtc.listener;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.ConfigHelper;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.helper.MTCHelper;
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

        if (plugin.getLogoutHandler().isFighting(plrName) && !ConfigHelper.getFightAllowedCmds().contains(cmd) &&
                plugin.getWorldGuardHook().isPvP(e.getPlayer().getLocation())) {
            MTCHelper.sendLocArgs("XU-fightcmd", e.getPlayer(), true, CommandHelper.CSCollection(ConfigHelper.getFightAllowedCmds(), "nichts :/"));
            e.setCancelled(true);
            return;
        }
        if (cmd.equalsIgnoreCase("stop") || cmd.equalsIgnoreCase("restart")) {
            plugin.getLogoutHandler().clearFighters(); //TODO what is this doing here?
        }
    }
}
