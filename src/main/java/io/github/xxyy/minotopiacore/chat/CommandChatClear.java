package io.github.xxyy.minotopiacore.chat;

import io.github.xxyy.common.localisation.LangHelper;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.MTC;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandChatClear implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
		if(!CommandHelper.checkPermAndMsg(sender, "mtc.chatclear", label)) {
            return true;
        }
		for(Player plr:Bukkit.getOnlinePlayers()){
			if(plr.hasPermission("mtc.chatclear.exempt")){
				plr.sendMessage(String.format(LangHelper.localiseString("XU-ccex", plr.getName(), MTC.instance().getName()),sender.getName()));
				continue;
			}
			for(int i = 0;i < 200;i++){
				plr.sendMessage("  ");
			}
		}
		String msg = LangHelper.localiseString("XU-ccglo", "CONSOLE", MTC.instance().getName());
		if(args.length >= 1){
			msg += " §bGrund: §7";
			for(int i = 0; i < args.length; i++){
				msg += (i == 0 ? "" : " ")+args[i];
			}
		}
		Bukkit.broadcastMessage(msg);
		return true;
	}

}
