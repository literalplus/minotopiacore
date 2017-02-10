/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.blocklock.removal;

import org.jetbrains.annotations.NonNls;

/**
 * Thrown if an invalid handler configuration is encountered.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-02-10
 */
public class HandlerConfigException extends RuntimeException {
    public HandlerConfigException(@NonNls String message) {
        super(message);
    }

    public HandlerConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
