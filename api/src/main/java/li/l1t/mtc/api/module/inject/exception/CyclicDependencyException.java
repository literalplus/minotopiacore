/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
