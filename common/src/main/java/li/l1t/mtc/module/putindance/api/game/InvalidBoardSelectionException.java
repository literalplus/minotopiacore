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

package li.l1t.mtc.module.putindance.api.game;

import li.l1t.common.exception.UserException;
import li.l1t.mtc.api.chat.MessageType;

/**
 * Thrown if the board boundaries selected by a player cannot be handled by the current tick
 * strategy.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-13
 */
public class InvalidBoardSelectionException extends UserException {
    private final TickStrategy strategy;

    public InvalidBoardSelectionException(TickStrategy strategy, String message) {
        super(message);
        this.strategy = strategy;
    }

    public InvalidBoardSelectionException(TickStrategy strategy, String message, Throwable cause) {
        super(message, cause);
        this.strategy = strategy;
    }

    public InvalidBoardSelectionException(TickStrategy strategy, String messagePattern, Object... params) {
        super(messagePattern, params);
        this.strategy = strategy;
    }

    public TickStrategy getStrategy() {
        return strategy;
    }

    @Override
    public String getMessage() {
        return MessageType.USER_ERROR.format(
                "Invalide Spielfeldränder: %s: %s",
                strategy.getClass().getSimpleName(), super.getMessage()
        );
    }
}
