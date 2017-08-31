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
