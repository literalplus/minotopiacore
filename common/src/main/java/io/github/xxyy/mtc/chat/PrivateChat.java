/*
 * Copyright (c) 2013-2014.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.chat;

import io.github.xxyy.mtc.LogHelper;
import io.github.xxyy.mtc.MTC;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Level;


public class PrivateChat { //REFACTOR
    public static int currentId = 0;
    public static Map<Player, List<PrivateChat>> recChats = new HashMap<>();
    public static Map<Player, List<PrivateChat>> invitedChats = new HashMap<>();
    public static Map<Player, PrivateChat> activeChats = new HashMap<>();

    public int chatId = 0;
    public List<Player> recipients = new ArrayList<>();
    public Player leader;
    public List<Player> activeRecipients = new ArrayList<>();
    public String topic = "";

    public PrivateChat(Player leader, List<Player> recipients) {
        this.leader = leader;
        this.recipients = recipients;
        this.activeRecipients.add(leader);
        this.chatId = PrivateChat.currentId++;
        for (Player plr : recipients) {
            if (!PrivateChat.recChats.containsKey(plr)) {
                List<PrivateChat> list = new ArrayList<>();
                list.add(this);
                PrivateChat.recChats.put(plr, list);
            } else {
                List<PrivateChat> list = PrivateChat.recChats.get(plr);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(this);
                PrivateChat.recChats.put(plr, list);
            }
            if (!plr.getName().equalsIgnoreCase(leader.getName())) {
                plr.sendMessage(MTC.chatPrefix + "§b" + leader.getName() + "§6 hat dich zu einem privaten Chat eingeladen.");
                plr.sendMessage(MTC.chatPrefix + "Annehmen: §b/chat switch " + this.chatId + " §a§k| §6Deine Chats: §b/chat list");
            }
        }
        PrivateChat.updateActiveChat(leader, this);
        LogHelper.getPrivChatLogger().log(Level.INFO, leader.getName() + " CREATED: " + recipients);
    }

    public String getFormattedPlayerListAsString() {
        String recs = "";
        int i = 0;
        for (Player rec : this.recipients) {
            recs += ((PrivateChat.isActiveChat(rec, this)) ? "§a" : "§c") + rec.getName() + ((i == (this.recipients.size() - 1)) ? "" : "§6,");
            i++;
        }
        if (recs.isEmpty()) {
            return "leer";
        }
        return recs;
    }

    public boolean isLeader(Player plr) {
        return this.leader.equals(plr);
    }

    public void sendMessage(String msg) {
        if (this.activeRecipients.size() == 0) {
            return;
        }
        for (Player plr : this.activeRecipients) {
            plr.sendMessage(msg);
        }
    }

    public void removeRecipient(Player plr) {
        this.activeRecipients.remove(plr);
        this.recipients.remove(plr);
    }

    public static PrivateChat getActiveChat(Player plr) {
        if (!PrivateChat.activeChats.containsKey(plr)) {
            return null;
        }
        return PrivateChat.activeChats.get(plr);
    }

    public static boolean isActiveChat(Player plr, PrivateChat pc) {
        return PrivateChat.getActiveChat(plr) == pc;
    }

    public static boolean isInAnyPChat(Player plr) {
        if (!PrivateChat.activeChats.containsKey(plr)) {
            return false;
        }
        PrivateChat pc = PrivateChat.activeChats.get(plr);
        return pc != null;
    }

    public static void tryRemoveChatFromP(Player plr, PrivateChat pc) {
        if (!PrivateChat.isInAnyPChat(plr)) {
            return;
        }
        List<PrivateChat> lst = PrivateChat.recChats.get(plr);
        lst.remove(pc);
        pc.recipients.remove(plr);
        if (pc.activeRecipients.contains(plr)) {
            pc.activeRecipients.remove(plr);
            PrivateChat.activeChats.remove(plr);
        }
        PrivateChat.recChats.put(plr, lst);
        pc.sendMessage(MTC.chatPrefix + "§b" + plr.getName() + " §6hat den Chat verlassen.");
        if (pc.leader.getName().equalsIgnoreCase(plr.getName())) {
            if (pc.activeRecipients.isEmpty()) {
                MTCChatHelper.directChats.remove(pc.chatId);
                return;
            }
            int newLeaderId = (new Random()).nextInt(pc.activeRecipients.size());
            pc.leader = pc.activeRecipients.get(newLeaderId);
            pc.sendMessage(MTC.chatPrefix + "§b" + pc.leader.getName() + "§6 ist der neue Leiter dieses Chats!");
        }
    }

    public static boolean updateActiveChat(Player plr, PrivateChat pc) {
        return PrivateChat.updateActiveChatWMsg(plr, pc) == null;
    }

    public static String updateActiveChatWMsg(Player plr, PrivateChat pc) {
        if (!pc.recipients.contains(plr)) {
            return "Du wurdest nicht in diesen Chat eingeladen!";
        }
        if (!PrivateChat.activeChats.containsKey(plr)) {
            PrivateChat.activeChats.put(plr, pc);
            if (!pc.activeRecipients.contains(plr)) {
                pc.activeRecipients.add(plr);
            }
            return null;
        }
        PrivateChat previousChat = PrivateChat.activeChats.get(plr);
        PrivateChat.activeChats.put(plr, pc);
        if (!pc.activeRecipients.contains(plr)) {
            pc.activeRecipients.add(plr);
        }
        previousChat.activeRecipients.remove(plr);
        previousChat.sendMessage(MTC.chatPrefix + "§b" + plr.getName() + " §6hat den Chat gewechselt.");
        return null;
    }
}
