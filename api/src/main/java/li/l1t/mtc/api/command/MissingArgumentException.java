/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.command;

import li.l1t.common.exception.UserException;

/**
 * Thrown if too few arguments were specified for a command.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-26
 */
public class MissingArgumentException extends UserException {
    public MissingArgumentException(String messagePattern, Object... params) {
        super(messagePattern, params);
    }

    public MissingArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public static MissingArgumentException forIndex(int argumentIndex) {
        return new MissingArgumentException(
                "Fehlendes Argument %d", argumentIndex + 1
        );
    }
}
