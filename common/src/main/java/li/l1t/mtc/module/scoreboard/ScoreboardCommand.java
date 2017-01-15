/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.scoreboard;

import li.l1t.common.command.BukkitExecution;
import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.command.MTCExecutionExecutor;

/**
 * Handles the /scb command which allows players to manage their own scoreboard view.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-15
 */
public class ScoreboardCommand extends MTCExecutionExecutor {
    private final CommonScoreboardProvider scoreboard;

    @InjectMe
    public ScoreboardCommand(CommonScoreboardProvider scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public boolean execute(BukkitExecution exec) throws UserException, InternalException {
        if (exec.hasArg(0)) {
            switch (exec.arg(0).toLowerCase()) {
                case "toggle":
                    handleToggle(exec);
                    return true;
                case "refresh":
                    handleRefresh(exec);
                    return true;
            }
        }
        sendUsageTo(exec);
        return true;
    }

    private void handleToggle(BukkitExecution exec) {
        if (scoreboard.isBoardHiddenFor(exec.player())) {
            scoreboard.unhideBoardFor(exec.player());
            exec.respond(MessageType.RESULT_LINE_SUCCESS, "Das Scoreboard wird dir jetzt wieder angezeigt.");
        } else {
            scoreboard.hideBoardFor(exec.player());
            exec.respond(MessageType.RESULT_LINE_SUCCESS, "Das Scoreboard ist jetzt versteckt.");
        }
    }

    private void handleRefresh(BukkitExecution exec) {
        scoreboard.updateScoreboardFor(exec.player());
        exec.respond(MessageType.RESULT_LINE_SUCCESS, "Das Scoreboard wurde aktualisiert.");
        exec.respond(MessageType.WARNING, "Manche Daten sind womöglich nicht auf dem allerneuesten Stand. " +
                "Das Scoreboard aktualisiert sich alle paar Minuten selbst.");
    }

    public void sendUsageTo(BukkitExecution exec) {
        exec.respondUsage("toggle", "", "Versteckt/Zeigt Scoreboard");
        exec.respondUsage("refresh", "", "Aktualisiert das Scoreboard sofort");
    }
}
