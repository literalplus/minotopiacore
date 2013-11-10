package io.github.xxyy.minotopiacore.chat;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.MTC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class CommandCmdSpy implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!CommandHelper.checkPermAndMsg(sender, "mtc.cmdspy", label)) return true;
		if(CommandHelper.kickConsoleFromMethod(sender, label)) return true;
		
		String sdrName = sender.getName().toLowerCase();
		
		if(args.length >= 1 && args[0].equalsIgnoreCase("help")) return this.printHelpTo(sender);
        else if(args.length >= 2 && args[0].equalsIgnoreCase("-i")){
			if(MTCChatHelper.indCmdSpies.containsKey(args[1].toLowerCase()) && MTCChatHelper.indCmdSpies.get(args[1].toLowerCase()).equals(sdrName)){
				MTCChatHelper.indCmdSpies.remove(args[1].toLowerCase());
				sender.sendMessage(MTC.chatPrefix+"CommandSpy -i deaktiviert!");
			}else{
				MTCChatHelper.indCmdSpies.put(args[1].toLowerCase(), sdrName);
				sender.sendMessage(MTC.chatPrefix+"CommandSpy -i aktiviert!");
			}
		}else if(args.length >= 2 && args[0].equalsIgnoreCase("-p")){
			if(MTCChatHelper.plrCmdSpies.containsKey(args[1].toLowerCase()) && MTCChatHelper.plrCmdSpies.get(args[1].toLowerCase()).equals(sdrName)){
				MTCChatHelper.plrCmdSpies.remove(args[1].toLowerCase());
				sender.sendMessage(MTC.chatPrefix+"CommandSpy -p deaktiviert!");
			}else{
				MTCChatHelper.plrCmdSpies.put(args[1].toLowerCase(), sdrName);
				sender.sendMessage(MTC.chatPrefix+"CommandSpy -p aktiviert!");
			}
		}else if(args.length >= 1 && args[0].equalsIgnoreCase("-c")){
			MTCChatHelper.cmdSpies.remove(sender.getName().toLowerCase());
			if(MTCChatHelper.indCmdSpies.containsValue(sdrName))
            {
                MTCChatHelper.indCmdSpies.values().remove(sdrName);
            }
			if(MTCChatHelper.plrCmdSpies.containsValue(sdrName))
            {
                MTCChatHelper.plrCmdSpies.values().remove(sdrName);
            }
			sender.sendMessage(MTC.chatPrefix+"CommandSpy deaktiviert (forced!)!");
		}else if(args.length == 0){
			if(MTCChatHelper.cmdSpies.contains(sender.getName().toLowerCase())){
				MTCChatHelper.cmdSpies.remove(sender.getName());
				sender.sendMessage(MTC.chatPrefix+"CommandSpy deaktiviert!");
			}else{
				MTCChatHelper.cmdSpies.add(sender.getName().toLowerCase());
				sender.sendMessage(MTC.chatPrefix+"CommandSpy aktiviert!");
			}
		}
        else return this.printHelpTo(sender);
		
		return true;
	}
	
	public boolean printHelpTo(CommandSender sender){
		sender.sendMessage("§e/cmdspy §6Aktiviert CommandSpy.");
		sender.sendMessage("§e/cmdspy -i <CMD> §6Aktiviert CommandSpy für einen einzelnen Befehl.");
		sender.sendMessage("§e/cmdspy -p <SPIELER> §6Aktiviert CommandSpy für einen bestimmten Spieler.");
		sender.sendMessage("§e/cmdspy -c §6Deaktiviert CommandSpy (force)");
		sender.sendMessage("§cFür -i: OHNE FÜHRENDEN SLASH");
		return true;
	}
}
