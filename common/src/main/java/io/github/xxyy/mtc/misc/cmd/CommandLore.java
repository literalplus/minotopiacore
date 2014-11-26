/*
 * Copyright (c) 2013-2014.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.misc.cmd;

import io.github.xxyy.mtc.helper.MTCHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.xxyy.common.misc.HelpManager;

import io.github.xxyy.mtc.misc.LoreManager;


public final class CommandLore implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
		if(!MTCHelper.isEnabledAndMsg(".command.lore", sender)) {
            return true;
        }
		if(args.length == 0){
			if(!(sender instanceof Player)){
				sender.sendMessage("Das Kommando /"+label+" kann nur von einem Spieler benutzt werden!");
			}
			this.printHelpToSender(sender,label);
		}else if(args.length >= 1){
			LoreManager lm = new LoreManager(sender,label,args,this);
			switch(args[0]){
			case "add":
				lm.addLore();
				break;
			case "clear":
				lm.clearLore();
				break;
			case "remove":
				lm.removeLore();
				break;
			case "set":
				lm.setLoreAt();
				break;
			case "list":
				lm.listlore();
				break;
			default:
				sender.sendMessage("§8Invalide Aktion '"+args[0]+"'! Valide Aktionen: add,clear,remove,set,list.");
				this.printHelpToSender(sender, label);
			}
		}else{
			sender.sendMessage("§8Falsche Verwendung von "+label+"!");
			this.printHelpToSender(sender,label);
		}
		return true;
	}
	public void printHelpToSender(CommandSender sender,String label){
		HelpManager.tryPrintHelp("lore", sender, label, "", "mtc help lore");
	}
	
	
	
	

}
