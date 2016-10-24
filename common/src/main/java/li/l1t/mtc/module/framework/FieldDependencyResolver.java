/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.framework;

import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.api.module.inject.InjectionTarget;
import li.l1t.mtc.api.module.inject.exception.CyclicDependencyException;
import li.l1t.mtc.api.module.inject.exception.InjectionException;
import li.l1t.mtc.api.module.inject.exception.SilentFailException;

import java.lang.reflect.Field;

/**
 * Resolves dependencies on fields.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-25
 */
class FieldDependencyResolver {
    private final SimpleDependencyResolver parent;

    FieldDependencyResolver(SimpleDependencyResolver parent) {
        this.parent = parent;
    }

    public void declareFieldDependencies(InjectionTarget<?> target) {
        for (Field field : target.getClazz().getDeclaredFields()) {
            examineField(target, field);
        }
    }

    private void examineField(InjectionTarget<?> target, Field field) {
        InjectMe annotation = field.getAnnotation(InjectMe.class);
        if (isInjectable(annotation)) {
            resolveAndInstantiateFieldDependency(target, field, annotation);
        }
    }

    private boolean isInjectable(InjectMe annotation) {
        return annotation != null;
    }

    private void resolveAndInstantiateFieldDependency(InjectionTarget<?> target, Field field, InjectMe annotation) {
        InjectionTarget<?> dependency = declareDependency(target, field);
        instantiateFieldIfPossible(field, annotation, dependency);
    }

    private InjectionTarget<?> declareDependency(InjectionTarget<?> dependant, Field field) {
        Class<?> dependencyType = field.getType();
        InjectionTarget<?> dependency = parent.getTarget(dependencyType);
        dependency.registerDependant(dependant, field);
        return dependency;
    }

    private void instantiateFieldIfPossible(Field field, InjectMe annotation, InjectionTarget<?> dependency) {
        if (parent.isCyclicDependency(field.getType())) {
            checkCyclicDependencyIsLegal(field, annotation.required());
            return;
            //since we depend upon them, it is their initialisation that calls us, meaning that if we
            // try to start another initialisation for them, we create an infinite loop
        }
        instantiateDependencyIfNecessary(dependency, annotation);
    }

    private void checkCyclicDependencyIsLegal(Field field, boolean required) {
        if (required) {
            throw new CyclicDependencyException(
                    field.getDeclaringClass(), field.getType(), parent.describeCurrentDependencyStack()
            );
        }
    }

    private void instantiateDependencyIfNecessary(InjectionTarget<?> dependency, InjectMe annotation) {
        try {
            parent.initialiseIfNecessary(dependency);
        } catch (InjectionException e) {
            handleInitialisationFailure(dependency, annotation, e);
        }
    }

    private void handleInitialisationFailure(InjectionTarget<?> dependency, InjectMe annotation, InjectionException e) {
        if (annotation.required()) {
            if (annotation.failSilently()) {
                throw new SilentFailException(dependency + ", required by: " + parent.describeCurrentDependencyStack(), e);
            } else {
                throw e;
            }
        }
    }
}
