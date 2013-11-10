package io.github.xxyy.minotopiacore.listener;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.ConfigHelper;
import io.github.xxyy.minotopiacore.LogHelper;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.chat.MTCChatHelper;
import io.github.xxyy.minotopiacore.helper.MTCHelper;
import io.github.xxyy.minotopiacore.helper.PluginAPIInterfacer;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


public class MainCommandListener implements Listener {
	@EventHandler(priority=EventPriority.LOW)
	public void onCmdBoatOrSpy(PlayerCommandPreprocessEvent e){
		
		String plrName = e.getPlayer().getName();
		String cmd = e.getMessage();
		if(cmd.contains(" ")) {
            cmd = e.getMessage().replaceAll("/((.)+?)\\s+.*", "$1");
        } else {
            cmd = cmd.substring(1,cmd.length());
        }
		
		if(e.getPlayer().isInsideVehicle() && ConfigHelper.isProhibitCmdsInBoats() &&
		        !ConfigHelper.getVehicleAllowedCmds().contains(cmd)){
		    e.setCancelled(true);
		    e.getPlayer().sendMessage(MTC.chatPrefix+"Du kannst in einem Vehikel keine Befehle ausführen!");
		    return;
		}
		
		if(AntiLogoutListener.isInAnyFight(plrName) && !ConfigHelper.getFightAllowedCmds().contains(cmd) &&
		        PluginAPIInterfacer.isPvPEnabledAt(e.getPlayer().getLocation())){
		    MTCHelper.sendLocArgs("XU-fightcmd", e.getPlayer(), true, CommandHelper.CSCollection(ConfigHelper.getFightAllowedCmds(), "nichts :/"));
		    e.setCancelled(true);
		    return;
		}
		if(cmd.equalsIgnoreCase("stop") || cmd.equalsIgnoreCase("restart")){
		    AntiLogoutListener.playersInAFight.clear();
		}
		    
		if(!cmd.equalsIgnoreCase("login") && !cmd.equalsIgnoreCase("register") && MTCChatHelper.indCmdSpies.containsKey(cmd.toLowerCase())){
			Player plr = Bukkit.getPlayerExact(MTCChatHelper.indCmdSpies.get(cmd.toLowerCase()));
			if(plr != null){
				plr.sendMessage("§9[CmdSpy]§7"+plrName+": §o"+e.getMessage());
			}else{
				MTCChatHelper.indCmdSpies.remove(cmd);
			}
		}
		if(!cmd.equalsIgnoreCase("login") && !cmd.equalsIgnoreCase("register") && MTCChatHelper.plrCmdSpies.containsKey(plrName.toLowerCase())){
			Player plr = Bukkit.getPlayerExact(MTCChatHelper.plrCmdSpies.get(plrName.toLowerCase()));
			if(plr != null){
				plr.sendMessage("§e[CmdSpy]§7"+plrName+": §o"+e.getMessage());
			}
		}
		if(ConfigHelper.getBadCmds().contains(cmd)){
			CommandHelper.broadcast("§4[CmdSpy]§c"+plrName+": §7§o"+e.getMessage(), "mtc.cmdspy");
			LogHelper.getBadCmdLogger().log(Level.INFO, plrName+"("+e.getPlayer().getAddress()+"): "+e.getMessage());
		}else if(cmd.equalsIgnoreCase("login")){
			MTCChatHelper.sendCommandSpyMsg("§8[CmdSpy]§7"+plrName+": §o/login *******");
		}else if(cmd.equalsIgnoreCase("register")){
			MTCChatHelper.sendCommandSpyMsg("§8[CmdSpy]§7"+plrName+": §o/register ******* *******");
		}else{
			MTCChatHelper.sendCommandSpyMsg("§8[CmdSpy]§7"+plrName+": §o"+e.getMessage());
		}
		LogHelper.getCmdLogger().log(Level.INFO, plrName+"("+e.getPlayer().getAddress()+"): "+e.getMessage());
	}
}
