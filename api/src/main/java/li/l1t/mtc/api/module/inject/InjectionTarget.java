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

import li.l1t.mtc.api.module.inject.exception.InjectionException;
import li.l1t.mtc.api.module.inject.exception.SilentFailException;

import java.lang.reflect.Constructor;
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
     * Registers with this injection target a new dependant that depends on this target. This is
     * used to notify/disable dependants when this target is no longer available.
     *
     * @param dependant   the dependant to register
     * @param constructor the constructor in dependant where this target is injected
     */
    <V> Injection<T> registerDependant(InjectionTarget<V> dependant, Constructor<V> constructor);

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
     * @throws IllegalAccessException    if the {@link #getInjectableConstructor() injectable
     *                                   constructor} is inaccessible
     * @throws InvocationTargetException if an error occurs instantiating the target
     * @throws InstantiationException    if an error occurs instantiating the target
     * @throws InjectionException        if the parameters of the {@link #getInjectableConstructor()
     *                                   injectable constructor} are not declared as dependencies of
     *                                   this target
     */
    T createInstance() throws IllegalAccessException, InvocationTargetException, InstantiationException;

    /**
     * Finds the constructor that this target uses for injection, if any. The parameters of the
     * constructor, if any, are dependencies that need to be resolved before construction.
     *
     * @return the injectable constructor, or null if this target does not use create new instances
     * @throws InjectionException if there are multiple {@link InjectMe} constructors
     * @throws InjectionException if there are no {@link InjectMe} constructors and there is no
     *                            default constructor
     */
    Constructor<T> getInjectableConstructor() throws InjectionException;

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

    /**
     * Examines a {@link NoClassDefFoundError} that occurred during class loading. The purpose of
     * this method is to allow targets to check missing dependencies to determine whether given
     * error is actually serious or just the cause of a missing external dependency. If there is
     * just a dependency missing, it is not necessary to propagate an error up and possibly cause
     * bigger breakage. If it is detected that the cause of the error is a missing dependency, the
     * target may throw a {@link SilentFailException} to indicate that and prevent the error from
     * propagating directly to the caller.
     *
     * @param error the encountered error
     * @throws SilentFailException if given error is the cause of a known missing dependency and
     *                             need not be propagated as an error
     */
    void handleMissingClass(NoClassDefFoundError error) throws SilentFailException;
}
