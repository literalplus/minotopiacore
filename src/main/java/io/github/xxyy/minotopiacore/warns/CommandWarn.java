package io.github.xxyy.minotopiacore.warns;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.LogHelper;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.misc.BungeeHelper;

import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandWarn implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!CommandHelper.checkPermAndMsg(sender, "mtc.warns.warn", label)) return true;
		if(args.length >= 2 && !args[0].equalsIgnoreCase("help")){
			byte multiplier = 1;//AKA how many warns will be given
			byte reasonStartIndex = 1;
			if(StringUtils.isNumeric(args[1])){
				try {
					multiplier = Byte.parseByte(args[1]);
				} catch (NumberFormatException e) {
					sender.sendMessage(MTC.warnChatPrefix+"Das ist keine Zahl...aber irgendwie schon...ZAHLCEPTION! (zu gross?)"); return true;
				}
				reasonStartIndex = 2;
				if(args.length < 3){ sender.sendMessage("§c/warn <Spieler> [Anzahl] <Grund>"); return true; }
			}
			if(multiplier > 15){ sender.sendMessage(MTC.warnChatPrefix+"Das sind aber viele Warns...Auf 15 reduziert."); multiplier = 1; }
			Player plr = Bukkit.getPlayerExact(args[0]);
			if(plr == null){
				OfflinePlayer oPlr = Bukkit.getOfflinePlayer(args[0]);
				if(oPlr == null){
					sender.sendMessage(MTC.warnChatPrefix+"Dieser Spieler ist dem System nicht bekannt.");
				}else{
					sender.sendMessage(MTC.warnChatPrefix+"Dieser Spieler ist nicht online.");
				}
			}
			String reason = args[reasonStartIndex];
			if(args.length > (reasonStartIndex+1)){
				StringBuilder sb = new StringBuilder();
				for(byte i = (byte) (reasonStartIndex + 1);i < args.length;i++){
					sb.append(" "+args[i]);
				}
				reason += sb.toString();
			}
			reason = ChatColor.translateAlternateColorCodes('&', reason);
			reason += " "+MTC.instance().warnBanServerSuffix;
			if(WarnHelper.playerTimeouts.contains(args[0].toLowerCase())){ 
				sender.sendMessage(MTC.warnChatPrefix+"Bitte warte 20 Sekunden, bevor du diesen Spieler erneut warnen kannst."); return true;
			}
			CommandHelper.broadcast(MTC.warnChatPrefix+"§6"+args[0]+"§c wurde von §6"+sender.getName()+"§c gewarnt.", "mtc.warns.adminmsg");
			for(byte i = 0;i < multiplier;i++){
				BungeeHelper.notifyServersWarn(WarnHelper.addWarn(args[0], sender, reason, (byte)0));
			}
			if (!MTC.instance().getConfig().getBoolean("enable.bungeeapi",true)) {
				Bukkit.broadcastMessage(MTC.warnChatPrefix + "§e" + args[0] + "§c wurde "
						+ ((multiplier == 1) ? "" : multiplier + "mal ") + "gewarnt. Grund:");
				Bukkit.broadcastMessage(MTC.warnChatPrefix + "§c=>§6"
						+ ChatColor.translateAlternateColorCodes('&', reason) + "§c<=");
			}
			WarnHelper.checkWarnNumberAndDoStuff(args[0], sender, (multiplier));
			WarnHelper.playerTimeouts.add(args[0]);
			Bukkit.getScheduler().runTaskLater(MTC.instance(), new RunnableResetTimeout(args[0]), 400);
			LogHelper.getWarnLogger().log(Level.INFO, sender.getName()+" WARNED: "+args[0]+" TIMES: "+multiplier+" COZ: "+reason);
		}else{
			sender.sendMessage("§c/warn <Spieler> [Anzahl] <Grund>");
		}
		return true;
	}

}
