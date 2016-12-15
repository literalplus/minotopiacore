/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.pvpstats;

import li.l1t.common.chat.ComponentSender;
import li.l1t.common.chat.XyComponentBuilder;
import li.l1t.common.util.CommandHelper;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.hook.XLoginHook;
import li.l1t.mtc.module.pvpstats.model.PlayerStats;
import li.l1t.mtc.module.pvpstats.scoreboard.PvPStatsBoardManager;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.md_5.bungee.api.ChatColor.DARK_RED;
import static net.md_5.bungee.api.ChatColor.GOLD;
import static net.md_5.bungee.api.ChatColor.UNDERLINE;
import static net.md_5.bungee.api.ChatColor.YELLOW;

/**
 * Executes the /stats command, which allows users to view their own stats and administrators to
 * manage stats.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-03
 */
public class CommandStats implements CommandExecutor {
    private final PvPStatsModule module;
    @InjectMe(required = false)
    private PvPStatsBoardManager scoreboard;

    @InjectMe
    public CommandStats(PvPStatsModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (CommandHelper.kickConsoleFromMethod(sender, label)) {
                return true;
            }
            Player plr = (Player) sender;
            module.getPlugin().getServer().getScheduler().runTaskAsynchronously(
                    module.getPlugin(),
                    () -> sendPlayerStatsTo(plr, module.getRepository().find(plr))
            );
            return true;
        } else {
            switch (args[0].toLowerCase()) {
                case "top":
                    return handleTop(sender, args);
                case "admin":
                    return handleAdmin(sender, label, args);
                default: //player name, probably
                    XLoginHook.Profile profile = module.getPlugin().getXLoginHook().getBestProfile(args[0]);
                    if (profile == null) {
                        return CommandHelper.msg(String.format(
                                "§cKonnte keinen Spieler mit dem Namen '§4%s§c' finden.", args[0]
                        ), sender);
                    } else {
                        module.getPlugin().getServer().getScheduler().runTaskAsynchronously(
                                module.getPlugin(),
                                () -> sendPlayerStatsTo(
                                        sender,
                                        module.getRepository().findByUniqueId(profile.getUniqueId(), profile.getName())
                                )
                        );
                        return true;
                    }

            }
        }
    }

    private boolean handleAdmin(CommandSender sender, String label, String[] args) {
        if (!CommandHelper.checkPermAndMsg(sender, PvPStatsModule.ADMIN_PERMISSION, label)) {
            return true;
        }

        if (args.length == 1 || !args[1].equalsIgnoreCase("reset")) {
            ComponentSender.sendTo(new XyComponentBuilder("/stats admin reset [Name oder UUID] ", YELLOW)
                    .hintedCommand("/stats admin reset ").append("Setzt Stats zurück."), sender);
            return true;
        }

        XLoginHook.Profile profile = module.getPlugin().getXLoginHook().getBestProfile(args[2]);
        if (profile == null) {
            return CommandHelper.msg(String.format(
                    "§cKonnte keinen Spieler mit dem Namen '§4%s§c' finden.", args[2]
            ), sender);
        }

        PlayerStats playerStats = module.getRepository().findByUniqueId(profile.getUniqueId(), profile.getName());
        if (playerStats.getKills() == playerStats.getDeaths() && playerStats.getDeaths() == 0) {
            return CommandHelper.msg("§cFür diesen Spieler sind keine Stats gespreichert.", sender);
        }

        playerStats.setDeaths(0);
        playerStats.setKills(0);
        module.getRepository().save(playerStats);

        return CommandHelper.msg(String.format(
                "§aDie Stats von %s wurden zurückgesetzt.",
                playerStats.getDisplayName()), sender
        );
    }

    private boolean handleTop(CommandSender sender, String[] args) {
        boolean showKills = true;
        int limit = 10;
        if (args.length >= 2) {
            switch (args[1]) {
                case "kills":
                    break;
                case "deaths":
                    showKills = false;
                    break;
                default:
                    sender.sendMessage("§a/stats top [kills|deaths] [Anzahl]");
                    return true;
            }
            if (args.length > 2) {
                if (!StringUtils.isNumeric(args[2])) {
                    return CommandHelper.msg(String.format(
                            "§cDie Anzahl muss eine Zahl sein, nicht '%s'!", args[2]
                    ), sender);
                }
                limit = Integer.parseInt(args[2]);
                if (limit > 50) {
                    limit = 50;
                }
            }
        }
        CompletableFuture<List<PlayerStats>> future;
        if (showKills) {
            future = module.getRepository().findTopKillers(limit);
        } else {
            future = module.getRepository().findWhoDiedMost(limit);
        }

        String description = limit + (showKills ? " Kills" : " Deaths");
        future.thenAccept(result -> sendTopListTo(sender, result, description));

        return true;
    }

    private boolean sendPlayerStatsTo(CommandSender receiver, PlayerStats stats) {
        receiver.sendMessage(String.format("§9────────── §eStats: §6%s §9──────────",
                stats.getDisplayName()));
        int killsRank = module.getRepository().getKillsRank(stats);
        int deathsRank = module.getRepository().getDeathsRank(stats);
        if (stats.getDeaths() != 0) {
            receiver.sendMessage(String.format("§6Kills: §e%d §6(%d.Platz) / Deaths: §e%d §6(%d.Platz) = K/D: §e%.2f",
                    stats.getKills(), killsRank, stats.getDeaths(), deathsRank, stats.getKDRatio()));
        } else {
            receiver.sendMessage(String.format("§6Kills: §e%d §6(%d.Platz) §6/ Deaths: §e%d §6(%d.Platz)",
                    stats.getKills(), killsRank, stats.getDeaths(), deathsRank));
        }
        if (receiver.hasPermission(PvPStatsModule.ADMIN_PERMISSION)) {
            ComponentSender.sendTo(new XyComponentBuilder("Stats zurücksetzen: ", GOLD)
                    .append("[zurücksetzen]", DARK_RED, UNDERLINE)
                    .tooltip("/stats admin reset " + stats.getUniqueId())
                    .suggest("/stats admin reset " + stats.getUniqueId())
                    .create(), receiver);
        }
        Player subjectPlayer = Bukkit.getPlayer(stats.getUniqueId()); //update scoreboard so that command output is never inconsistent
        if(scoreboard != null && subjectPlayer != null) {
            scoreboard.updateAll(subjectPlayer, stats);
        }
        return true;
    }

    private void sendTopListTo(CommandSender receiver, List<PlayerStats> stats, String description) {
        receiver.sendMessage(String.format("§9────────── §eTop %s §9──────────", description));
        int i = 1;
        for (PlayerStats playerStats : stats) {
            ComponentSender.sendTo(new XyComponentBuilder(i + ". ", ChatColor.GOLD)
                    .hintedCommand("/stats " + playerStats.getUniqueId().toString())
                    .append(playerStats.getDisplayName(), YELLOW)
                    .append(" (" + playerStats.getKills() + " Kills, " + playerStats.getDeaths() + " Deaths)", ChatColor.BLUE)
                    .create(), receiver);
            i++;
        }
    }
}
