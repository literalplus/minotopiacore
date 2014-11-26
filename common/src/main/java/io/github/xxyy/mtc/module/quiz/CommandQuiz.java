/*
 * Copyright (c) 2013-2014.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.quiz;

import mkremins.fanciful.FancyMessage;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.common.util.StringHelper;

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
                    if(module.hasActiveGame()) {
                        sender.sendMessage("§9Aktuelle Frage:");
                        sender.sendMessage("§d"+module.getGame().getCurrentQuestion().getText());
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
                        if(CommandHelper.kickConsoleFromMethod(sender, label)) {
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
                            //@formatter:off
                            new FancyMessage("#" + i + " ").color(ChatColor.GOLD)
                                    .then(q.getText() + " ").color(ChatColor.GOLD).style(ChatColor.UNDERLINE)
                                        .tooltip("Antwort:", q.getAnswer())
                                    .then("[-]").style(ChatColor.UNDERLINE).color(ChatColor.DARK_RED)
                                        .tooltip("/qz remq " + i)
                                        .suggest("/qz remq " + i)
                                    .then(" ")
                                    .then("[~]").style(ChatColor.UNDERLINE).color(ChatColor.DARK_GREEN)
                                        .tooltip("/qz start " + i)
                                        .suggest("/qz start " + i++)
                                    .send(sender);
                            //@formatter:on
                        }

                        new FancyMessage("[Quiz mit zufälliger Frage starten]")
                                .style(ChatColor.UNDERLINE).color(ChatColor.GOLD)
                                .tooltip("/qz start r")
                                .suggest("/qz start r")
                                .send(sender);

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
