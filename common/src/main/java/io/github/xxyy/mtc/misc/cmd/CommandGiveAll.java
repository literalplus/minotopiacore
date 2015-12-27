/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.misc.cmd;

import io.github.xxyy.common.chat.XyComponentBuilder;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.MTC;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.md_5.bungee.api.ChatColor.BLUE;
import static net.md_5.bungee.api.ChatColor.GOLD;

public final class CommandGiveAll extends MTCCommandExecutor {

    @Override
    public boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
        if (!CommandHelper.checkPermAndMsg(sender, "mtc.giveall", label)) {
            return true;
        }
        ItemStack finalStack;
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("hand")) {
                CommandHelper.kickConsoleFromMethod(sender, label + " hand");
                Player plr = (Player) sender;
                finalStack = plr.getItemInHand().clone();
                if (finalStack == null || finalStack.getType().equals(Material.AIR) || finalStack.getAmount() == 0) {
                    plr.sendMessage(MTC.chatPrefix + "Du hast nichts in der Hand!");
                    return true;
                }
                if (args.length >= 2 && StringUtils.isNumeric(args[1])) {
                    finalStack.setAmount(Integer.parseInt(args[1]));
                }
            } else {
                if (args[0].equalsIgnoreCase("help")) {
                    return CommandGiveAll.printHelpTo(sender);
                } else {
                    if (args.length >= 2) {
                        String[] itemInfo = args[0].split(":");
                        short damage = 0; //TODO
                        if (!StringUtils.isNumeric(args[1])) {
                            sender.sendMessage(MTC.chatPrefix + "Die An§lzahl" + MTC.priChatCol + " ist keine Zahl: " + args[1]);
                            return true;
                        }
                        if (itemInfo.length > 1) {
                            if (StringUtils.isNumeric(itemInfo[1])) {
                                damage = Short.parseShort(itemInfo[1]);
                            } else {
                                sender.sendMessage(MTC.chatPrefix + "Invalider Schadenswert/Invalide Metadata: " + itemInfo[1]);
                                return true;
                            }
                        }
                        if (StringUtils.isNumeric(itemInfo[0])) {
                            finalStack = new ItemStack(Integer.parseInt(itemInfo[0]), Integer.parseInt(args[1]), damage);
                            sender.sendMessage("§cACHTUNG! In zukünftigen Minecraft-Updates wirst du diese Methode nicht mehr benutzen können! "
                                    + "§cIch habe dir mal verziehen, aber bitte nutze beim nächsten Mal "
                                    + "§4/" + label + " " + finalStack.getType().name() + (damage == 0 ? "" : ":" + damage)
                                    + " " + finalStack.getAmount());
                        } else {
                            Material mat = Material.matchMaterial(itemInfo[0].replace("-", "_"));
                            if (mat == null) {
                                sender.sendMessage(MTC.chatPrefix + "Dieses Material ist unbekannt: " + itemInfo[0].toUpperCase());
                                return true;
                            }
                            finalStack = new ItemStack(mat, Integer.parseInt(args[1]), damage);
                        }
                    } else {
                        return CommandGiveAll.printHelpTo(sender);
                    }
                }
            }
            if (Bukkit.getOnlinePlayers().size() < 1) {
                sender.sendMessage(MTC.chatPrefix + "Es sind keine Spieler online.");
                return true;
            }
            BaseComponent[] components =
                    new XyComponentBuilder("[").color(GOLD).tooltip("§6Edler Spender:", "§7" + senderName)
                            .append("MTC", BLUE).append("] ", GOLD)
                            .append("Alle Spieler haben ", GOLD)
                            .append(getISString(finalStack), BLUE)
                            .append(" erhalten!", GOLD).create();

            for (Player plr : Bukkit.getOnlinePlayers()) {
                if (!plr.hasPermission("mtc.giveall.noitems")) {
                    plr.getInventory().addItem(finalStack.clone());
                }
                plr.spigot().sendMessage(components);
            }
        } else {
            return CommandGiveAll.printHelpTo(sender);
        }
        return true;
    }

    private static String getISString(ItemStack is) {
        return is.getType().toString() + " * " + is.getAmount();
    }

    private static boolean printHelpTo(CommandSender sender) {
        return CommandHelper.msg("§e/giveall hand <Anzahl>\n§e/giveall [ITEM_NAME]:<Damage> [Anzahl]\n"
                + "§e/giveall [Item-ID]:<Damage>", sender);
    }
}
