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

package li.l1t.mtc.api.module.inject;

import li.l1t.mtc.api.module.inject.exception.SilentFailException;

import java.lang.annotation.*;

/**
 * This annotation is used to declare the need for dependency injection on a field.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-06-11
 */
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InjectMe {
    /**
     * Gets whether this injection is a required injection. If a required injection cannot be
     * performed, some kind of error will be raised during the injection process. Note that
     * constructor injections are always required and therefore this parameter is ignored.
     *
     * @return whether this injection is required
     */
    boolean required() default true;

    /**
     * Gets whether the module enabling should fail silently if this dependency could not be
     * injected and it is required. See {@link SilentFailException} for details.
     *
     * @return whether missing required dependencies should abort loading but not log messages
     */
    boolean failSilently() default false;

    /**
     * @return a hint to the injector
     */
    String hint() default "";
}
