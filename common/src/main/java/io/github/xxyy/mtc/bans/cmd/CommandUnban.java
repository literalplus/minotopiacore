package io.github.xxyy.mtc.bans.cmd;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.LogHelper;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.bans.BanHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;


public final class CommandUnban implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!CommandHelper.checkPermAndMsg(sender, "mtc.ban.unban", label)) {
            return true;
        }
		if(args.length >= 1 && !args[0].equalsIgnoreCase("help")){
			BanHelper.deleteBan(args[0].toLowerCase());
			CommandHelper.broadcast(MTC.banChatPrefix+args[0]+"§a wurde von §6"+sender.getName()+"§a entbannt.", "mtc.ban.adminmsg");
			sender.sendMessage(MTC.banChatPrefix+"§aDu hast §6"+args[0]+"§a entbannt.");
			LogHelper.getBanLogger().log(Level.WARNING, sender.getName()+" UNBANNED: "+args[0]);
		}else{
			sender.sendMessage("§6Verwendung: §b/unban <Spieler>");
		}
		return true;
	}

}
