/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.module.inject.exception;

import li.l1t.mtc.api.module.inject.InjectMe;

/**
 * Thrown if a required dependency that has the {@link InjectMe#failSilently()} attribute set to
 * true cannot be initialised. Declaring that attribute essentially states that the dependant cannot
 * operate without the failed dependency, but that is not a critical error.<p>An example where this
 * might be used is submodules. Imagine a chat module that has a coloured chat submodule. The
 * coloured chat module requires the chat module, so it declares that dependency as required.
 * However, a coloured chat does not make sense without a chat in the first place. Because of that,
 * the coloured chat module declares {@link InjectMe#failSilently()}. If the chat module is
 * disabled, the coloured chat module will fail to enable (and throw this exception), but no error
 * message should be printed.</p>
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-24
 */
public class SilentFailException extends InjectionException {
    public SilentFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
