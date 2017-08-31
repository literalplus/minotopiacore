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

package li.l1t.mtc.module.quiz;

import li.l1t.common.chat.ComponentSender;
import li.l1t.common.chat.XyComponentBuilder;
import li.l1t.common.util.CommandHelper;
import li.l1t.common.util.StringHelper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Provides a text-based front-end to the quiz module, with admin and player features.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 14.11.14
 */
class CommandQuiz implements CommandExecutor {
    private final QuizModule module;

    public CommandQuiz(QuizModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "info":
                    if (module.hasActiveGame()) {
                        sender.sendMessage("§9Aktuelle Frage:");
                        sender.sendMessage("§d" + module.getGame().getCurrentQuestion().getText());
                    } else {
                        sender.sendMessage("§cEs läuft momentan kein Quiz!");
                    }
                    return true;
            }
            if (sender.hasPermission(QuizModule.ADMIN_PERMISSION)) {
                switch (args[0].toLowerCase()) {
                    case "start":
                        if (module.hasActiveGame()) {
                            sender.sendMessage("§cDas Quiz wurde bereits gestartet! §6/quiz abort");
                            sender.sendMessage("§6Aktuelle Frage: " + module.getGame().getCurrentQuestion());
                            return true;
                        }

                        QuizQuestion question = null;
                        if (args.length >= 2) {
                            if (!StringUtils.isNumeric(args[1]) && !args[1].equals("r")) {
                                sender.sendMessage("§cDas ist keine Zahl: " + args[1]);
                                break;
                            }
                            int questionId = args[1].equals("r") ?
                                    RandomUtils.nextInt(module.getQuestions().size()) : Integer.parseInt(args[1]);
                            if (module.getQuestions().size() > questionId) {
                                question = module.getQuestions().remove(questionId);
                            }
                        } else {
                            question = module.consumeQuestion();
                        }

                        if (question == null) {
                            sender.sendMessage("§cKeine passende Frage gefunden!");
                            break;
                        }

                        module.getGame().setQuestion(question);
                        return true;
                    case "abort":
                        if (!module.hasActiveGame()) {
                            sender.sendMessage("§cEs läuft momentan kein QuiZ!");
                            return true;
                        }

                        module.getGame().abort(args.length > 1 ? StringHelper.varArgsString(args, 1, true) : "");
                        return true;
                    case "addq":
                        if (CommandHelper.kickConsoleFromMethod(sender, label)) {
                            return true;
                        }

                        if (args.length < 2) {
                            sender.sendMessage("§cZu wenige Argumente!");
                            break;
                        }

                        String text = StringHelper.varArgsString(args, 1, true);
                        module.createQuestionSession((Player) sender, text);
                        sender.sendMessage("§9Frage vorgemerkt.");
                        sender.sendMessage("§eTippe die Antwort jetzt in den Chat. (Einfach so, ja!)");
                        return true;
                    case "remq":
                        if (args.length < 2) {
                            sender.sendMessage("§cZu wenige Parameter!");
                            return true;
                        }

                        int index;
                        if (!StringUtils.isNumeric(args[1])) {
                            sender.sendMessage("§cDas zweite Argument muss eine Zahl sein!");
                            return true;
                        }
                        index = Integer.parseInt(args[1]);

                        if (module.getQuestions().size() <= index) {
                            sender.sendMessage("§eSo viele Antworten haben wir nicht! (" + module.getQuestions().size() + " vorhanden)");
                            return true;
                        }

                        module.getQuestions().remove(index);
                        module.save();
                        sender.sendMessage("§aMarcel Davis von 1&1 hat diese Antwort gegessen.");
                        return true;
                    case "listq":
                        if (module.getQuestions().isEmpty()) {
                            sender.sendMessage("§eEs sind keine Fragen mehr gespeichert!");
                            return true;
                        }

                        int i = 0;
                        for (QuizQuestion q : module.getQuestions()) { //name chosen because of `question` variable already in scope
                            ComponentSender.sendTo(
                                    new XyComponentBuilder("#" + i + " ", ChatColor.GOLD)
                                            .append(q.getText(), ChatColor.GOLD, ChatColor.UNDERLINE)
                                            .tooltip("Antwort:", q.getAnswer())
                                            .append(" ", ComponentBuilder.FormatRetention.NONE)
                                            .append("[-]", ChatColor.DARK_RED, ChatColor.UNDERLINE)
                                            .hintedCommand("/qz remq " + i)
                                            .append(" ", ComponentBuilder.FormatRetention.NONE)
                                            .append("[~]", ChatColor.DARK_GREEN, ChatColor.UNDERLINE)
                                            .hintedCommand("/qz start " + i++), sender
                            );
                        }

                        ComponentSender.sendTo(
                                new XyComponentBuilder("[Quiz mit zufälliger Frage starten]", ChatColor.GOLD)
                                        .underlined(true)
                                        .hintedCommand("/qz start r"), sender
                        );
                        return true;
                }
            }
        }

        sender.sendMessage("§9/qz info §2Zeigt Informationen zum aktuellen Quiz");
        if (sender.hasPermission(QuizModule.ADMIN_PERMISSION)) {
            sender.sendMessage("§9/qz start §2Startet ein neues Quiz mit der ersten Frage");
            sender.sendMessage("§9/qz start <Index> §2Startet ein neues Quiz mit einer spezifischen Frage");
            sender.sendMessage("§9/qz abort [Grund] §2Bricht das laufende Quiz ab");
            sender.sendMessage("§9/qz addq <Frage> §2Fügt eine Frage hinzu");
            sender.sendMessage("§9/qz remq <Index> §2Entfernt eine Frage");
            sender.sendMessage("§9/qz listq §2Zeigt alle verfügbaren Fragen an");
        }
        return true;
    }
}
