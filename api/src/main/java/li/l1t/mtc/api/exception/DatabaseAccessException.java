/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.exception;

/**
 * Thrown if an error occurs talking to an underlying database.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public class DatabaseAccessException extends RuntimeException {
    public DatabaseAccessException(Throwable cause) {
        super(cause);
    }
}
