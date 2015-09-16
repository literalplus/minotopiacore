/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.fulltag.dist;

import com.google.common.collect.ImmutableList;
import io.github.xxyy.common.chat.XyComponentBuilder;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.misc.cmd.MTCCommandExecutor;
import io.github.xxyy.mtc.module.fulltag.FullTagModule;
import io.github.xxyy.mtc.module.fulltag.model.FullInfo;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

import static net.md_5.bungee.api.ChatColor.*;

/**
 * Command executor for a command that allows players to retrieve full items created for them if they were not able
 * to receive them at creation time, for example due to their inventory being full or due to them being offline.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 16/09/15
 */
public class CommandRetrieveFull extends MTCCommandExecutor {
    private final FullTagModule module;

    public CommandRetrieveFull(FullTagModule module) {
        this.module = module;
    }

    @Override
    public boolean catchCommand(@Nonnull CommandSender sender, String senderName, Command cmd, String label, @Nonnull String[] args) {
        if (CommandHelper.kickConsoleFromMethod(sender, label)) {
            return true;
        }
        Player plr = (Player) sender;

        if (args.length != 0) {
            switch (args[0].toLowerCase()) {
                case "list":
                    module.getDistributionManager().requestRetrievableFulls(plr, fullInfos -> {
                        fullInfos.removeIf(fi -> fi.getData() == null);
                        //noinspection ConstantConditions
                        fullInfos.forEach(fi -> plr.sendMessage(String.format("§e -> #%d: %s (\"%s\")",
                                fi.getId(), fi.getData().getPart().getAlias(), fi.getData().getComment())));
                        if (fullInfos.isEmpty()) {
                            plr.sendMessage("§eAuf dich warten leider keine Fullteile. =(");
                        } else {
                            plr.sendMessage("§aAuf dich warten §2" + fullInfos.size() + " §aFullteile!");
                            plr.spigot().sendMessage(
                                    new XyComponentBuilder("Tippe ").color(YELLOW)
                                            .append("/fulls get", GOLD, UNDERLINE)
                                            .append(" oder klicke ", YELLOW, ComponentBuilder.FormatRetention.NONE)
                                            .append("[hier]", DARK_GREEN, UNDERLINE)
                                            .command("/fulls get")
                                            .tooltip("/fulls get")
                                            .append(", um sie zu erhalten!", YELLOW, ComponentBuilder.FormatRetention.NONE)
                                            .create()
                            );
                        }
                    });
                    return true;
                case "get":
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
                case "help":
                    break;
                default:
                    sender.sendMessage(String.format("§cUnbekannte Aktion '%s'.", args[0]));
            }
        }

        plr.sendMessage("§eMit diesem Befehl kannst du für dich bereitgestellte Fulls abholen.");
        plr.spigot().sendMessage(
                new XyComponentBuilder("/" + label + " list ").color(YELLOW)
                        .command("/canhasfull list")
                        .tooltip("§eHier klicken zum Ausführen:", "/" + label + " list")
                        .append("Zeigt für dich bereitgestellte Fulls an.", ChatColor.GOLD)
                        .create()
        );
        plr.spigot().sendMessage(
                new XyComponentBuilder("/" + label + " get ").color(YELLOW)
                        .command("/canhasfull get")
                        .tooltip("§eHier klicken zum Ausführen:", "/" + label + " get")
                        .append("Gibt dir so viele für dich bereitgestellte Fulls, wie in dein Inventar passen.",
                                ChatColor.GOLD)
                        .create()
        );

        return true;
    }
}
