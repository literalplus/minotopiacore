/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.fulltag.dist;

import com.google.common.collect.ImmutableList;
import li.l1t.common.chat.ComponentSender;
import li.l1t.common.chat.XyComponentBuilder;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.misc.cmd.MTCCommandExecutor;
import li.l1t.mtc.module.fulltag.FullTagModule;
import li.l1t.mtc.module.fulltag.model.FullData;
import li.l1t.mtc.module.fulltag.model.FullInfo;
import li.l1t.mtc.module.fulltag.model.LegacyFullData;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.apache.logging.log4j.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.md_5.bungee.api.ChatColor.*;

/**
 * Command executor for a command that allows players to retrieve full items created for them if
 * they were not able to receive them at creation time, for example due to their inventory being
 * full or due to them being offline.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 16/09/15
 */
public class CommandRetrieveFull extends MTCCommandExecutor {
    private static final Logger LOGGER = LogManager.getLogger(CommandRetrieveFull.class);
    private final FullTagModule module;

    public CommandRetrieveFull(FullTagModule module) {
        this.module = module;
    }

    @Override
    public boolean catchCommand(@Nonnull CommandSender sender, String senderName, Command cmd, String label, @Nonnull String[] args) {
        Player plr = (Player) sender;

        if (args.length != 0) {
            switch (args[0].toLowerCase()) {
                case "list":
                    return interpretList(plr);
                case "get":
                    return interpretGet(plr);
                case "info": //fullreturn command
                    return interpretInfo(plr);
                case "accept": //fullreturn
                    return interpretAccept(plr);
                case "help":
                    break;
                default:
                    sender.sendMessage(String.format("§cUnbekannte Aktion '%s'.", args[0]));
            }
        }

        if ("fullreturn".equalsIgnoreCase(label)) { //legacy behaviour - need to keep consistency
            plr.spigot().sendMessage(
                    new XyComponentBuilder("§eMit diesem Befehl kannst du Fulls von früheren Maps erneut anfordern.")
                            .color(YELLOW).create()
            );
            plr.spigot().sendMessage(
                    new XyComponentBuilder("/fullreturn info").color(GOLD)
                            .append(" - Zeigt an, ob du Fulls erstattet bekommen kannst.").color(YELLOW).create()
            );
            return true;
        }

        plr.sendMessage("§eMit diesem Befehl kannst du für dich bereitgestellte Fulls abholen.");
        plr.spigot().sendMessage(
                new XyComponentBuilder("/" + label + " list ").color(YELLOW)
                        .command("/canhasfull list")
                        .tooltip("§eHier klicken zum Ausführen:", "/" + label + " list")
                        .append("Zeigt für dich bereitgestellte Fulls an.", GOLD)
                        .create()
        );
        plr.spigot().sendMessage(
                new XyComponentBuilder("/" + label + " get ").color(YELLOW)
                        .command("/canhasfull get")
                        .tooltip("§eHier klicken zum Ausführen:", "/" + label + " get")
                        .append("Gibt dir so viele für dich bereitgestellte Fulls, wie in dein Inventar passen.",
                                GOLD)
                        .create()
        );

        return true;
    }

