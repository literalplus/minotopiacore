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

import li.l1t.mtc.api.chat.ChatConstants;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.helper.MTCHelper;
import li.l1t.mtc.module.chat.globalmute.GlobalMuteModule;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Manages a quiz game.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 14.11.14
 */
public class QuizGame {
    private final QuizModule module;
    private final GlobalMuteModule globalMuteModule;
    private QuizQuestion currentQuestion;
    private boolean wasGlobalMute = false; //Whether global mute was enabled when the quiz was started

    public QuizGame(QuizModule module, GlobalMuteModule globalMuteModule) {
        this.module = module;
        this.globalMuteModule = globalMuteModule;
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
        startGlobalMute();
    }

    private void startGlobalMute() {
        if (!isGlobalMuteSupported()) {
            return;
        }
        wasGlobalMute = globalMuteModule.isGlobalMute();
        if (wasGlobalMute) {
            globalMuteModule.disableGlobaleMute();
        }
    }

    private boolean isGlobalMuteSupported() {
        return globalMuteModule != null;
    }

    public void reset(Player winner) {
        Validate.isTrue(currentQuestion != null, "Cannot reset when not running!");
        BaseComponent[] winnerComponents = ChatConstants.prefixBuilder()
                .append(winner.getName(), ChatColor.YELLOW).tooltip("kopieren").suggest(winner.getName())
                .append(" hat ", ChatColor.GOLD).tooltip("UUID kopieren: " + winner.getUniqueId().toString())
                .append("gewonnen. Die richtige Antwort war:")
                .suggest(winner.getUniqueId().toString()).create();
        module.getPlugin().getServer().getOnlinePlayers()
                .forEach(receiver -> receiver.spigot().sendMessage(winnerComponents));
        MessageType.BROADCAST.broadcast(module.getPlugin().getServer(), "§d%s", getCurrentQuestion().getAnswer());
        currentQuestion = null;
        resetGlobalMute();
    }

    private void resetGlobalMute() {
        if (isGlobalMuteSupported() && wasGlobalMute) {
            globalMuteModule.enableGlobalMute("WahrFalsch");
        }
    }

    public boolean abort(String reason) {
        Bukkit.broadcastMessage(MTCHelper.locArgs("XU-qzabort", "CONSOLE", false) +
                (reason == null ? "" : " §7Grund: " + reason));
        module.setGame(null);
        resetGlobalMute();
        return true;
    }
}
