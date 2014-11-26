/*
 * Copyright (c) 2013-2014.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.quiz;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.github.xxyy.common.util.ChatHelper;
import io.github.xxyy.mtc.helper.MTCHelper;

/**
 * Manages a quiz game.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 14.11.14
 */
public class QuizGame {
    private final QuizModule module;
    private QuizQuestion currentQuestion;
    private boolean wasGlobalMute = false; //Whether global mute was enabled when the quiz was started

    public QuizGame(QuizModule module) {
        this.module = module;
    }

    public QuizQuestion getCurrentQuestion() {
        return currentQuestion;
    }

    /**
     * @return whether this game has an unanswered question
     */
    public boolean hasQuestion() {
        return getCurrentQuestion() != null;
    }

    public void setQuestion(QuizQuestion question) {
        Validate.isTrue(currentQuestion == null, "Cannot override question!");
        Bukkit.broadcastMessage(MTCHelper.locArgs("XU-qzquestion", "CONSOLE", false, question.getText()));
        currentQuestion = question;

        wasGlobalMute = ChatHelper.isGlobalMute;
        if(wasGlobalMute) {
            ChatHelper.isGlobalMute = false;
        }
    }

    public void reset(Player winner) {
        Validate.isTrue(currentQuestion != null, "Cannot reset when not running!");
        module.getPlugin().getServer().broadcastMessage(MTCHelper.locArgs("XU-qzanswer", "CONSOLE", false,
                winner.getName(), getCurrentQuestion().getAnswer()));
        currentQuestion = null;

        if(wasGlobalMute) {
            ChatHelper.isGlobalMute = true;
        }
        wasGlobalMute = false;
    }

    public boolean abort(String reason) {
        Bukkit.broadcastMessage(MTCHelper.locArgs("XU-qzabort", "CONSOLE", false) +
                (reason == null ? "" : " §7Grund: " + reason));
        module.setGame(null);

        if(wasGlobalMute) {
            ChatHelper.isGlobalMute = true;
        }
        wasGlobalMute = false;

        return true;
    }
}
