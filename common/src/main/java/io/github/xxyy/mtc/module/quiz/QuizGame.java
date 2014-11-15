package io.github.xxyy.mtc.module.quiz;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.github.xxyy.common.util.CommandHelper;
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

    public QuizGame(QuizModule module) {
        this.module = module;
    }

    public boolean nextQuestion() {
        Validate.isTrue(currentQuestion == null, "Cannot override question!");
        if (module.hasQuestion()) {
            setQuestion(module.consumeQuestion());
            return true;
        }
        CommandHelper.broadcast(MTCHelper.loc("XU-tfnq", false), QuizModule.ADMIN_PERMISSION);
        return false;
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
    }

    public void reset(Player winner) {
        Validate.isTrue(currentQuestion != null, "Cannot reset when not running!");
        module.getPlugin().getServer().broadcastMessage(MTCHelper.locArgs("XU-qzanswer", "CONSOLE", false,
                winner.getName(), getCurrentQuestion().getAnswer()));
        currentQuestion = null;
    }

    public boolean abort(String reason) {
        Bukkit.broadcastMessage(MTCHelper.locArgs("XU-qzabort", "CONSOLE", false) +
                (reason == null ? "" : " §7Grund: " + reason));
        module.setGame(null);
        return true;
    }
}
