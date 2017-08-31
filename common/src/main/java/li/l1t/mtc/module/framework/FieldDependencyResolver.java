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
    private final ReflectionDependencyResolver parent;

    FieldDependencyResolver(ReflectionDependencyResolver parent) {
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
