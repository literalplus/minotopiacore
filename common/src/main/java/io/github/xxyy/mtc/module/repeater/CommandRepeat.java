/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.repeater;

import io.github.xxyy.common.chat.ComponentSender;
import io.github.xxyy.common.chat.XyComponentBuilder;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.common.util.StringHelper;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;

import static net.md_5.bungee.api.ChatColor.*;

/**
 * Provides a text-based front-end to the repeater module, with admin features.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 14.11.14
 */
class CommandRepeat implements CommandExecutor {
    private final RepeaterModule module;

    public CommandRepeat(RepeaterModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "list":
                    if (module.getMessages().isEmpty()) {
                        ComponentSender.sendTo(
                                new XyComponentBuilder("Keine Nachrichten vorhanden. ").color(GOLD)
                                        .append("[+]", DARK_GREEN)
                                        .tooltip("Hinzufügen?")
                                        .suggest("/rpt add 5m <Nachricht>")
                                        .create(),
                                sender
                        );
                        return true;
                    }

                    int i = 0;
                    for (RepeatingMessage msg : module.getMessages()) {
                        ComponentSender.sendTo(
                                new XyComponentBuilder(" -> ").color(GOLD)
                                        .append(msg.getMessage(), WHITE)
                                        .tooltip("von " + module.getPlugin().getXLoginHook().getDisplayString(msg.getAuthor()))
                                        .append(" @" + msg.getSecondInterval() + "s ", RED, ComponentBuilder.FormatRetention.NONE)
                                        .append("[-]", DARK_RED)
                                        .tooltip("Löschen?")
                                        .suggest("/repeat delete " + i++)
                                        .create(),
                                sender
                        );
                    }
                    return true;
                case "delete":
                case "remove":
                    if (args.length < 2) {
                        sender.sendMessage("§c/repeat delete [id]");
                        return true;
                    }
                    int index;
                    if (StringUtils.isNumeric(args[1])) {
                        index = Integer.parseInt(args[1]);
                    } else {
                        sender.sendMessage("§cDas ist keine Zahl!");
                        return true;
                    }

                    if (index >= module.getMessages().size()) {
                        sender.sendMessage("§cEs gibt keine Nachricht mit dieser ID!");
                        return true;
                    }

                    RepeatingMessage removed = module.getMessages().remove(index);
                    module.save();
                    sender.sendMessage("§6Entfernt: " + removed.getMessage() + " §c@" + removed.getSecondInterval() + "s");
                    return true;
                case "add":
                    if (args.length < 3) {
                        sender.sendMessage("§c/repeat add [Intervall: 1y2M3d5h40m10s] [Nachricht]");
                        return true;
                    }

                    long interval;
                    try {
                        interval = StringHelper.parseTimePeriod(args[1]);
                    } catch (IllegalStateException e) {
                        sender.sendMessage("§cTime Parse error: " + e.getMessage());
                        return true;
                    }
                    interval = interval / 1000;

                    if (interval < TimeUnit.SECONDS.convert(5, TimeUnit.MINUTES)) {
                        sender.sendMessage("§cAchtung! Intervalle kürzer als 5 Minuten sind sehr nervig für die User!");
                    }

                    String message = StringHelper.varArgsString(args, 2, true);
                    module.getMessages().add(new RepeatingMessage(message,
                            (long) Math.floor(interval / 5),
                            CommandHelper.getSenderId(sender)));
                    sender.sendMessage("§aHinzugefügt mit Intervall=§e" + interval + "s§a!");
                    module.save();
                    return true;
            }
        }

        sender.sendMessage("§9/repeat list §2Listet alle Nachrichten auf");
        sender.sendMessage("§9/repeat remove [Index] §2Entfernt eine Nachricht");
        sender.sendMessage("§9/repeat add [Intervall] [Frage] §2Fügt eine Nachricht hinzu");
        sender.sendMessage("§cAchtung: Wenn es für ein Intervall mehrere Nachrichten gibt, wird jedes Mal nur eine zufällige angezeigt!");
        sender.sendMessage("§eVerwende §6{player} §ein einer Nachricht für den jeweiligen Spielernamen!");
        sender.sendMessage("§eVerwende §6{p} §ein einer Nachricht für " + module.getPlugin().getChatPrefix() + "§e!");
        sender.sendMessage("§eMehr Info: https://wiki.minotopia.me/w/Repeater");
        return true;
    }
}
