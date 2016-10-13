/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
