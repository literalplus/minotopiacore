package io.github.xxyy.minotopiacore.chat;

import io.github.xxyy.common.util.ChatHelper;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.MTC;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class CommandGlobalMute implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
		if(!CommandHelper.checkPermAndMsg(sender, "mtc.globalmute.toggle", label)) return true;
		ChatHelper.isGlobalMute = !ChatHelper.isGlobalMute;
		String msg = MTC.chatPrefix+"GlobalMute wurde "+((ChatHelper.isGlobalMute) ? "" : "de")+"aktiviert!";
		String reason = "";
		if(args.length >= 1){
			reason = " §bGrund: §7";
			for(int i = 0; i < args.length; i++){
				reason += (i == 0 ? "" : " ")+args[i];
			}
		}
		ChatHelper.gloMuReason = reason;
		Bukkit.broadcastMessage(msg+reason);
		return true;
	}

}
