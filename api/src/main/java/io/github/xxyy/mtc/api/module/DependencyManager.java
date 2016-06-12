/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.api.module;

import io.github.xxyy.mtc.api.module.inject.InjectionTarget;

import java.lang.reflect.InvocationTargetException;
import java.util.Stack;

/**
 * Manages dependencies for dependency injection.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 12.6.16
 */
public interface DependencyManager {
    boolean discoverInjections(InjectionTarget<?> meta, Stack<Class<?>> dependencyStack)
            throws InstantiationException, IllegalAccessException, InvocationTargetException;

    void initialise(InjectionTarget<?> meta, Stack<Class<?>> dependencyStack)
            throws InvocationTargetException, InstantiationException, IllegalAccessException;
}
