package io.github.xxyy.minotopiacore.chat;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.clan.ClanHelper;
import io.github.xxyy.minotopiacore.clan.ClanInfo;
import io.github.xxyy.minotopiacore.clan.ClanMemberInfo;
import me.minotopia.xLogin.xLogin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandChatFarbe implements CommandExecutor {

	/**
     * @param args Needed for consistent method signature.
     */
	public void changeChatFarbeWMsgTo(CommandSender sender, String[] args, String cf, String setMsg, OfflinePlayer target){
	    String targetName = target.getName();
		if(cf.length() > 20){
			sender.sendMessage(MTC.chatPrefix+"Die Chatfarbe darf maximal 20 Zeichen haben!");
			return;
		}
		MTCChatHelper.setChatColorByName(targetName, cf);
		 sender.sendMessage(MTC.chatPrefix+setMsg);
		String pexUserPrefix = ChatColor.translateAlternateColorCodes('&', (MTC.chat == null) ? "§7[Fehler]" : (target.isOnline()) ? MTC.chat.getPlayerPrefix(target.getPlayer()) : MTC.chat.getPlayerPrefix(Bukkit.getWorlds().get(0),target.getName()));
		String clanTag = "";
		if(ClanHelper.isInAnyClan(targetName)){
		    ClanMemberInfo cmi = ClanHelper.getMemberInfoByPlayerName(targetName);
		    ClanInfo ci = ClanHelper.getClanInfoById(cmi.clanId);
		    if(ci.id > 0){
		        clanTag = ClanHelper.getFormattedPrefix(ci);
		    }
		}
		String color = (target.isOnline() ? MTCChatHelper.getFinalChatColorByCSender(target.getPlayer()) : MTCChatHelper.getFinalChatColorByNameIgnorePerms(target.getName()));
		sender.sendMessage("§6 => §r"+pexUserPrefix+" "+clanTag+"§7"+target.getName()+"§7:§f "+color+"cogito ergo sum");
		return;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
		if(!CommandHelper.checkPermAndMsg(sender, "mtc.chatfarbe.change", label)) return true;
		
		if(args.length == 0){
			if(CommandHelper.kickConsoleFromMethod(sender, label)) return true;
			CommandChatFarbe.printChatFarbeTo(sender, (Player)sender);
			return true;
		}else if(args.length >= 1 && args[0].equalsIgnoreCase("help")){
			sender.sendMessage("§6###§b/chatfarbe§6###");
			sender.sendMessage("§b/chatfarbe §6Zeigt deine Chatfarbe.");
			sender.sendMessage("§b/chatfarbe <neue Chatfarbe> §6Setzt deine Chatfarbe.");
			if(sender.hasPermission("mtc.chatfarbe.player")){
				sender.sendMessage("§b/chatfarbe player <Spieler> §6Zeigt die Chatfarbe eines Spielers.");
				sender.sendMessage("§b/chatfarbe player <Spieler> <neue Chatfarbe> §6Setzt die Chatfarbe eines Spielers.");
			}
			return true;
		}else if(args.length >= 2 && args[0].equalsIgnoreCase("player")){
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
			if(target == null){
				if(!(sender instanceof BlockCommandSender)) {
                    sender.sendMessage(xLogin.chatPrefix+"Diesen Spieler gibt es nicht.");
                } return true;
			}
			if(args.length == 2){
				if(!(sender instanceof BlockCommandSender)) {
                    CommandChatFarbe.printChatFarbeTo(sender,target);
                } return true;
			}
			String cf = "";
			for(int i = 2;i<args.length;i++){
				cf += ((i == 2) ? "" : " ")+args[i];
			}
			this.changeChatFarbeWMsgTo(sender,args,cf,"Chatfarbe wurde gesetzt auf:",target);
			return true;
		}
		if(CommandHelper.kickConsoleFromMethod(sender, label)) return true;
		String cf = "";
		for(int i = 0;i<args.length;i++){
			cf += ((i == 0) ? "" : " ")+args[i];
		}
		this.changeChatFarbeWMsgTo(sender,args,cf,"Deine Chatfarbe wurde gesetzt auf:",(Player) sender);
		return true;
	}
	public static void printChatFarbeTo(CommandSender sender, OfflinePlayer target){
		String targetName = target.getName();
		
		String pexUserPrefix = ChatColor.translateAlternateColorCodes('&', (MTC.chat == null) ? "§7[Fehler]" : MTC.chat.getPlayerPrefix(Bukkit.getWorlds().get(0), target.getName()));
		String clanTag = "";
		if(ClanHelper.isInAnyClan(targetName)){
            ClanMemberInfo cmi = ClanHelper.getMemberInfoByPlayerName(targetName);
            ClanInfo ci = ClanHelper.getClanInfoById(cmi.clanId);
            if(ci.id > 0){
                clanTag = ClanHelper.getFormattedPrefix(ci);
            }
        }
		sender.sendMessage(MTC.chatPrefix+(sender.getName().equalsIgnoreCase(targetName) ? "Deine aktuelle Chatfarbe:" : "§b"+targetName+"§6's aktuelle Chatfarbe:"));
		String color = (target.isOnline() ? MTCChatHelper.getFinalChatColorByCSender(target.getPlayer()) : MTCChatHelper.getFinalChatColorByNameIgnorePerms(targetName));
		sender.sendMessage("§6 => §r"+pexUserPrefix+" "+clanTag+"§7"+targetName+"§7:§f "+color+"cogito ergo sum");
	}
}
