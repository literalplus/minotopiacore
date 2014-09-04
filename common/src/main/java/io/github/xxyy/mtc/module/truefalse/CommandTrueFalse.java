package io.github.xxyy.mtc.module.truefalse;

import mkremins.fanciful.FancyMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.xxyy.common.misc.XyLocation;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.common.util.StringHelper;
import io.github.xxyy.common.util.inventory.ItemStackFactory;
import io.github.xxyy.mtc.helper.MTCHelper;

/**
 * Provides a text-based front-end for true/false, with admin and player features.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 4.9.14
 */
public class CommandTrueFalse implements CommandExecutor {
    private final TrueFalseModule module;

    public CommandTrueFalse(TrueFalseModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (sender instanceof Player) {
                switch (args[0].toLowerCase()) {
                    case "join":
                        if (!CommandHelper.checkPermAndMsg(sender, "mtc.truefalse.join", label)) {
                            return true;
                        }

                        if (!module.isGameOpen()) {
                            MTCHelper.sendLoc("XU-tfastart", sender, false);
                            return true;
                        }

                        module.getGame().addParticipant((Player) sender);
                        return true;
                    case "leave":
                        sender.sendMessage("§6/spawn");
                        return true;
                    case "wand":
                        if (!CommandHelper.checkPermAndMsg(sender, TrueFalseModule.ADMIN_PERMISSION, label)) {
                            return true;
                        }

                        ((Player) sender).getInventory().addItem(
                                new ItemStackFactory(TrueFalseModule.MAGIC_WAND_MATERIAL)
                                        .displayName(TrueFalseModule.MAGIC_WAND_NAME)
                                        .lore("§7Right-click a block to set the 1.boundary!")
                                        .produce()
                        );
                        module.boundarySessions.add(((Player) sender).getUniqueId());
                        sender.sendMessage("§eHier ist deine Zaubermelone!");
                        return true;
                }
            }
            if (sender.hasPermission(TrueFalseModule.ADMIN_PERMISSION)) {
                switch (args[0].toLowerCase()) {
                    case "new":
                        if (module.getGame() != null) {
                            sender.sendMessage("§cEs läuft noch ein Spiel! Brich dieses mit §4/wf abort §cab.");
                            return true;
                        }

                        module.setGame(new TrueFalseGame(module));
                        return true;
                    case "start":
                        if (module.getGame() == null) {
                            sender.sendMessage("§cEs läuft kein Spiel! Verwende §4/wf new§c!");
                            return true;
                        } else if(!module.getGame().getState().equals(TrueFalseGame.State.TELEPORT)) {
                            sender.sendMessage("§cDas Spiel wurde bereits gestartet!");
                            return true;
                        }

                        module.getGame().start();
                        return true;
                    case "abort":
                        if (module.getGame() == null) {
                            sender.sendMessage("§cEs läuft kein Spiel! Verwende §4/wf new§c!");
                            return true;
                        }

                        if (module.getGame().abort()) {
                            sender.sendMessage("§aDas Spiel wurde abgebrochen.");
                        } else {
                            sender.sendMessage("§aDas Spiel wird nach dieser Frage beendet!");
                        }
                        sender.sendMessage("§eBeachte, dass Spieler nicht automatisch zurückteleportiert werden!");
                        return true;
                    case "addq":
                        if (args.length < 3) {
                            sender.sendMessage("§cZu wenige Argumente!");
                            break;
                        }

                        boolean answer;
                        switch (args[1]) {
                            case "true":
                            case "ja":
                            case "wahr":
                            case "yes":
                            case "yea":
                            case "y":
                                answer = true;
                                break;
                            case "false":
                            case "nein":
                            case "falsch":
                            case "no":
                                answer = false;
                                break;
                            default:
                                sender.sendMessage("§cDie Antwort kann nur 'ja' oder 'nein' sein!");
                                return true;
                        }

                        String text = StringHelper.varArgsString(args, 2, true);
                        module.getQuestions().add(new TrueFalseQuestion(text, answer));
                        sender.sendMessage("§aFrage hinzugefügt: §e" + text);
                        return true;
                    case "remq":
                        if(args.length < 2) {
                            sender.sendMessage("§cZu wenige Parameter!");
                            return true;
                        }

                        int index;
                        if(!StringUtils.isNumeric(args[1])) {
                            sender.sendMessage("§cDas zweite Argument muss eine Zahl sein!");
                            return true;
                        }
                        index = Integer.parseInt(args[1]);

                        if(module.getQuestions().size() <= index) {
                            sender.sendMessage("§eSo viele Antworten haben wir nicht! ("+ module.getQuestions()+" vorhanden)");
                            return true;
                        }

                        module.getQuestions().remove(index);
                        sender.sendMessage("§aAntwort gelöscht.");
                        return true;
                    case "listq":
                        if(module.getQuestions().isEmpty()) {
                            sender.sendMessage("§eEs sind keine Fragen mehr gespeichert!");
                            return true;
                        }

                        int i = 0;
                        for(TrueFalseQuestion question : module.getQuestions()) {
                            new FancyMessage("#"+i+" ").color(ChatColor.GOLD)
                                    .then(question.getText()+" ").color(question.getAnswer() ? ChatColor.GREEN : ChatColor.RED)
                                    .then("[ - ]").style(ChatColor.UNDERLINE).color(ChatColor.DARK_RED)
                                    .tooltip("/wf remq").suggest("/wf remq").send(sender);
                        }
                        return true;
                    case "next":
                        if (module.getGame() == null) {
                            sender.sendMessage("§cEs läuft kein Spiel! Verwende §4/wf new§c!");
                            return true;
                        }
                        module.getGame().nextQuestion();
                        return true;
                    case "spawn":
                        module.setSpawn(new XyLocation(((Player) sender).getLocation()));
                        sender.sendMessage("§aDer Spawn wurde auf deine Position gesetzt!");
                        return true;
                }
            }
        }

        sender.sendMessage("§9/wf join §2Betritt ein offenes W/F-Spiel");
        if(sender.hasPermission(TrueFalseModule.ADMIN_PERMISSION)) {
            sender.sendMessage("§9/wf wand §2Gibt dir ein Tool, mit dem du die Ränder der zu Entfernenden Fläche markieren kannst");
            sender.sendMessage("§9/wf new §2Öffnet ein neues W/F-Spiel");
            sender.sendMessage("§9/wf start §2Startet ein neues W/F-Spiel");
            sender.sendMessage("§9/wf abort §2Bricht das laufende W/F-Spiel ab");
            sender.sendMessage("§9/wf addq <ja|nein> <Frage> §2Fügt eine Frage hinzu");
            sender.sendMessage("§9/wf remq <Index> §2Fügt eine Frage hinzu");
            sender.sendMessage("§9/wf listq §2Zeigt alle Fragen im Backlog an");
            sender.sendMessage("§9/wf next §2Stellt die nächste Frage");
            sender.sendMessage("§9/wf spawn §2Setzt den W/F-Spawn");
        }
        return true;
    }
}
