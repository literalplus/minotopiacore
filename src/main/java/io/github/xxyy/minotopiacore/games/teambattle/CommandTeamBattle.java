package io.github.xxyy.minotopiacore.games.teambattle;

import io.github.xxyy.common.HelpManager;
import io.github.xxyy.minotopiacore.helper.MTCHelper;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandTeamBattle implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,String label, String[] args) {
		if(!MTCHelper.isEnabledAndMsg(".command.war", sender)) {
            return true;
        }
		if(args.length == 0){
			HelpManager.tryPrintHelp("war", sender, label, "", label+" help");
		}else if(args.length == 1){//join, leave
			CommandTeamBattleHelper helper=new CommandTeamBattleHelper(sender,args,label);
			switch(args[0]){
				case "join":
					return helper.prepareJoinGame();
				case "leave":
					return helper.leaveGame();
				case "help":
					HelpManager.tryPrintHelp("war", sender, label, "", label+" help");
					break;
				case "list":
					return helper.listPlayersInGame();
				case "lobby":
					return helper.prepareJoinLobby();
				case "prev":
					return helper.tpToPrevLocFromFl();
				default:
					sender.sendMessage(TeamBattle.chatPrefix+" Unbekannte Aktion. Hilfe:");
					HelpManager.tryPrintHelp("war", sender, label, "", label+" help");
			}
		}else if(args.length == 2){//setspawn (red|blue) setkit (red|blue)
			switch(args[0]){
				case "help":
					HelpManager.tryPrintHelp("war", sender, label, args[1], label+" help");
					break;
				case "lobby":
					if(args[1].equalsIgnoreCase("leave")||args[1].equalsIgnoreCase("l")) {
                        return (new CommandTeamBattleHelper(sender, args, label)).leaveLobby();
                    }
                return (new CommandTeamBattleHelper(sender,args,label)).prepareJoinLobby();
				case "prev":
					if(!(sender instanceof Player))
                    {
                        sender.sendMessage("§7Nur für Spieler! :P");
                    }
					if(args[1].equalsIgnoreCase("clear")){
						TeamBattle.leaveMan.clearLocation(sender.getName());
						sender.sendMessage(TeamBattle.chatPrefix+" Deine vorherige Position wurde gel§scht.");
					}
					break;
				default:
					HelpManager.tryPrintHelp("war", sender, label, "", label+" help");
			}
		}else{//unknown lenght
			sender.sendMessage(TeamBattle.chatPrefix+" Unbekannte Aktion. Hilfe:");
			HelpManager.tryPrintHelp("war", sender, label, "", label+" help");
		}
		return true;
	}
	
}
