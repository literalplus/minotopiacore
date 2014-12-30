/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.quiz;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;

/**
 * You want a quiz? 'Cause I got them quizzes!
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 14.11.14
 */
public class QuizModule extends ConfigurableMTCModule {
    public static final String NAME = "Quiz";
    private static final String QUESTION_PATH = "questions";
    public static final String ADMIN_PERMISSION = "mtc.quiz.admin";
    private final Map<UUID, QuizQuestion.Builder> questionSessions = Collections.synchronizedMap(new HashMap<>());
    private List<QuizQuestion> questions = Collections.synchronizedList(new LinkedList<>());
    private QuizGame game;

    public QuizModule() {
        super(NAME, "modules/quiz.conf.yml", ClearCacheBehaviour.SAVE);
        ConfigurationSerialization.registerClass(QuizQuestion.class);
    }

    @Override
    public void enable(MTC plugin) {
        super.enable(plugin);

        plugin.getCommand("quiz").setExecutor(new CommandQuiz(this));
        plugin.getServer().getPluginManager().registerEvents(new EventListener(), plugin);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void reloadImpl() {
        questions = (List<QuizQuestion>) configuration.getList(QUESTION_PATH, questions);
    }

    @Override
    public void save() {
        configuration.set(QUESTION_PATH, questions);
        super.save();
    }

    /**
     * @return whether there are any questions left to be asked
     */
    public boolean hasQuestion() {
        return !questions.isEmpty();
    }

    public QuizQuestion consumeQuestion() {
        if (hasQuestion()) {
            return questions.remove(0);
        }
        save();
        return null;
    }

    public List<QuizQuestion> getQuestions() {
        return questions;
    }

    public boolean hasGame() {
        return game != null;
    }

    public boolean hasActiveGame() {
        return hasGame() && getGame().hasQuestion();
    }

    public QuizGame getGame() {
        if (game == null) {
            game = new QuizGame(this); //hmmmmmm
        }
        return game;
    }

    public void setGame(QuizGame game) {
        this.game = game;
    }

    void createQuestionSession(Player plr, String question) {
        QuizQuestion.Builder builder = new QuizQuestion.Builder();
        builder.text(question);
        questionSessions.put(plr.getUniqueId(), builder);
    }

    private class EventListener implements Listener {
        @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true) //main chat listens on HIGH
        public void onChat(AsyncPlayerChatEvent evt) { //Ignores if message goes to proper clan chat or anything - probability is probably low, also the impl would cost this much: |-------| performance and complexity
            if (!evt.getMessage().startsWith("#") && hasActiveGame()) {
                Matcher matcher = getGame().getCurrentQuestion().matcher(ChatColor.stripColor(evt.getMessage()));
                if (matcher.find()) {
                    evt.setMessage("§7" + matcher.replaceFirst("§c§n$0§7"));
                    getGame().reset(evt.getPlayer());
                }
            }
        }

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true) //Make sure we always catch this one
        public void onChatLow(AsyncPlayerChatEvent evt) { //Handle adding of question answers
            if (questionSessions.containsKey(evt.getPlayer().getUniqueId())) {
                evt.setCancelled(true);
                QuizQuestion.Builder session = questionSessions.remove(evt.getPlayer().getUniqueId());
                session.answer(ChatColor.stripColor(evt.getMessage()));

                session.build(QuizModule.this);
                evt.getPlayer().sendMessage("§9[§dQuiz§9] Frage und Antwort gespeichert!");
                evt.getPlayer().sendMessage("§eDu kannst jetzt wieder normal chatten.");
            }
        }
    }
}
