/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.module.inject;


import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Performs injections.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 11.6.16
 */
public interface Injector {
    /**
     * Perform all injections dependant on a target.
     *
     * @param injectee the target to inject
     * @param value    the value to inject into dependants
     * @throws IllegalStateException if the injection fails
     */
    void injectAll(InjectionTarget<?> injectee, Object value);

    /**
     * Performs an injection.
     *
     * @param receiver the injection to be performed
     * @param value    the object to inject
     * @throws IllegalStateException if the injection fails
     */
    void injectInto(Injection<?> receiver, Object value);

    /**
     * Registers an instance with the injector. If there is already a target for given instance's
     * class, the instance is set on that, overriding any existing instance. If there is not yet a
     * target, a new one is created to hold the instance.
     *
     * @param instance the instance to register
     * @param <T>      the type of the object to register
     */
    <T> void registerInstance(T instance);

    /**
     * Gets or creates a creation target for given class.
     *
     * @param clazz the class to create a target for
     * @param <T>   the type of the class
     * @return the created or cached target
     */
    @Nonnull
    <T> InjectionTarget<T> getTarget(@Nonnull Class<T> clazz);

    /**
     * @return all injection targets managed by this injector
     */
    Set<InjectionTarget<?>> getTargets();
}
