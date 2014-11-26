/*
 * Copyright (c) 2013-2014.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.chat;

import io.github.xxyy.mtc.ConfigHelper;
import io.github.xxyy.mtc.LogHelper;
import io.github.xxyy.mtc.clan.ClanInfo;
import io.github.xxyy.mtc.clan.ClanMemberInfo;
import io.github.xxyy.mtc.clan.ClanPermission;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import io.github.xxyy.common.util.ChatHelper;
import io.github.xxyy.common.util.CommandHelper;

import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.clan.ClanHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;


public final class ChatListener implements Listener {

    private final MTC plugin;

    private Map<String, String> lastMessages = new HashMap<>();
//	private Map<String,Boolean> plrAdCounts = new HashMap<>();

    public ChatListener(MTC plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent e) { //REFACTOR
        Player plr = e.getPlayer();
        String plrName = plr.getName();
        String clanTag = "";
        String finalMsg = e.getMessage();
        boolean isClanChat = ClanHelper.isInChat(plrName) || e.getMessage().startsWith("#");
        String vaultUserPrefix = ChatColor.translateAlternateColorCodes('&', plugin.getVaultHook().getPlayerPrefix(plr));

        //glomu
        if (ChatHelper.isGlobalMute && !plr.hasPermission("mtc.globalmute.exempt") && !plr.hasPermission("mtc.ignore") && !isClanChat) {
            plr.sendMessage(MTC.chatPrefix + "Du kannst nicht schreiben, wenn GlobalMute aktiviert ist!" + ChatHelper.gloMuReason);
            e.setCancelled(true);
            return;
        }


        //lag
        if (Pattern.compile("\\b([lL]+[aA4]+[gG]+)\\b").matcher(finalMsg).find()) {
            if (!plr.hasPermission("mtc.ignore")) {
                LogHelper.getChatLogger().log(Level.WARNING, "LAGMSG DETECTED=>" + plrName + "(" + plr.getAddress() + "): '" + finalMsg + "'");
                plr.sendMessage(MTC.chatPrefix + "Bitte keine Lagnachrichten :) §b/rules");
                e.setCancelled(true);
                return;
            }
            plr.sendMessage(MTC.chatPrefix + "Der Server laggt gar nicht! Lügner!!11");
        }

        //spam
        String lastMessage = this.lastMessages.get(plrName);
        if (lastMessage != null &&
                (lastMessage.equalsIgnoreCase(finalMsg) || //If it's the same, that's faster than Levenshtein
                        (lastMessage.length() > 5 && finalMsg.length() > 5 && //messages shorter than 5 letters usually cause false positives and can't contain IPs etc
                                StringUtils.getLevenshteinDistance(this.lastMessages.get(plrName), finalMsg) <= 2))) { //That's how many letters you have to change to get the other message
            LogHelper.getChatLogger().log(Level.WARNING, "SPAM DETECTED=>" + plrName + "(" + plr.getAddress() + "): '" + finalMsg + "'");
            e.setCancelled(true);
            plr.sendMessage(MTC.chatPrefix + "Bitte nicht spammen :)");
            return;
        }
        if (!plr.hasPermission("mtc.ignore")) {
            this.lastMessages.put(plrName, finalMsg);
        }

        //werbung
        if (MTCChatHelper.isAdvertisement(finalMsg)) {
            LogHelper.getChatLogger().log(Level.WARNING, "ADVERTISEMENT DETECTED=>" + plrName + "(" + plr.getAddress() + "): '" + finalMsg + "'");
            if (plr.hasPermission("mtc.ignore")) {
                plr.sendMessage(MTC.chatPrefix + "Du hast den Werbefilter ignoriert. Na toll!");
            } else {
                e.setCancelled(true);
                plr.sendMessage(MTC.chatPrefix + "§cWerbung ist ein Armutszeugnis.");
                CommandHelper.broadcast(MTC.chatPrefix + "Der Spieler §b" + plrName + "§6 hat den Werbefilter ausgelöst:", "mtc.adinfo");
                CommandHelper.broadcast(MTC.chatPrefix + "§b   " + finalMsg, "mtc.adinfo");
                return;
            }
        }

        //farben
        finalMsg = MTCChatHelper.replaceSpecialWords(finalMsg);
        if (plr.hasPermission("MTS.color.sonderfarben") || plr.hasPermission("mtc.chatcolor.special")) {
            finalMsg = ChatColor.translateAlternateColorCodes('&', finalMsg);
        } else if (plr.hasPermission("MTS.color") || plr.hasPermission("mtc.chatcolor")) {
            finalMsg = MTCChatHelper.convertStandardColors(finalMsg);
        }

        //clan
        if (ConfigHelper.getChatUseClan()) {
            if (ClanHelper.isInAnyClan(plrName) && !finalMsg.startsWith("❢g")) {
                ClanInfo ci = ClanHelper.getClanInfoByPlayerName(plrName);
                ClanMemberInfo cmi = ClanHelper.getMemberInfoByPlayerName(plrName);
                if (ci.id < 0 || cmi.clanId < 0) {
                    clanTag = "§cERR" + "§7*";
                } else {
                    if (isClanChat) {
                        e.setCancelled(true);
                        if (!ClanPermission.hasAndMessage(plr, ClanPermission.USECHAT)) {
                            return;
                        }
                        ClanHelper.broadcast(ci.id, "XC-chatformat", false, ClanHelper.getNameFormatByRank(plrName, cmi.getRank()),
                                ClanHelper.parseChatMessage(finalMsg, cmi));
                        MTCChatHelper.sendClanSpyMsg(plrName + ": " + e.getMessage(), ci.prefix);
                        LogHelper.getClanChatLogger().log(Level.INFO, "[C-" + ci.prefix + "=" + ci.name + "]" + plrName + ": " + e.getMessage());
                        return;
                    }
                    clanTag = ClanHelper.getFormattedPrefix(ci) + ClanHelper.getStarsByRank(cmi.getRank());
                }
            } else {
                if (isClanChat) {
                    e.setCancelled(true);
                    plr.sendMessage(MTC.chatPrefix + "Du bist in keinem Clan! §b/clan");
                    return;
                }
            }
        }

        //private chat
        if (PrivateChat.isInAnyPChat(plr) && !finalMsg.startsWith("❢g")) {
            String rtrn = MTCChatHelper.sendPrivateChat(plr, finalMsg, clanTag);//<--logging in there
            if (rtrn != null) {
                plr.sendMessage(MTC.chatPrefix + rtrn);
                e.setCancelled(true);
                return;
            }
            e.setCancelled(true);
            return;
        }

        //mute!!111
        if (MuteHelper.isPlayerMuted(plrName) && !plr.hasPermission("mtc.ignore")) {
            plr.sendMessage(MTC.chatPrefix + "Du bist gemuted. Mehr Info: §b/mute info " + plrName);
            e.setCancelled(true);
            return;
        }

        //CAPSSSSS
        if (MTCChatHelper.isCaps(finalMsg)) {
            LogHelper.getChatLogger().log(Level.WARNING, "CAPS DETECTED=>" + plrName + "(" + plr.getAddress() + "): '" + finalMsg + "'");
            if (plr.hasPermission("mtc.ignore")) {
                plr.sendMessage(MTC.chatPrefix + "CAPS-Autodetection ignoriert.");
            } else {
                finalMsg = finalMsg.toLowerCase();
                plr.sendMessage(MTC.chatPrefix + "Bitte nicht alles großschreiben :)");
            }
        }

        //FINAL SEND
        e.setFormat("MTC WAZ HEAR");
        e.setCancelled(true);
        finalMsg = finalMsg.replace("❢g", "");
        finalMsg = finalMsg.replace(".#", "#");
        if (ConfigHelper.isWorldSpecificChat()) {
            World world = plr.getWorld();
            String worldPrefix = (world.getName().equals("world")) ? "" : ("§6[§a§l" + world.getName() + "§6] ".replaceAll("a", "SG"));
            MTCChatHelper.sendMessageWorld(vaultUserPrefix + " " + worldPrefix + clanTag + "§7" + plrName + "§7:§f "
                    + MTCChatHelper.getFinalChatColorByCSender(plr) + finalMsg, plr, world);
        } else {
            MTCChatHelper.sendMessage(vaultUserPrefix + " " + clanTag + "§7" + plrName + "§7:§f "
                    + MTCChatHelper.getFinalChatColorByCSender(plr) + finalMsg, plr);
        }
    }
}
