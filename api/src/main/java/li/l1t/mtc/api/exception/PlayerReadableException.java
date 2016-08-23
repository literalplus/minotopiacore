/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.exception;

import org.bukkit.command.CommandSender;

/**
 * Abstract base class for exceptions that are explicitly thrown from commands to be shown to the
 * command sender as formatted message.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-23
 */
public abstract class PlayerReadableException extends RuntimeException {
    PlayerReadableException(String message, Throwable cause) {
        super(message, cause);
    }

    PlayerReadableException(String message) {
        super(message);
    }

    /**
     * @return whether this exception is supposed to be logged to console for further inspection
     */
    public abstract boolean needsLogging();

    /**
     * @return the human-readable legacy chat representation of this exception
     */
    protected abstract String formatMessage();

    public void sendMessageTo(CommandSender sender) {
        sender.sendMessage(formatMessage());
    }
}
