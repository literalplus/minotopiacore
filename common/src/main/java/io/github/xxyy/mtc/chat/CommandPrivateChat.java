/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.chat;

import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.logging.LogManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


public final class CommandPrivateChat implements CommandExecutor {
	private static final Logger LOGGER = LogManager.getLogger(CommandPrivateChat.class);

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
		if(!CommandHelper.checkPermAndMsg(sender, "mtc.privatechat", label)) {
            return true;
        }
		if(CommandHelper.kickConsoleFromMethod(sender, label)) {
            return true;
        }
		Player plr = (Player)sender;
		if(args.length >= 1 && args[0].equalsIgnoreCase("help")){
			this.printHelpTo(plr,label);
			return true;
		}else if(args.length >= 1 && args[0].equalsIgnoreCase("leave")){
			if(PrivateChat.isInAnyPChat(plr)){
				if(args.length >= 2){
					int i = 1;
					while(i < args.length){
						int j;
						try {
							j = Integer.parseInt(args[i]);
						} catch (NumberFormatException e) {
							plr.sendMessage(MTC.chatPrefix+"Eines der Argumente ist keine Zahl: "+args[i]);
							plr.sendMessage(MTC.chatPrefix + "Verwendung: §a/chat leave <chatId> <chatId> <..>");
							return true;
						}
						PrivateChat pc = MTCChatHelper.directChats.get(j);
						if(pc == null){
							plr.sendMessage(MTC.chatPrefix + "Es existiert kein privater Chat mit der ID §a#" + j + "§6!");
							i++;
							continue;
						}
						PrivateChat.tryRemoveChatFromP(plr, pc);
						i++;
					}
					plr.sendMessage(MTC.chatPrefix+"Du hast "+ (i - 1) +((i == 2) ? " privaten Chat" : " private Chats")+" verlassen!");
					return true;
				}
				int i = 0;
				List<PrivateChat> lst = PrivateChat.recChats.get(plr);
				if(!lst.isEmpty()){
					while(i <= lst.size()){
						PrivateChat pc = lst.get(i);
						if(pc == null) {
                            continue;
                        }
						PrivateChat.tryRemoveChatFromP(plr, pc);
						i++;
					}
				}
				PrivateChat.recChats.remove(plr);
				plr.sendMessage(MTC.chatPrefix+"Du hast "+i+((i == 1) ? " privaten Chat" : " private Chats")+" verlassen!");
				return true;
			}
            plr.sendMessage(MTC.chatPrefix+"Du bist in keinem privaten Chat!");
            return true;
		}else if(args.length >= 1 && args[0].equalsIgnoreCase("switch")){
			if(args.length == 1){
				PrivateChat pc = PrivateChat.getActiveChat(plr);
				if(pc != null){
					pc.activeRecipients.remove(plr);
					pc.sendMessage(MTC.chatPrefix + "§a" + plr.getName() + " §6hat den Chat gewechselt!");
				}
				PrivateChat.activeChats.remove(plr);
				plr.sendMessage(MTC.chatPrefix+"Du chattest jetzt wieder global!");
				return true;
			}
			int i;
			try {
				i = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				plr.sendMessage(MTC.chatPrefix+"Eines der Argumente ist keine Zahl: "+args[1]);
				plr.sendMessage(MTC.chatPrefix + "Verwendung: §a/chat switch <chatId>");
				return true;
			}
			if(!MTCChatHelper.directChats.containsKey(i)){
				plr.sendMessage(MTC.chatPrefix+"Diesen privaten Chat gibt es nicht!");
				return true;
			}
			PrivateChat pc = MTCChatHelper.directChats.get(i);
			String rtrn = PrivateChat.updateActiveChatWMsg(plr, pc);
			if(rtrn != null){
				plr.sendMessage(MTC.chatPrefix+rtrn);
			}
			pc.sendMessage(MTC.chatPrefix + "§a" + plr.getName() + "§6 ist dem Chat beigetreten.");
			plr.sendMessage(MTC.chatPrefix + "Du chattest jetzt in §a#" + pc.chatId + "§6.");
			if(!pc.topic.isEmpty()){
				plr.sendMessage("§6Thema: §e"+pc.topic);
			}
			return true;
		}else if(args.length >= 1 && args[0].equalsIgnoreCase("list")){
			if(!PrivateChat.recChats.containsKey(plr)){
				plr.sendMessage(MTC.chatPrefix+"Du bist in keinem privaten Chat!");
				return true;
			}
			List<PrivateChat> lst = PrivateChat.recChats.get(plr);
			if(lst == null || lst.isEmpty()){
				plr.sendMessage(MTC.chatPrefix+"Du bist in keinem privaten Chat!");
				return true;
			}
			plr.sendMessage("§6----- §aAktive Chats §6----");
			for(PrivateChat pc : lst){
				plr.sendMessage(((PrivateChat.isActiveChat(plr, pc)) ? "§a" : "§6") + "[" + pc.chatId + "]§a: " + pc.getFormattedPlayerListAsString());
			}
		}else if(args.length >= 2 && args[0].equalsIgnoreCase("add")){
			if(!CommandHelper.checkPermAndMsg(sender, "mtc.chat.add", label)) {
                return true;
            }
			
			Player target = Bukkit.getPlayerExact(args[1]);
			if(target == null){
				plr.sendMessage(MTC.chatPrefix + "Der Spieler §a" + args[1] + "§6 ist nicht online!");
				return true;
			}
			PrivateChat pc = PrivateChat.getActiveChat(plr);
			if(pc == null){
				plr.sendMessage(MTC.chatPrefix + "Du bist in keinem Chat! §a/chat switch <ID>");
				return true;
			}
            LOGGER.info("{} added {} to private chat {}", sender.getName(), args[1], pc.chatId);
			if(pc.activeRecipients.contains(target)){
				plr.sendMessage(MTC.chatPrefix+"Diese Person ist bereits in deinem Chat!");
				return true;
			}
			if(!pc.isLeader(plr) && !plr.hasPermission("mtc.chat.overwrite")){
				plr.sendMessage(MTC.chatPrefix+"Du bist nicht der Leiter dieses Chats!");
				return true;
			}
			boolean hasExt = plr.hasPermission("mtc.chat.extended");
			boolean hasInf = plr.hasPermission("mtc.chat.infinite");
			if(pc.recipients.size() >= 3 && !hasExt && !hasInf){
				plr.sendMessage(MTC.chatPrefix+"Du kannst nicht mehr als 3 Leute zu einem Chat hinzufügen!");
				return true;
			}
			if(pc.recipients.size() >= 5 && !hasInf){
				plr.sendMessage(MTC.chatPrefix+"Du kannst nicht mehr als 5 Leute zu einem Chat hinzufügen!");
				return true;
			}
			if(pc.recipients.contains(target)){
				target.sendMessage(MTC.chatPrefix + "§a" + plr.getName() + "§6 hat dich zu einem privaten Chat eingeladen.");
				target.sendMessage(MTC.chatPrefix + "Annehmen: §a/chat switch " + pc.chatId + " §a§k| §6Deine Chats: §a/chat list");
				pc.sendMessage(MTC.chatPrefix + "Der Spieler §a" + args[1] + "§6 wurde zum Chat eingeladen.");
				return true;
			}
			pc.recipients.add(target);
			if(PrivateChat.recChats.containsKey(target)){
				List<PrivateChat> lst = PrivateChat.recChats.get(target);
				if(lst == null) {
                    lst = new ArrayList<>();
                }
				lst.add(pc);
				PrivateChat.recChats.put(target, lst);
			}else{
				List<PrivateChat> lst = new ArrayList<>();
				lst.add(pc);
				PrivateChat.recChats.put(target, lst);
			}
			target.sendMessage(MTC.chatPrefix + "§a" + plr.getName() + "§6 hat dich zu einem privaten Chat eingeladen.");
			target.sendMessage(MTC.chatPrefix + "Annehmen: §a/chat switch " + pc.chatId + " §a§k| §6Deine Chats: §a/chat list");
			pc.sendMessage(MTC.chatPrefix + "Der Spieler §a" + args[1] + "§6 wurde zum Chat eingeladen.");
			return true;
		}else if(args.length >= 1 && args[0].equalsIgnoreCase("info")){
			PrivateChat pc = PrivateChat.getActiveChat(plr);
			if(pc == null){
				plr.sendMessage(MTC.chatPrefix+"Du bist in keinem privaten Chat!");
				return true;
			}
			plr.sendMessage("§6---- §aPrivater Chat #" + pc.chatId + " §6----");
			plr.sendMessage("§6Leiter: §a" + pc.leader.getName());
			plr.sendMessage("§6Empfänger: §a" + pc.getFormattedPlayerListAsString());
		}else if(args.length >= 2 && args[0].equalsIgnoreCase("setleader")){
			Player target = Bukkit.getPlayerExact(args[1]);
			if(target == null){
				plr.sendMessage(MTC.chatPrefix + "Der Spieler §a" + args[1] + "§6 ist nicht online!");
				return true;
			}
			PrivateChat pc = PrivateChat.getActiveChat(plr);
			if(pc == null){
				plr.sendMessage(MTC.chatPrefix + "Du bist in keinem Chat! §a/chat switch <ID>");
				return true;
			}
			if(!pc.isLeader(plr) && !plr.hasPermission("mtc.chat.overwrite")){
				plr.sendMessage(MTC.chatPrefix+"Du bist nicht Leiter dieses Chats!");
				return true;
			}
			pc.leader = target;
			pc.sendMessage(MTC.chatPrefix + "§a" + target.getName() + "§6 ist der neue Leiter dieses Chats!");
			return true;
		}else if(args.length >= 2 && args[0].equalsIgnoreCase("kick")){
			Player target = Bukkit.getPlayerExact(args[1]);
			if(target == null){
				plr.sendMessage(MTC.chatPrefix + "Der Spieler §a" + args[1] + "§6 ist nicht online!");
				return true;
			}
			PrivateChat pc = PrivateChat.getActiveChat(plr);
			if(pc == null){
				plr.sendMessage(MTC.chatPrefix + "Du bist in keinem Chat! §a/chat switch <ID>");
				return true;
			}
			if(!pc.isLeader(plr) && !plr.hasPermission("mtc.chat.overwrite")){
				plr.sendMessage(MTC.chatPrefix+"Du bist nicht Leiter dieses Chats!");
				return true;
			}
			pc.recipients.remove(target);
			pc.activeRecipients.remove(target);
			if(PrivateChat.recChats.containsKey(target)){
				List<PrivateChat> lst = PrivateChat.recChats.get(target);
				if(lst == null) {
                    lst = new ArrayList<>();
                }
				lst.remove(pc);
				PrivateChat.recChats.put(target, lst);
			}else{
				List<PrivateChat> lst = new ArrayList<>();
				lst.remove(pc);
				PrivateChat.recChats.put(target, lst);
			}
			pc.sendMessage(MTC.chatPrefix + "§a" + target.getName() + "§6 wurde aus dem Chat gekickt!");
			if(pc.leader.getName().equalsIgnoreCase(target.getName())){
				if(pc.activeRecipients.isEmpty()){
					MTCChatHelper.directChats.remove(pc.chatId);
					pc = null;
				}else{
    				int newLeaderId = (new Random()).nextInt(pc.activeRecipients.size());
    				pc.leader = pc.activeRecipients.get(newLeaderId);
					pc.sendMessage(MTC.chatPrefix + "§a" + pc.leader.getName() + "§6 ist der neue Leiter dieses Chats!");
				}
			}
			if(PrivateChat.isActiveChat(target, pc)){
				PrivateChat.activeChats.remove(target);
				target.sendMessage(MTC.chatPrefix+"Du wurdest aus dem Chat gekickt!");
				target.sendMessage(MTC.chatPrefix+"Du chattest jetzt wieder global.");
			}else{
			    if(pc != null)
                {
					target.sendMessage(MTC.chatPrefix + "Du wurdest aus dem Chat §a#" + pc.chatId + "§6 gekickt.");
				}
			}
			return true;
		}else if(args.length >= 1 && args[0].equalsIgnoreCase("topic")){
			if(args.length == 1){
				PrivateChat pc = PrivateChat.getActiveChat(plr);
				if(pc == null){
					plr.sendMessage(MTC.chatPrefix+"Du bist in keinem privaten Chat!");
					return true;
				}
				if(!pc.topic.isEmpty()){
					plr.sendMessage("§6Thema: §e"+pc.topic);
				}else{
					plr.sendMessage("§6Thema nicht gesetzt. Setzen: §3/chat topic <Neues Thema>");
				}
				return true;
			}
			PrivateChat pc = PrivateChat.getActiveChat(plr);
			if(pc == null){
				plr.sendMessage(MTC.chatPrefix+"Du bist in keinem privaten Chat!");
				return true;
			}
			String txt = "";
			for(int i = 1;i < args.length;i++){
				txt += (i == 1 ? "" : ",")+args[i];
			}
			if(plr.hasPermission("mtc.chat.topic.color")) {
                txt = ChatColor.translateAlternateColorCodes('&', txt);
            }
			pc.topic = txt+" <gesetzt von "+plr.getName()+";"+(new SimpleDateFormat("dd.MM. HH:mm").format(Calendar.getInstance().getTime()))+">";
			pc.sendMessage("§6Neues Thema: §e"+pc.topic);
            LOGGER.info("{} set topic for private chat {} to: {}", sender.getName(), pc.chatId, pc.topic);
			return true;
		}
		else{
			if(args.length == 1){
				Player receiver = Bukkit.getPlayerExact(args[0]);
				if(receiver == null){
					plr.sendMessage("§cUnbekannter Spieler/Unbekannte Aktion! Hilfe:");
					this.printHelpTo(plr,label);
					return true;
				}
				if(receiver.getName().equalsIgnoreCase(plr.getName())){
					plr.sendMessage(MTC.chatPrefix+"Du kannst keinen privaten Chat mit dir selbst eröffnen.");
					return true;
				}
				ArrayList<Player> recList = new ArrayList<>();
				recList.add(receiver);
				recList.add(plr);
				PrivateChat pc = new PrivateChat(plr, recList);
				System.out.println(PrivateChat.activeChats.toString());
				MTCChatHelper.directChats.put(pc.chatId, pc);
//				PrivateChat.updateActiveChat(plr, pc);
				plr.sendMessage(MTC.chatPrefix + "Du bist jetzt im privaten Chat §a#" + pc.chatId + "§6.");
				return true;
			}
			plr.sendMessage("§cUnbekannte Aktion. Hilfe:");
			this.printHelpTo(plr,label);
		}
		return true;
	}
	
	public void printHelpTo(CommandSender sender,String label){
		sender.sendMessage("§6----------------- §a/chat Hilfe §6-----------------");
		sender.sendMessage("§6/" + label + " <Name> §aStartet einen privaten Chat mit <Name>");
		sender.sendMessage("§6/" + label + " leave §aVerlässt alle privaten Chats");
		sender.sendMessage("§6/" + label + " leave <ID> §aVerlässt <ID>");
		sender.sendMessage("§6/" + label + " list §aZeigt alle deine offenen Chats");
		sender.sendMessage("§6/" + label + " switch <ID> §aWechselt zum Chat <ID>");
		sender.sendMessage("§6/" + label + " switch §aWechselt zum Globalchat");
		sender.sendMessage("§6/" + label + " add <Name> §aFügt <Name> zum privaten Chat hinzu");
		sender.sendMessage("§6/" + label + " info §aZeigt Informationen zu deinem aktuellen Chat an");
		sender.sendMessage("§6/" + label + " setleader <Spieler> §aSetzt einen neuen Chatleiter");
		sender.sendMessage("§6/" + label + " kick <Spieler> §aKickt <Spieler>. Falls dieser Leiter war,          wird ein zufälliger neuer Leiter ausgewählt");
		sender.sendMessage("§6/" + label + " topic §aZeigt das Thema es aktuellen Chats an");
		sender.sendMessage("§6/" + label + " topic <Neues Thema> §aSetzt das Thema es aktuellen Chats");
	}

}
