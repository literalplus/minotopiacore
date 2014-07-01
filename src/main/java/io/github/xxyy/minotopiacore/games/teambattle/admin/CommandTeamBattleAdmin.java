package io.github.xxyy.minotopiacore.games.teambattle.admin;

import io.github.xxyy.common.HelpManager;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.games.teambattle.CommandTeamBattleHelper;
import io.github.xxyy.minotopiacore.games.teambattle.TeamBattle;
import io.github.xxyy.minotopiacore.games.teambattle.TeamBattleSign;
import io.github.xxyy.minotopiacore.games.teambattle.TeamBattleTeams;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class CommandTeamBattleAdmin implements CommandExecutor {

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!CommandHelper.checkPermAndMsg(sender, "mtc.teambattle.admin", label)) {
            return true;
        }
        if (args.length == 0) {
            HelpManager.tryPrintHelp("waradmin", sender, label, "", "mts help wa");
        } else {
            if (args.length == 1) {
                //enable,disable,savecfg
                switch (args[0]) {
                case "savecfg":
                    if (!CommandHelper.checkPermAndMsg(sender, "mtc.teambattle.admin.cmd.savecfg", label + " savecfg")) {
                        return true;
                    }
                    TeamBattle.instance().saveConfig();
                    sender.sendMessage("§6Config gespeichert.");
                    return true;
                case "rldcfg":
                    if (!CommandHelper.checkPermAndMsg(sender, "mtc.teambattle.admin.cmd.rldcfg", label + " rldcfg")) {
                        return true;
                    }
                    TeamBattle.instance().reloadConfig();
                    sender.sendMessage("§6Config neu geladen.");
                    return true;
                case "stopgame":
                    sender.sendMessage("§7DEPRECATED! Use §3/wa kick all §7instead.");
                    return true;
                case "setkit":
                    if (!CommandHelper.checkPermAndMsg(sender, "mtc.teambattle.admin.cmd.setkit", label + " setkit")) {
                        return true;
                    }
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("§7Du kannst diesen Befehl nur als Spieler benutzen!");
                        return true;
                    }
                    TeamBattle.instance().setKit((Player) sender);
                    sender.sendMessage(TeamBattle.CHAT_PREFIX + " Kit gesetzt.");
                    return true;
                case "setbkit":
                    if (!CommandHelper.checkPermAndMsg(sender, "mtc.teambattle.admin.cmd.setbkit", label + " setkit")) {
                        return true;
                    }
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("§7Du kannst diesen Befehl nur als Spieler benutzen!");
                        return true;
                    }
                    TeamBattle.instance().setBetterKit((Player) sender);
                    sender.sendMessage(TeamBattle.CHAT_PREFIX + " Besseres Kit gesetzt.");
                    return true;
                case "setsign":
                    if (!CommandHelper.checkPermAndMsg(sender, "mtc.teambattle.admin.cmd.setsign", label + " setsign")) {
                        return true;
                    }
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("§7Du kannst diesen Befehl nur als Spieler benutzen!");
                        return true;
                    }
                    Player plr = (Player) sender;
                    @SuppressWarnings("deprecation") List<Block> blks = plr.getLastTwoTargetBlocks(null, 120); //This being marked as deprecated does not
                    Block target = blks.get(1);
                    //System.out.println(target.toString());
                    if (!(target.getType() == Material.WALL_SIGN) && !(target.getType() == Material.SIGN_POST)) {
                        sender.sendMessage("§8Das nennst du ein Schild?!");
                        return true;
                    }
                    TeamBattleSign.setSignLoc(target.getWorld().getName(), target.getX(), target.getY(), target.getZ());
                    sender.sendMessage("§7Schild gesetzt.");
                    return true;
                case "rldsign":
                    if (!CommandHelper.checkPermAndMsg(sender, "mtc.teambattle.admin.cmd.rldsign", label + " rldsign")) {
                        return true;
                    }
                    TeamBattleSign.updateSign();
                    sender.sendMessage("§7Schild aktualisiert.");
                    break;
                case "setlobby":
                    if (!CommandHelper.checkPermAndMsg(sender, "mtc.teambattle.admin.cmd.setlobby", label + " setlobby")) {
                        return true;
                    }
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("§7Du kannst dieses Kommando nur als Spieler benutzen.");
                        return true;
                    }
                    TeamBattle.instance().setLobbySpawn((Player) sender);
                    sender.sendMessage(TeamBattle.CHAT_PREFIX + " Lobbyspawn gesetzt.");
                    return true;
                default:
                    HelpManager.tryPrintHelp("waradmin", sender, label, "", "xyu help wa");
                }
            } else {
                if (args.length == 2) {
                    //setspawn
                    switch (args[0]) {
                    case "setspawn":
                        if (!CommandHelper.checkPermAndMsg(sender, "mtc.teambattle.admin.cmd.setspawn", label + " " + args[0])) {
                            return true;
                        }
                        CommandHelper.kickConsoleFromMethod(sender, label + " setspawn");
                        System.out.println("'" + args[1] + "'");
                        TeamBattleTeams team;
                        if (args[1].equalsIgnoreCase("blue")) {
                            team = TeamBattleTeams.Blue;
                        } else {
                            if (args[1].equalsIgnoreCase("red")) {
                                team = TeamBattleTeams.Red;
                            } else {
                                sender.sendMessage("§7Invalides Team! §3/" + label + " setspawn (red|blue)");
                                return true;
                            }
                        }
                        TeamBattle.instance().setTeamSpawn(team, (Player) sender);
                        sender.sendMessage(TeamBattle.CHAT_PREFIX + " Spawn des Teams §3'" + args[1] + "'§7 gesetzt.");
                        return true;
                    case "wingame":
                        if (!CommandHelper.checkPermAndMsg(sender, "mtc.teambattle.admin.cmd.wingame", label + " wingame")) {
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("blue")) {
                            TeamBattle.instance().doEndGame(TeamBattleTeams.Blue);
                        } else {
                            if (args[1].equalsIgnoreCase("red")) {
                                TeamBattle.instance().doEndGame(TeamBattleTeams.Red);
                            } else {
                                sender.sendMessage("§7Invalides Team! §3/" + label + " wingame (red|blue)");
                            }
                        }
                        return true;
                    case "forcejoin":
                        if (!CommandHelper.checkPermAndMsg(sender, "mtc.teambattle.admin.msg.forcejoin", label + " forcejoin")) {
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("all")) {
                            for (Player item : Bukkit.getOnlinePlayers()) {
                                CommandTeamBattleHelper helper = new CommandTeamBattleHelper(item, args, label);
                                helper.plr = item;
                                if (TeamBattle.instance().isPlayerInGame(item)) {
                                    continue;
                                }
                                if (helper.preChecks()) {
                                    continue;
                                }
                                helper.tryJoinGame();
                                item.sendMessage(TeamBattle.CHAT_PREFIX + " §3" + sender.getName() + " §8hat dich dem TeamBattle hinzugefügt.");
                            }
                            sender.sendMessage(TeamBattle.CHAT_PREFIX + " Alle Spieler wurden ins TeamBattle teleportiert.");
                            return true;
                        }
                        Player item = Bukkit.getPlayer(args[1]);
                        CommandTeamBattleHelper helper = new CommandTeamBattleHelper(item, args, label);
                        helper.plr = item;
                        helper.tryJoinGame();
                        item.sendMessage(TeamBattle.CHAT_PREFIX + " §3" + sender.getName() + " §6hat dich dem TeamBattle hinzugefügt.");
                        return true;
                    case "kick":
                        if (!CommandHelper.checkPermAndMsg(sender, "mtc.teambattle.admin.cmd.kick", label + " kick")) {
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("all")) {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                CommandTeamBattleHelper helper1 = new CommandTeamBattleHelper(p, args, label);
                                helper1.plr = p;
                                if (!TeamBattle.instance().isPlayerInGame(p)) {
                                    continue;
                                }
                                helper1.leaveGame();
                                p.sendMessage(TeamBattle.CHAT_PREFIX + " §3" + sender.getName() + " §6hat dich aus dem TeamBattle entfernt.");
                            }
                            sender.sendMessage(TeamBattle.CHAT_PREFIX + " Alle Spieler wurden aus dem TeamBattle entfernt.");
                            return true;
                        }
                        Player p = Bukkit.getPlayer(args[1]);
                        CommandTeamBattleHelper helper1 = new CommandTeamBattleHelper(p, args, label);
                        helper1.plr = p;
                        helper1.leaveGame();
                        p.sendMessage(TeamBattle.CHAT_PREFIX + " §3" + sender.getName() + " §6hat dich aus dem TeamBattle entfernt.");
                        sender.sendMessage("§3" + p.getName() + " §6wurde aus dem TeamBattle entfernt.");
                        return true;
                    case "addpoint":
                        if (!CommandHelper.checkPermAndMsg(sender, "mtc.teambattle.admin.cmd.addpoint", label + " addpoint")) {
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("red")) {
                            TeamBattle.instance().addTeamPoint(TeamBattleTeams.Red);
                        } else {
                            TeamBattle.instance().addTeamPoint(TeamBattleTeams.Blue);
                        }
                        sender.sendMessage("§6Punkt hinzugef§gt.");
                        return true;
                    default:
                        HelpManager.tryPrintHelp("waradmin", sender, label, "", "xyu help wa");
                    }
                } else {
                    if (args.length == 3) {
                        switch (args[0]) {
                        case "simkill":
                            if (!CommandHelper.checkPermAndMsg(sender, "mtc.teambattle.admin.cmd.simkill", label + " simkill")) {
                                return true;
                            }
                            Player killer = Bukkit.getPlayer(args[1]);
                            Player plr = Bukkit.getPlayer(args[2]);
                            if (!TeamBattle.instance().isPlayerInGame(killer)) {
                                return true;
                            }
                            if (!TeamBattle.instance().isPlayerInGame(plr)) {
                                return true;
                            }
                            TeamBattle.instance().addTeamPoint(TeamBattle.instance().invertTeam(TeamBattle.instance().getPlayerTeam(plr)));
                            TeamBattle.instance().notifyPlayersKill(plr, killer);
                            sender.sendMessage("§6Bei der Ausführung dieses Befehls sind keine Kühe zu Schaden gekommen.");
                            sender.sendMessage("§bVielleicht könnte man jedoch etwas mit Blitzen arrangieren.");
                            break;
                        default:
                            HelpManager.tryPrintHelp("waradmin", sender, label, "", "mts help wa");
                        }
                    } else {
                        HelpManager.tryPrintHelp("waradmin", sender, label, "", "mts help wa");
                    }
                }
            }
        }
        return true;
    }
}
