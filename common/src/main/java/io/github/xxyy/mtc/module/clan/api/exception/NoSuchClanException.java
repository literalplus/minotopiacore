/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.api.exception;

/**
 * Thrown if a clan has been requested but doesn't exist. {@link #getMessage()} will provide details on what was requested.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 06/02/15
 */ //Thanks to @benaryorg for design considerations
public class NoSuchClanException extends Exception {
    public NoSuchClanException(String message) {
        super(message);
    }

    public NoSuchClanException(String message, Throwable cause) {
        super(message, cause);
    }
}
