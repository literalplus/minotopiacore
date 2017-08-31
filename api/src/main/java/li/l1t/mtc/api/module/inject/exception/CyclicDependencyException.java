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

/**
 * Thrown if a dependency cannot be injected because it depends on a dependant, and is marked as
 * required by the dependant.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-24
 */
public class CyclicDependencyException extends InjectionException {
    public CyclicDependencyException(Class<?> dependant, Class<?> dependency, String details) {
        super(formatMessageWithDetails(dependant, dependency, details));
    }

    private static String formatMessageWithDetails(Class<?> dependant,
                                                   Class<?> dependency, String details) {
        return formatMessage(dependant, dependency) + "(further information: " + details + ")";
    }

    private static String formatMessage(Class<?> dependant, Class<?> dependency) {
        return formatMessage(dependant.getSimpleName(), dependency.getSimpleName());
    }

    private static String formatMessage(String dependant, String dependency) {
        return String.format("Cannot inject %s into %s: Required cyclic dependency detected. " +
                        "[%s depends on %s, which requires %s]",
                dependency, dependant, dependency, dependant, dependency);
    }
}
