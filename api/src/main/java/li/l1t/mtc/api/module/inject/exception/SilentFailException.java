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
