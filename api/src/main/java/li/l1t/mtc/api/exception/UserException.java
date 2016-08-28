/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.exception;

import li.l1t.mtc.api.chat.MessageType;

/**
 * Represents an exception that will be shown to command senders as error they caused by providing
 * invalid arguments.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-23
 */
public class UserException extends PlayerReadableException {
    public UserException(String message) {
        super(message);
    }

    /**
     * Creates a new UserException with a {@link String#format(String, Object...)}-like message
     * pattern and parameters.
     *
     * @param messagePattern the pattern for the message
     * @param params         the parameters
     */
    public UserException(String messagePattern, Object... params) {
        super(String.format(messagePattern, params));
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }

    public static UserException wrap(Exception cause) {
        return wrap(cause, cause.getMessage());
    }

    public static UserException wrap(IllegalArgumentException cause) {
        return wrap(cause, cause.getMessage());
    }

    public static UserException wrap(IllegalStateException cause) {
        return wrap(cause, cause.getMessage());
    }

    public static UserException wrap(Exception cause, String message) {
        return new UserException(message, cause);
    }

    public static UserException wrapNotANumber(NumberFormatException nfe) {
        String strippedMessage = nfe.getMessage().replace("For Input string: ", ""); //For input string: "notanint"
        return new UserException("Das ist keine Zahl: %s", strippedMessage);
    }

    @Override
    protected String formatMessage() {
        return MessageType.USER_ERROR.format(getMessage());
    }

    @Override
    public boolean needsLogging() {
        return false;
    }
}
