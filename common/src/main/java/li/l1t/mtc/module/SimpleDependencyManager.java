/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module;

import com.google.common.base.Verify;
import li.l1t.mtc.api.module.DependencyManager;
import li.l1t.mtc.api.module.ModuleManager;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.api.module.inject.InjectionTarget;
import li.l1t.mtc.api.module.inject.exception.CyclicDependencyException;
import li.l1t.mtc.api.module.inject.exception.InjectionException;
import li.l1t.mtc.api.module.inject.exception.SilentFailException;

import java.lang.reflect.Field;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Takes care of initialising dependencies needed for dependency injection.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 11.6.16
 */
class SimpleDependencyManager implements DependencyManager {
    private final ModuleManager moduleManager;
    private final Deque<Class<?>> dependencyStack = new LinkedList<>();

    SimpleDependencyManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Override
    public void initialise(InjectionTarget<?> target) throws InjectionException {
        this.dependencyStack.clear();
        initialiseIfNecessary(target);
    }

    private void initialiseIfNecessary(InjectionTarget<?> target) {
        if (!alreadyInstantiated(target)) {
            tryInitialise(target);
        }
    }

    private boolean alreadyInstantiated(InjectionTarget<?> target) {
        return target.hasInstance();
    }

    private void tryInitialise(InjectionTarget<?> meta) {
        try {
            attemptDiscoverAndInitialiseDependencies(meta);
            meta.createInstance();
        } catch (SilentFailException e) {
            throw e;
        } catch (Exception e) {
            throw new InjectionException(String.format(
                    "Unable to instantiate %s (required by %s)",
                    meta.getClazz().getSimpleName(), dependencyStack.toString()
            ), e);
        }
    }

    private void attemptDiscoverAndInitialiseDependencies(InjectionTarget<?> target) throws InjectionException {
        try {
            dependencyStack.push(target.getClazz());
            discoverAndInitialiseDependencies(target);
        } finally {
            Class<?> removed = dependencyStack.pop();
            Verify.verify(removed == target.getClazz(),
                    "dependency stack handling flawed - expected top to be %s, but found %s in %s",
                    target, removed, dependencyStack
            );
        }
    }

    private void discoverAndInitialiseDependencies(InjectionTarget<?> target) {
        for (Field field : target.getClazz().getDeclaredFields()) {
            examineField(target, field);
        }
    }

    private void examineField(InjectionTarget<?> target, Field field) {
        InjectMe annotation = field.getAnnotation(InjectMe.class);
        if (isInjectable(annotation)) {
            resolveAndInstantiateDependency(target, field, annotation);
        }
    }

    private boolean isInjectable(InjectMe annotation) {
        return annotation != null;
    }

    private void resolveAndInstantiateDependency(InjectionTarget<?> target, Field field, InjectMe annotation) {
        InjectionTarget<?> dependency = declareDependency(target, field);
        instantiateFieldIfPossible(field, annotation, dependency);
    }

    private InjectionTarget<?> declareDependency(InjectionTarget<?> dependant, Field field) {
        Class<?> dependencyType = field.getType();
        InjectionTarget<?> dependency = moduleManager.getInjector().getTarget(dependencyType);
        dependency.registerDependant(dependant, field);
        return dependency;

        //TODO: Why is/was this necessary?
        // This checks whether the dependency has actually been instantiated
        // However, that shouldn't happen since that should throw an exception
//        if (annotation.required() && !alreadyInstantiated(dependency) && !annotation.failSilently()) {
//            throw new IllegalArgumentException(String.format(
//                    "Unable to load required module dependency: %s (from %s)",
//                    dependencyType.getSimpleName(), dependencyStack.toString()
//            ));
//        }
    }

    private void instantiateFieldIfPossible(Field field, InjectMe annotation, InjectionTarget<?> dependency) {
        if (isCyclicDependency(field.getType())) {
            checkCyclicDependencyIsLegal(field.getType(), annotation.required());
            return;
            //since we depend upon them, it is their initialisation that calls us, meaning that if we
            // try to start another initialisation for them, we create an infinite loop
        }
        instantiateDependencyIfNecessary(dependency, annotation);
    }

    private boolean isCyclicDependency(Class<?> dependencyType) {
        return dependencyStack.contains(dependencyType);
    }

    private void checkCyclicDependencyIsLegal(Class<?> dependencyType, boolean required) {
        if (required) {
            throw new CyclicDependencyException(
                    dependencyStack.peek(), dependencyType, dependencyStack.toString()
            );
        }
    }

    private void instantiateDependencyIfNecessary(InjectionTarget<?> dependency, InjectMe annotation) {
        try {
            initialiseIfNecessary(dependency);
        } catch (InjectionException e) {
            handleInitialisationFailure(dependency, annotation, e);
        }
    }

    private void handleInitialisationFailure(InjectionTarget<?> dependency, InjectMe annotation, InjectionException e) {
        if (annotation.required()) {
            if (annotation.failSilently()) {
                throw new SilentFailException(dependency + ", required by: " + dependencyStack.toString(), e);
            } else {
                throw e;
            }
        }
    }
}
