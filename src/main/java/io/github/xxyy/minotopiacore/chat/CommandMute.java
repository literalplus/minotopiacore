package io.github.xxyy.minotopiacore.chat;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.MTC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;


public final class CommandMute implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
		if(args.length >= 1 && !(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("info"))){
			if(!CommandHelper.checkPermAndMsg(sender, "mtc.mute", label)) {
                return true;
            }
			Player target = Bukkit.getPlayerExact(args[0]);
//			boolean targetOnline = (target != null && target.isOnline());
			String reason = "";
			for(int i = 1;i < args.length; i++){
				reason += (i == 1 ? "" : " ")+args[i];
			}
			if(MuteHelper.isPlayerMuted(args[0])){
				MuteHelper.unmutePlayer(args[0]);
				if(target != null && target.isOnline())
                {
                    target.sendMessage(MTC.chatPrefix+"Du wurdest von §b"+sender.getName()+"§6 entmuted."+(reason.equalsIgnoreCase("") ? "" : " Grund: §7"+reason));
                }
				sender.sendMessage(MTC.chatPrefix+"Du hast §b"+args[0]+"§6 entmuted."+(reason.equalsIgnoreCase("") ? "" : " Grund: §7"+reason));
				CommandHelper.broadcast(MTC.chatPrefix+"§b"+sender.getName()+"§6 hat §b"+args[0]+"§6 entmuted."+(reason.equalsIgnoreCase("") ? "" : " Grund: §7"+reason), "mtc.spy");
				return true;
			}
            MuteHelper.mutePlayer(args[0], (reason.equals("") ? "Kein Grund angegeben. Das ist gegen unsere Standards." : reason), sender.getName());
            if(target != null && target.isOnline())
            {
                target.sendMessage(MTC.chatPrefix+"Du wurdest von §b"+sender.getName()+"§6 gemuted."+(reason.equalsIgnoreCase("") ? "" : " Grund: §7"+reason));
            }
            sender.sendMessage(MTC.chatPrefix+"Du hast §b"+args[0]+"§6 gemuted."+(reason.equalsIgnoreCase("") ? "" : " Grund: §7"+reason));
            CommandHelper.broadcast(MTC.chatPrefix+"§b"+sender.getName()+"§6 hat §b"+args[0]+"§6 gemuted."+(reason.equalsIgnoreCase("") ? "" : " Grund: §7"+reason), "mtc.spy");
            return true;
		}else if(args.length >= 1 && args[0].equalsIgnoreCase("list")){
			if(!CommandHelper.checkPermAndMsg(sender, "mtc.mute", label)) {
                return true;
            }
			Set<String> paths = MuteHelper.getMutedPlayerPaths();
			if(paths == null || paths.size() == 0){
				sender.sendMessage(MTC.chatPrefix+"Es sind keine Spieler gemuted!");
				return true;
			}
			int pageNum = 1;
			if(args.length == 2){
				try {
					pageNum = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					sender.sendMessage(MTC.chatPrefix+"Die Seitenzahl '"+args[1]+"' ist keine Zahl! §b/mute list <Seite>");
					return true;
				}
			}
			sender.sendMessage("§6------ §bGemutete Spieler - Seite "+pageNum+" §6------");
			int i = 1;
			pageNum--;
			for(String path : paths){
				if(i <= pageNum*5 || i > (pageNum+1)*5){
					i++; continue;
				}
				sender.sendMessage("§b#"+i+": §6"+path+"§b "+MuteHelper.getMuteTimeByPath(path)+"§6 - §b von §6"+MuteHelper.getMuterByPath(path));
				sender.sendMessage("   §bwegen: §e"+MuteHelper.getReasonByPath(path));
				i++;
			}
			if((pageNum+1)*5 < paths.size())
            {
                sender.sendMessage("§6Nächste Seite: §b/mute list "+(pageNum+2));
            }
			return true;
		}else if(args.length >= 1 && args[0].equalsIgnoreCase("info")){
			String tgtName = args[1];
			if(args.length < 2)
            {
                args[1] = sender.getName();
            }
			if(MuteHelper.isPlayerMuted(tgtName)){
				sender.sendMessage("§6------ §bMuteInfo: "+tgtName+"§6 ------");
				sender.sendMessage("§6gemutet: §aja");
				sender.sendMessage("§6Grund: §e"+MuteHelper.getReasonByPath(tgtName));
				sender.sendMessage("§6gemutet von: §b"+MuteHelper.getMuterByPath(tgtName));
				sender.sendMessage("§6Timestamp: §e"+MuteHelper.getMuteTimeByPath(tgtName));
			}else{
				sender.sendMessage("§6Der Spieler §b"+tgtName+"§6 ist §lnicht §6gemutet.");
				return true;
			}
		}
		else{
			if(!CommandHelper.checkPermAndMsg(sender, "mtc.mute", label)) {
                return true;
            }
			sender.sendMessage("§cUnbekannte Aktion. Verwendung:");
			sender.sendMessage("§b/mute <Spieler> [Grund] §6Mutet/Entmutet einen Spieler, Grund ist optional");
			sender.sendMessage("§b/mute info <Spieler> §6Zeigt Muteinformationen zu einem Spieler");
			sender.sendMessage("§b/mute list [Seite] §6Listet alle aktiven Mutes auf");
		}
		return true;
	}

}
