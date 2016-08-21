/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.module.inject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Represents a target that my be injected into MTC modules.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-06-11
 */
public interface InjectionTarget<T> {
    /**
     * Registers with this injection target a new dependant that depends on this target. This is
     * used to notify/disable dependants when this target is no longer available.
     *
     * @param dependant the dependant to register
     * @param field     the field in dependant where this target is injected
     */
    <V> Injection<T> registerDependant(InjectionTarget<V> dependant, Field field);

    /**
     * Checks whether this target depends on another target.
     *
     * @param dependency the target to check for
     * @return whether this target depends on dependency
     */
    boolean hasDependencyOn(InjectionTarget<?> dependency);

    /**
     * Checks whether this target depends on another target and that target is a required
     * dependency.
     *
     * @param dependency the target to check for
     * @return whether this target depends on dependency and the dependency is required
     */
    boolean hasRequiredDependencyOn(InjectionTarget<?> dependency);

    /**
     * @return a stream of all dependencies of this target that are required
     */
    Stream<Injection<?>> getRequiredDependencies();

    /**
     * Creates a new instance of this target.
     *
     * @return a new instance of this target
     * @throws NoSuchMethodException     if the target does not have a default constructor
     * @throws IllegalAccessException    if the target's default constructor is inaccessible
     * @throws InvocationTargetException if an error occurs instantiating the target
     * @throws InstantiationException    if an error occurs instantiating the target
     */
    T createInstance() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException;

    boolean isModule();

    /**
     * @return the class of this target
     */
    Class<T> getClazz();

    /**
     * @return an instance of this target, if set
     */
    T getInstance();

    /**
     * Sets this target's (singleton) instance.
     *
     * @param instance an instance of this target, may not be null
     */
    void setInstance(T instance);

    /**
     * @return whether this target has an instance set
     */
    boolean hasInstance();

    /**
     * Gets a map from dependants on this target to whether they have declared this target as a
     * required dependency.
     *
     * @return a map of dependants on this target
     */
    Map<InjectionTarget<?>, Injection<T>> getDependants();

    /**
     * Gets a map from this target's dependencies to whether this target requires them.
     *
     * @return a map of this target's dependencies
     */
    Map<InjectionTarget<?>, Injection<?>> getDependencies();
}
