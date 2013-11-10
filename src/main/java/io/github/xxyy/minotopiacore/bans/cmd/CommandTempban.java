package io.github.xxyy.minotopiacore.bans.cmd;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.LogHelper;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.bans.BanHelper;
import io.github.xxyy.minotopiacore.bans.BanInfo;
import io.github.xxyy.minotopiacore.misc.BungeeHelper;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandTempban implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!CommandHelper.checkPermAndMsg(sender, "mtc.ban.temporary", label)) return true;
		if(args.length >= 3 && !args[0].equalsIgnoreCase("help")){
			Player plr = Bukkit.getPlayerExact(args[0]);
			if(plr == null){
				OfflinePlayer oPlr = Bukkit.getOfflinePlayer(args[0]);
				if(oPlr == null){
					sender.sendMessage(MTC.banChatPrefix+"Dieser Spieler ist dem System nicht bekannt.");
				}else{
					sender.sendMessage(MTC.banChatPrefix+"Dieser Spieler ist nicht online.");
				}
			}
			long millisUntilExpiry = BanHelper.getMillisFromRelativeString(args[1]);
			if(millisUntilExpiry < 0){
				sender.sendMessage(MTC.banChatPrefix+"Deine Zeitangabe §6'"+args[1]+"'§b ist invalide. ("+millisUntilExpiry+")"); return true;
			}
			String reason = args[2];
			if(args.length > 3){
				StringBuilder sb = new StringBuilder();
				for(byte i = 3;i < args.length;i++){
					sb.append(" "+args[i]);
				}
				reason += sb.toString();
			}
			reason = ChatColor.translateAlternateColorCodes('&', reason);
			reason += " "+MTC.instance().warnBanServerSuffix;
			BanHelper.setBanned(args[0].toLowerCase(), sender.getName(), reason, (byte)0, millisUntilExpiry/1000);//return won't work
			BanInfo bi = BanHelper.getBanInfoByPlayerName(args[0].toLowerCase());
			if(plr != null){
				plr.kickPlayer(BanHelper.getBanReasonForKick(bi, true));
			}
			BanHelper.broadcastBanChatMsg(bi);
			BungeeHelper.notifyServersBan(bi);
			LogHelper.getBanLogger().log(Level.WARNING, sender.getName()+" TEMPBANNED: "+args[0]+" COZ: "+reason+" UNTIL: "+args[1]+"="+bi.banExpiryTimestamp+" ID: "+bi.id);
			return true;
		}
        sender.sendMessage("§6Verwendung: §b/tempban <SPIELER> <ZEIT> <GRUND>");
        sender.sendMessage("§6Bsp.: /tempban McSender 2y3M Weil Baum. §bBannt McSender 2 Jahre und 3 Monate.");
        sender.sendMessage("§6Zeitformat: §by=Jahr; M=Monat; w=Woche; d=Tag; h=Stunde; m=Minute; s=Sekunde");
		return true;
	}

}
