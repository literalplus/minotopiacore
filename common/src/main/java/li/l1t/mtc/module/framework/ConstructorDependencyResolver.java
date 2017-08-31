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

import java.lang.reflect.Constructor;
import java.util.Arrays;

/**
 * Resolved dependencies declared in constructors.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-25
 */
class ConstructorDependencyResolver {
    private final ReflectionDependencyResolver parent;

    ConstructorDependencyResolver(ReflectionDependencyResolver parent) {
        this.parent = parent;
    }

    public <D> void declareConstructorDependencies(InjectionTarget<D> target) {
        Constructor<D> constructor = target.getInjectableConstructor();
        if (constructor != null && constructor.getParameterCount() > 0) {
            declareAllDependenciesOf(target, constructor);
        }
    }

    private <D> void declareAllDependenciesOf(InjectionTarget<D> target, Constructor<D> constructor) {
        Arrays.stream(constructor.getParameterTypes())
                .forEach(parameterType -> declareTypeDependency(target, constructor, parameterType));
    }

    private <D> void declareTypeDependency(InjectionTarget<D> target, Constructor<D> constructor, Class<?> parameterType) {
        InjectionTarget<?> dependency = declareDependency(target, constructor, parameterType);
        instantiateDependency(dependency, constructor, parameterType);
    }

    private <D> InjectionTarget<?> declareDependency(InjectionTarget<D> dependant,
                                                     Constructor<D> constructor, Class<?> dependencyType) {
        InjectionTarget<?> dependency = parent.getTarget(dependencyType);
        dependency.registerDependant(dependant, constructor);
        return dependency;
    }

    private void instantiateDependency(InjectionTarget<?> dependency, Constructor<?> constructor, Class<?> parameterType) {
        if (parent.isCyclicDependency(parameterType)) {
            throw new CyclicDependencyException(
                    constructor.getDeclaringClass(), parameterType,
                    "constructor dependency in " + constructor
            );
        }
        instantiateDependencyIfNecessary(dependency, constructor);
    }

    private void instantiateDependencyIfNecessary(InjectionTarget<?> dependency, Constructor<?> constructor) {
        try {
            parent.initialiseIfNecessary(dependency);
        } catch (InjectionException e) {
            handleInitialisationFailure(dependency, constructor, e);
        }
    }

    private void handleInitialisationFailure(InjectionTarget<?> dependency, Constructor<?> constructor, InjectionException e) {
        InjectMe annotation = constructor.getAnnotation(InjectMe.class);
        if (annotation != null && annotation.failSilently()) {
            throw new SilentFailException(dependency + ", required by: " + parent.describeCurrentDependencyStack(), e);
        } else {
            throw e;
        }
    }
}
