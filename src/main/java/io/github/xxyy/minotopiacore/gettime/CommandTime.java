package io.github.xxyy.minotopiacore.gettime;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.helper.MTCHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class CommandTime implements CommandExecutor{

	@Override
	//onCommand(sender,command,label(Comamnd that was typed by user),args
	//reacts to command
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
		if(!MTCHelper.isEnabledAndMsg(".command.gtime", sender)) {
            return true;
        }
		if(!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.gtime", label)) {
            return true;
        }
		if(args.length != 0){
			sender.sendMessage("§7[MTS][Info]Dieser Befehl ben§tigt keine Argumente!");
		}
		sender.sendMessage(ChatColor.GOLD + "============={§7Serverzeit"+ChatColor.RESET+ChatColor.GOLD+"}=============");
		if(CommandHelper.checkActionPermAndMsg(sender, "mtc.cmd.gtime.time", "Die RL-Serverzeit anzeigen")) {
            sender.sendMessage(ChatColor.GOLD + "Aktuelle RL-Serverzeit: " + ChatColor.BLUE + (new SimpleDateFormat("HH:mm:ss")).format(Calendar.getInstance().getTime()));
        }
		if(!label.equalsIgnoreCase("gd") && !label.equalsIgnoreCase("getdate") && CommandHelper.checkActionPermAndMsg(sender, "mtc.cmd.gtime.date", "Das Serverdatum anzeigen")) {
            sender.sendMessage(ChatColor.GOLD + "Aktuelles Serverdatum: " + ChatColor.BLUE + (new SimpleDateFormat("dd.MM.yyyy")).format(Calendar.getInstance().getTime()));
        }
		sender.sendMessage(ChatColor.GOLD + "============={§7Serverzeit"+ChatColor.RESET+ChatColor.GOLD+"}=============");
		return true;
	}
	
}
