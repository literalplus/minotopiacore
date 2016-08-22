/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.chat;

import li.l1t.common.util.ChatHelper;
import li.l1t.common.util.CommandHelper;
import li.l1t.mtc.ConfigHelper;
import li.l1t.mtc.MTC;
import li.l1t.mtc.clan.ClanHelper;
import li.l1t.mtc.clan.ClanInfo;
import li.l1t.mtc.clan.ClanMemberInfo;
import li.l1t.mtc.clan.ClanPermission;
import li.l1t.mtc.logging.LogManager;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public final class ChatListener implements Listener {

    private static final Logger LOGGER = LogManager.getLogger(ChatListener.class);
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
                LOGGER.warn("=>LAG MESSAGE=>{}({}): {}", plrName, plr.getAddress(), ChatColor.stripColor(finalMsg));
                plr.sendMessage(MTC.chatPrefix + "Bitte keine Lagnachrichten :) §a/rules");
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
            LOGGER.warn("=>SPAM MESSAGE=>{}({}): {}", plrName, plr.getAddress(), ChatColor.stripColor(finalMsg));
            e.setCancelled(true);
            plr.sendMessage(MTC.chatPrefix + "Bitte nicht spammen :)");
            return;
        }
        if (!plr.hasPermission("mtc.ignore")) {
            this.lastMessages.put(plrName, finalMsg);
        }

        //werbung
        if (MTCChatHelper.isAdvertisement(finalMsg)) {
            LOGGER.warn("=>AD MESSAGE=>{}({}): {}", plrName, plr.getAddress(), ChatColor.stripColor(finalMsg));
            if (plr.hasPermission("mtc.ignore")) {
                plr.sendMessage(MTC.chatPrefix + "Du hast den Werbefilter ignoriert. Na toll!");
            } else {
                e.setCancelled(true);
                plr.sendMessage(MTC.chatPrefix + "§aWerbung ist ein Armutszeugnis.");
                CommandHelper.broadcast(MTC.chatPrefix + "Der Spieler §a" + plrName + "§6 hat den Werbefilter ausgelöst:", "mtc.adinfo");
                CommandHelper.broadcast(MTC.chatPrefix + "§a   " + finalMsg, "mtc.adinfo");
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
                    clanTag = "§aERR" + "§7*";
                } else {
                    if (isClanChat) {
                        e.setCancelled(true);
                        if (!ClanPermission.hasAndMessage(plr, ClanPermission.USECHAT)) {
                            return;
                        }
                        ClanHelper.sendChatMessage(ci, finalMsg, cmi);
                        return;
                    }
                    clanTag = ClanHelper.getFormattedPrefix(ci) + ClanHelper.getStarsByRank(cmi.getRank());
                }
            } else {
                if (isClanChat) {
                    e.setCancelled(true);
                    plr.sendMessage(MTC.chatPrefix + "Du bist in keinem Clan! §a/clan");
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
            plr.sendMessage(MTC.chatPrefix + "Du bist gemuted. Mehr Info: §a/mute info " + plrName);
            e.setCancelled(true);
            return;
        }

        //CAPSSSSS
        if (MTCChatHelper.isCaps(finalMsg)) {
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