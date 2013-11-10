package io.github.xxyy.minotopiacore.warns;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.bans.BanHelper;
import io.github.xxyy.minotopiacore.bans.BanInfo;

import java.text.SimpleDateFormat;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class CommandListWarns implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!CommandHelper.checkPermAndMsg(sender, "mtc.warns.list", label)) return true;
		if(args.length == 0){
			listWarnsOfTo(sender, sender.getName()); return true;
		}else if(args.length >= 1 && !args[0].equalsIgnoreCase("help")){
			if(!sender.hasPermission("mtc.warns.list.others")){
				sender.sendMessage(MTC.warnChatPrefix+"§cDu darfst nur deine eigenen Warns ansehen! /warns"); return true;
			}
			listWarnsOfTo(sender, args[0]); return true;
		}else{
			sender.sendMessage("§cVerwendung: §6/warns §7Zeigt deine Warns.");
			sender.sendMessage("§cVerwendung: §6/warns <Spieler> §7Zeigt die Warns von <Spieler>.");
			sender.sendMessage("§cVerwendung: §6/warns help §7Zeigt diese Hilfe.");
		}
		return true;
	}
	public void listWarnsOfTo(CommandSender sender, String targetName){
		sender.sendMessage("§6==== "+WarnHelper.getWarnCountByPlayerName(targetName)+" §bWarnungen von "+targetName+" §6====");
		String bannedMsg = "§6gebannt? §anein";
		if(BanHelper.isBanned(targetName)){
			BanInfo bi = BanHelper.getBanInfoByPlayerName(targetName);
			if(bi.id < 0) bannedMsg = "§cgebannt, Fehler: "+bi.id;
			else bannedMsg = "§cgebannt: §b"+bi.reason+"§7 Mehr Info: /baninfo "+targetName;
		}
		sender.sendMessage(bannedMsg);
		List<WarnInfo> warns = WarnHelper.getWarnsByPlayerName(targetName,true);
		byte i = 1;
		for(WarnInfo wi : warns){//ordered by ID in WarnInfo method
			if(wi.id <= 0){ 
				if(wi.id == -4){sender.sendMessage("§6Du hast keine Warnungen =)"); return; }
				sender.sendMessage("§4Fehler: "+wi.id); continue;
			}
			String m = (wi.status == 0) ? "" : "§m";//strikethrough if invalid
			sender.sendMessage(((wi.status == 2) ? "§7§c!" : "")+"§b§l"+m+"#"+i+" §6"+m+"von §c"+m+wi.warnedByName+"§6"+m+" am §c"+m+(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(wi.timestamp*1000L)+"§7§o"+m+" (uid: "+wi.id+")")
					+"\n  §e"+m+ChatColor.translateAlternateColorCodes('&', wi.reason));
			i++;
			if(i >= 20){
				sender.sendMessage("§9[continued]"); break;
			}
		}
	}
}