    private boolean interpretAccept(Player plr) {
        if (!checkReturnAllowed(plr)) {
            return true;
        }

        module.getPlugin().getServer().getScheduler().runTaskAsynchronously(module.getPlugin(), () -> {
            List<LegacyFullData> legacyData = module.getLegacyRepository()
                    .findByCurrentUniqueId(plr.getName(), plr.getUniqueId());
            List<FullData> eligibleFullData = findReturnEligibleFullData(plr);
            int initialSize = legacyData.size() + eligibleFullData.size();

            legacyData.forEach(ld -> LOGGER.info("Attempting to return {} to {}.", ld, plr.getName()));
            eligibleFullData.forEach(fd -> LOGGER.info("Attempting to return {} to {}.", fd, plr.getName()));

            ComponentSender.sendToSync(new XyComponentBuilder("Du bekommst ", YELLOW)
                    .append(String.valueOf(initialSize), GOLD)
                    .append(" Fullitems... (Bitte warten)", YELLOW)
                    .create(), plr, module.getPlugin());

            List<FullInfo> migratedInfos = new ArrayList<>(legacyData).stream()
                    .map(ld -> {
                        try {
                            FullData fullData = ld.toFullData(module.getRepository());
                            return module.getRegistry().create(fullData, plr.getLocation());
                        } catch (RuntimeException e) {
                            LOGGER.info(String.format(
                                    "Encountered exception while returning legacy full %s: ",
                                    ld == null ? "null?!" : ld.getId()), e);
                            legacyData.remove(ld); //Prevent it from being deleted
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            migratedInfos.addAll(
                    eligibleFullData.stream()
                            .map(data -> module.getRegistry().create(data, plr.getLocation()))
                            .collect(Collectors.toList())
            );

            legacyData.forEach(module.getLegacyRepository()::delete);

            module.getPlugin().getServer().getScheduler().runTask(module.getPlugin(), () -> {
                migratedInfos.forEach(info -> module.getDistributionManager().requestStore(info, plr));

                module.getDistributionManager().notifyWaiting(plr);
                plr.sendMessage(String.format("§a%d Fullitems erstellt.", initialSize));
                LOGGER.info("Returned legacy ids {} to {}.",
                        legacyData.stream()
                                .map(LegacyFullData::getId)
                                .map(String::valueOf)
                                .collect(Collectors.joining(", ", "[", "]")),
                        plr.getName());
            });
        });
        return true;
    }

    private boolean interpretGet(Player plr) {
        module.getDistributionManager().requestRetrievableFulls(plr, fullInfos -> {
            int remaining = fullInfos.size();
            for (FullInfo info : ImmutableList.copyOf(fullInfos)) { //dunno why this is necessary
                if (!module.getDistributionManager().attemptStore(info, plr)) {
                    plr.sendMessage("§cDu hast keinen Platz mehr in deinem Inventar.");
                    plr.sendMessage(String.format("§2%d §aFullteile übrig.", remaining));
                    plr.sendMessage("§eBitte leere dein Inventar und tippe dann erneut §6/fulls get");
                    break;
                }
                remaining--;
            }
            if (remaining == 0) {
                plr.sendMessage("§aDu hast alle verfügbaren Fullteile erhalten. Viel Spaß damit!");
            }
            module.getDistributionManager().saveStorage();
        });
        return true;
    }

    private boolean interpretList(Player plr) {
        module.getDistributionManager().requestRetrievableFulls(plr, fullInfos -> {
            fullInfos.removeIf(fi -> fi.getData() == null);
            //noinspection ConstantConditions
            fullInfos.forEach(fi -> plr.sendMessage(String.format("§e -> #%d: %s (\"%s\")",
                    fi.getId(), fi.getData().getPart().getAlias(), fi.getData().getComment())));
            if (fullInfos.isEmpty()) {
                plr.sendMessage("§eAuf dich warten leider keine Fullteile. =(");
            } else {
                plr.sendMessage("§aAuf dich warten §2" + fullInfos.size() + " §aFullteile!");
                plr.spigot().sendMessage(new XyComponentBuilder(
                        "Tippe ").color(YELLOW)
                        .append("/fulls get", GOLD, UNDERLINE)
                        .append(" oder klicke ", YELLOW, ComponentBuilder.FormatRetention.NONE)
                        .append("[hier]", DARK_GREEN, UNDERLINE)
                        .hintedCommand("/fulls get")
                        .append(", um sie zu erhalten!", GOLD, ComponentBuilder.FormatRetention.NONE)
                        .create()
                );
            }
        });
        return true;
    }

    private boolean interpretInfo(Player plr) {
        if (!checkReturnAllowed(plr)) {
            return true;
        }

        List<LegacyFullData> legacyData = findLegacyData(plr);
        List<FullData> notInRegistry = findReturnEligibleFullData(plr);

        boolean fullsAvailable = !legacyData.isEmpty() || !notInRegistry.isEmpty();
        plr.spigot().sendMessage(new XyComponentBuilder(
                "Auf dich warten ", GREEN)
                .append(fullsAvailable ? String.valueOf(legacyData.size() + notInRegistry.size()) : "keine", DARK_GREEN)
                .append(" Fullitems.", GREEN)
                .create());
        if (fullsAvailable) {
            plr.spigot().sendMessage(new XyComponentBuilder(
                    "Tippe ", GOLD)
                    .append("/fullreturn accept", YELLOW)
                    .hintedCommand("/fullreturn accept")
                    .append(" oder klicke ", GOLD)
                    .append("[hier]", DARK_GREEN, UNDERLINE)
                    .hintedCommand("/fullreturn accept")
                    .append(", um die Items anzufordern.", GOLD, ComponentBuilder.FormatRetention.NONE)
                    .create()
            );
        }
        return true;
    }

    private List<FullData> findReturnEligibleFullData(Player plr) {
        List<FullData> notInRegistry = module.getRepository().findNotInRegistry(plr.getUniqueId());
        if (module.isFullReturnStrict()) {
            notInRegistry = notInRegistry.stream()
                    .filter(data -> data.getHolderId() == null || data.getHolderId().equals(plr.getUniqueId()))
                    .collect(Collectors.toList());
        }
        return notInRegistry;
    }

    private List<LegacyFullData> findLegacyData(Player plr) {
        List<LegacyFullData> legacyData = module.getLegacyRepository()
                .findByCurrentUniqueId(plr.getName(), plr.getUniqueId());
        legacyData.forEach(ld -> plr.spigot().sendMessage(new XyComponentBuilder(
                " -> ", YELLOW)
                .append("#" + ld.getId() + ": ", GRAY)
                .append(ld.getPartName())
                .append(" (" + ld.getSenderName() + "->" + ld.getReceiverName() + ") ")
                .append(ld.getComment(), YELLOW)
                .create())
        );
        return legacyData;
    }

    private boolean checkReturnAllowed(CommandSender sender) {
        if (!module.isFullReturnEnabled()) {
            sender.sendMessage("§cDu kannst momentan noch keine Fullitems zurückerhalten. " +
                    "Bitte gedulde dich, bis diese Option für alle Spieler freigeschalten wird.");
            return false;
        }
        return true;
    }
}
