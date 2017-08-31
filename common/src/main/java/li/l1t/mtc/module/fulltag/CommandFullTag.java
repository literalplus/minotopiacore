/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package li.l1t.mtc.module.fulltag;

import li.l1t.common.chat.ComponentSender;
import li.l1t.common.chat.XyComponentBuilder;
import li.l1t.common.util.CommandHelper;
import li.l1t.common.util.LocationHelper;
import li.l1t.common.util.StringHelper;
import li.l1t.common.util.UUIDHelper;
import li.l1t.mtc.hook.XLoginHook;
import li.l1t.mtc.module.fulltag.model.FullData;
import li.l1t.mtc.module.fulltag.model.FullInfo;
import li.l1t.mtc.module.fulltag.model.FullPart;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static net.md_5.bungee.api.ChatColor.*;

/**
 * Executes the /full command, which allows specifically privileged administrators to manage Full
 * items, that is, distribute new ones, see information about existing ones, and view stats.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-08-29
 */
public final class CommandFullTag implements CommandExecutor {
    private final FullTagModule module;

    public CommandFullTag(FullTagModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, @Nonnull String[] args) {
        if (!(sender instanceof ConsoleCommandSender || module.isAllowedPlayer(CommandHelper.getSenderId(sender)))) {
            sender.sendMessage("§4Du bist nicht autorisiert, Fulls zu verwalten!");
            return true;
        }

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "info":
                    return interpretInfo((Player) sender, args);
                case "list":
                    return interpretList(sender, args);
                case "get":
                    return interpretGet(sender, args);
                case "help":
                    break;
                default:
                    sender.sendMessage("§cUnbekannte Aktion! Hilfe:");
            }
        }
        printHelpTo(sender);
        return true;
    }

    private boolean interpretGet(@Nonnull CommandSender sender, @Nonnull String[] args) {
        /*
        /full get [-t] [-a] [-p <partId>]... <Receiver> <Comment>
         */
        if (args.length < 4 || !args[1].startsWith("-")) {
            sender.sendMessage("§cIch brauche mindestens vier Argumente und das zweite muss mit einem Bindestrich beginnen.");
            printHelpTo(sender);
            return true;
        }

        byte startIndex = 1;
        boolean thorns = false;
        boolean getAll = false;
        Set<FullPart> parts = new HashSet<>();
        for (; startIndex < args.length; startIndex++) {
            String arg = args[startIndex];
            if (arg.charAt(0) != '-') {
                break;
            }
            switch (arg.charAt(1)) {
                case 't':
                    thorns = true;
                    continue;
                case 'a':
                    getAll = true;
                    continue;
                case 'p': //needs an additional argument describing the part
                    if (args.length < startIndex + 1) {
                        sender.sendMessage("§cNach /full get -p muss die Art des Fullteils stehen.");
                        sender.sendMessage("§eBeispiel: /full get -p Hose chris301234 test");
                        return true;
                    }
                    String nextArg = args[startIndex + 1];
                    FullPart part = FullPart.fromString(nextArg);
                    if (part != null) {
                        parts.add(part);
                    } else {
                        sender.sendMessage(String.format("§cDas ist kein Fullteil: §e'%s'", nextArg));
                        sender.sendMessage("§eValide Fullteile: " + StringUtils.join(FullPart.values(), ", "));
                        return true;
                    }
                    startIndex += 1; //we consumed an extra argument
                    continue;
                default:
                    sender.sendMessage(String.format("§cUnbekanntes Argument '%s'", arg));
                    printHelpTo(sender);
                    return true;
            }
        }

        String receiverName = args[startIndex];
        String comment = StringHelper.varArgsString(args, startIndex + 1, false);
        Player receiver = Bukkit.getPlayerExact(receiverName);
        if (UUIDHelper.isValidUUID(receiverName)) {
            receiver = Bukkit.getPlayer(UUID.fromString(receiverName));
        }
        if (receiver == null) {
            sender.sendMessage(String.format("§cKein Spieler mit diesem Namen oder dieser UUID ist online: '%s'",
                    receiverName));
            return false;
        }

        if (getAll) {
            Arrays.stream(FullPart.values()).forEach(parts::add);
        }

        if (parts.isEmpty()) {
            sender.sendMessage("§cKeine Fullteile ausgewählt. Versuche -a oder -p <Fullteil>");
            printHelpTo(sender);
            return true;
        }

        final Player finalReceiver = receiver;
        final boolean finalThorns = thorns;
        sender.sendMessage(String.format("%s§eBitte warten, §6%d §eFullteile werden für §6%s §ebereitgestellt...",
                module.getPlugin().getChatPrefix(), parts.size(), receiver.getName()));
        module.getPlugin().getServer().getScheduler().runTaskAsynchronously(module.getPlugin(), () -> {
                    List<FullInfo> createdInfos = new LinkedList<>();
                    for (FullPart part : parts) {
                        FullData fullData = module.getRepository().create(
                                comment, CommandHelper.getSenderId(sender),
                                finalReceiver.getUniqueId(), part, finalThorns
                        );
                        createdInfos.add(module.getRegistry().create(
                                fullData, finalReceiver.getLocation()
                        ));
                    }

                    module.getPlugin().getServer().getScheduler().runTask(module.getPlugin(), () -> {
                        createdInfos.forEach(info ->
                                module.getDistributionManager().requestStore(info, finalReceiver)
                        );
                        module.getDistributionManager().notifyWaiting(finalReceiver);
                        sender.sendMessage(String.format("%s%d Fullitems erstellt.",
                                module.getPlugin().getChatPrefix(), createdInfos.size()));
                    });
                }
        );
        return true;
    }

    private boolean interpretInfo(@Nonnull Player sender, @Nonnull String[] args) {
        /*
            /full info <ID>
         */
        if (args.length < 2) {
            sender.sendMessage("§cIch brauche zwei Argumente: /full info <id>");
            return printHelpTo(sender);
        }

        if (!StringUtils.isNumeric(args[1])) {
            sender.sendMessage("§cDas zweite Argument muss eine Zahl sein. ");
            return printHelpTo(sender);
        }

        int fullId = Integer.parseInt(args[1]);

        FullData data = module.getRepository().getById(fullId);
        if (data == null) {
            sender.sendMessage("§cEs gibt kein registriertes Fullitem mit dieser ID.");
            return true;
        }
        FullInfo info = module.getRegistry().getById(fullId);
        String senderString = module.getPlugin().getXLoginHook().getDisplayString(data.getSenderId());
        String receiverString = module.getPlugin().getXLoginHook().getDisplayString(data.getReceiverId());

        sender.sendMessage(String.format("§9---- §2Fullitem: %d (%s) §9----", data.getId(), data.getPart().getAlias()));
        ComponentSender.sendTo(
                new XyComponentBuilder("gegeben von: ").color(BLUE)
                        .append(senderString, DARK_GREEN)
                        .append(" [UUID kopieren]", DARK_AQUA)
                        .suggest(data.getSenderId().toString()),
                sender
        );
        ComponentSender.sendTo(
                new XyComponentBuilder("Besitzer: ").color(BLUE)
                        .append(receiverString)
                        .append(" [UUID kopieren]", DARK_AQUA)
                        .suggest(data.getReceiverId().toString()),
                sender
        );
        sender.sendMessage(String.format("§9gegeben um: §2%s", data.getCreationTime().format(DateTimeFormatter.ISO_DATE_TIME)));
        sender.sendMessage("§9Kommentar: §2" + data.getComment());
        sender.sendMessage(String.format("§9Thorns? %s", data.isThorns() ? "§aja" : "§cnein"));
        sender.sendMessage("§9Teil: §2" + data.getPart().getAlias());
        if (info != null && info.isValid()) {
            String holderString = module.getPlugin().getXLoginHook().getDisplayString(info.getHolderId());

            sender.sendMessage("§9Zuletzt gesehen:");
            sender.sendMessage("  §9Position: §2" + LocationHelper.prettyPrint(info.getLocation()));
            ComponentSender.sendTo(
                    new XyComponentBuilder("  Spieler: ").color(BLUE)
                            .append(holderString, DARK_GREEN)
                            .append(" [UUID kopieren]", DARK_AQUA)
                            .suggest(info.getHolderId().toString()),
                    sender
            );
            sender.sendMessage(String.format("  §9um: §2%s", info.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME)));
            sender.sendMessage("  §9Aktion: §2" + info.getLocationCode());
            sender.sendMessage("  §9In Container? §2" + info.isInContainer());
        } else {
            sender.sendMessage("§eDieser Fullteil ist momentan nicht im Spiel.");
        }
        return true;
    }

    private boolean interpretList(@Nonnull CommandSender sender, @Nonnull String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cIch brauche zumindest zwei Argumente.");
            return printHelpTo(sender);
        }
        XLoginHook.Profile profile = module.getPlugin().getXLoginHook().getBestProfile(args[1]);
        if (profile == null) {
            sender.sendMessage("§cZu diesem Benutzernamen/dieser UUID kennen wir keinen Benutzer: " + args[1]);
            return true;
        }

        module.getPlugin().getServer().getScheduler().runTaskAsynchronously(module.getPlugin(), () -> {
            sender.sendMessage(String.format("§9---- §6FullInfo: %s §9----", profile.getName()));

            sender.sendMessage("§e§lFulls erhalten:");
            List<FullData> received = module.getRepository().findByReceiver(profile.getUniqueId()); //show them what we're loading - should only be a fraction of a second, but still

            received.forEach(data -> ComponentSender.sendTo(
                    new XyComponentBuilder(" => #").color(BLUE)
                            .append(String.valueOf(data.getId()), DARK_GREEN)
                            .append(" ist ein(e) ", BLUE)
                            .append(data.getPart().getAlias(), DARK_GREEN)
                            .append(" [Mehr Info]", DARK_AQUA)
                            .command("/full info " + data.getId()),
                    sender
            ));

            sender.sendMessage("§e§lFulls im Besitz:");
            List<FullInfo> held = module.getRegistry().findByLastHolder(profile.getUniqueId());

            held.forEach(info -> ComponentSender.sendTo(
                    new XyComponentBuilder(" => #").color(BLUE)
                            .append(String.valueOf(info.getId()), DARK_GREEN)
                            .append(" ist ein(e) ", BLUE)
                            .append(info.getData() == null ?
                                    "unbekanntes Fullitem" :
                                    info.getData().getPart().getAlias(), DARK_GREEN)
                            .append(" [Mehr Info]", DARK_AQUA)
                            .command("/full info " + info.getId()),
                    sender
            ));
        });

        return true;
    }

    private boolean printHelpTo(@Nonnull CommandSender sender) { //returns whether help was printed
        sender.sendMessage("§9FullTag - Fullmanagement 3.0");
        sender.sendMessage("§e/full info <ID> §6Zeigt Informationen zu einem Fullitem.");
        sender.sendMessage("§e/full list <Name> §6Zeigt Statistiken zu einem Spieler.");
        sender.sendMessage("§e/full get [-a|-t|-p <partId>] <Receiver> {Comment} §6Vergibt eine Full oder einen Fullteil..");
        sender.sendMessage("§7-t §8Fügt Thorns hinzu.");
        sender.sendMessage("§7-a §8Wählt alle Fullteile.");
        sender.sendMessage("§7-p <partId> §8Wählt nur einen Teil.");

        XyComponentBuilder builder = new XyComponentBuilder("Teile: ").color(GRAY);

        for (FullPart part : FullPart.values()) {
            builder.append(part.getAlias(), DARK_GRAY)
                    .tooltip(
                            "§eID: " + part.ordinal(),
                            "§eItem: " + part.getMaterial().name(),
                            "§eAlias: " + part.getAlias()
                    )
                    .append(", ", GRAY, ComponentBuilder.FormatRetention.NONE);
        }

        ComponentSender.sendTo(builder, sender);
        return true;
    }
}
