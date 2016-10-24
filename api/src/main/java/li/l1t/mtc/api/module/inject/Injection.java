/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.module.inject;

/**
 * Represents something that may be injected.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-24
 */
public interface Injection<T> {
    /**
     * @return the dependency of the injection, e.g. what we inject
     */
    InjectionTarget<T> getDependency();

    /**
     * @return the dependant of this injection, e.g. what we inject into
     */
    InjectionTarget<?> getDependant();

    /**
     * @return whether this injection represents a required dependency
     */
    boolean isRequired();
}
