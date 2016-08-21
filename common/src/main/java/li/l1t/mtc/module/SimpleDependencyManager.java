/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module;

import li.l1t.mtc.api.module.DependencyManager;
import li.l1t.mtc.api.module.ModuleManager;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.api.module.inject.InjectionTarget;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Stack;

/**
 * Takes care of initialising dependencies needed for dependency injection.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 11.6.16
 */
class SimpleDependencyManager implements DependencyManager {
    private final ModuleManager moduleManager;

    SimpleDependencyManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Override
    public boolean discoverInjections(InjectionTarget<?> target, Stack<Class<?>> dependencyStack)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {

        dependencyStack.push(target.getClazz());

        for (Field field : target.getClazz().getDeclaredFields()) {
            InjectMe annotation = field.getAnnotation(InjectMe.class);
            if (annotation == null) {
                continue;
            }

            Class<?> dependencyType = field.getType();
            InjectionTarget<?> dependency = moduleManager.getInjector().getTarget(dependencyType);
            dependency.registerDependant(target, field);

            if (!dependency.hasInstance()) {
                if (dependencyStack.contains(dependencyType)) { //Can't check directly on object because infinite recursion, also easier ok
                    if (annotation.required()) {
                        throw new IllegalArgumentException(String.format("Cyclic dependencies can't be required: %s (from %s)",
                                dependencyType.getSimpleName(), dependencyStack.toString()));
                    } else {
                        continue; //dependencies and injections have our record, we can't load now though
                    }
                }

                initialise(dependency, dependencyStack);
            }

            if (annotation.required() && !dependency.hasInstance()) {
                throw new IllegalArgumentException(String.format("Unable to load required module dependency: %s (from %s)",
                        dependencyType.getSimpleName(), dependencyStack.toString()));
            }
        }

        dependencyStack.pop();
        return true;
    }

    @Override
    public void initialise(InjectionTarget<?> meta, Stack<Class<?>> dependencyStack)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {

        if (meta.hasInstance()) {
            return;
        }

        try {
            discoverInjections(meta, dependencyStack); //throws IllegalArgumentException
            meta.createInstance();
        } catch (NoSuchMethodException e) {
            //Wrap this specific problem with a more helpful error message to simplify usage for developers
            throw new IllegalArgumentException("Modules must specify a default constructor!", e);
        }
    }
}
