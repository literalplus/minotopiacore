/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.exception;

import li.l1t.mtc.api.chat.MessageType;

import java.sql.SQLException;

/**
 * Represents an exception that will be shown to command senders as 'internal exception' with the
 * provided reason string.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-23
 */
public class InternalException extends PlayerReadableException {
    public InternalException(String message) {
        super(message);
    }

    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InternalException wrap(Exception cause) {
        return wrap(cause, cause.getMessage());
    }

    public static InternalException wrap(SQLException cause) {
        return new InternalException("Datenbankfehler.", cause);
    }

    public static InternalException wrap(Exception cause, String message) {
        return new InternalException(message, cause);
    }

    @Override
    protected String formatMessage() {
        return MessageType.INTERNAL_ERROR.format(getMessage());
    }

    @Override
    public boolean needsLogging() {
        return getCause() != null;
    }
}
