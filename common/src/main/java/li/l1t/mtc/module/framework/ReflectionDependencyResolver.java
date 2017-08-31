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

import com.google.common.base.Verify;
import li.l1t.mtc.api.module.DependencyManager;
import li.l1t.mtc.api.module.ModuleManager;
import li.l1t.mtc.api.module.inject.InjectionTarget;
import li.l1t.mtc.api.module.inject.exception.InjectionException;
import li.l1t.mtc.api.module.inject.exception.SilentFailException;
import li.l1t.mtc.logging.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Takes care of initialising dependencies needed for dependency injection.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-06-11
 */
class ReflectionDependencyResolver implements DependencyManager {
    private static final Logger LOGGER = LogManager.getLogger(ReflectionDependencyResolver.class);
    private final ModuleManager moduleManager;
    private final Deque<Class<?>> dependencyStack = new LinkedList<>();
    private final FieldDependencyResolver fieldResolver = new FieldDependencyResolver(this);
    private final ConstructorDependencyResolver constructorResolver = new ConstructorDependencyResolver(this);

    ReflectionDependencyResolver(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    protected <T> InjectionTarget<T> getTarget(Class<T> dependencyType) {
        return moduleManager.getInjector().getTarget(dependencyType);
    }

    @Override
    public void initialise(InjectionTarget<?> target) throws InjectionException {
        this.dependencyStack.clear();
        initialiseIfNecessary(target);
    }

    protected void initialiseIfNecessary(InjectionTarget<?> target) {
        if (!alreadyInstantiated(target)) {
            tryInitialise(target);
        }
    }

    private boolean alreadyInstantiated(InjectionTarget<?> target) {
        return target.hasInstance();
    }

    private void tryInitialise(InjectionTarget<?> target) {
        try {
            attemptDiscoverAndInitialiseDependencies(target);
            target.createInstance();
        } catch (SilentFailException e) {
            throw e;
        } catch (NoClassDefFoundError ncdfe) {
            LOGGER.info("Missing class for {}: {}", target.getClazz().getSimpleName(), ncdfe.getMessage());
            target.handleMissingClass(ncdfe);
        } catch (Exception e) {
            throw new InjectionException(String.format(
                    "Unable to instantiate %s (required by %s)",
                    target.getClazz().getSimpleName(), dependencyStack.toString()
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

    private <D> void discoverAndInitialiseDependencies(InjectionTarget<D> target) {
        constructorResolver.declareConstructorDependencies(target);
        fieldResolver.declareFieldDependencies(target);
    }

    protected boolean isCyclicDependency(Class<?> dependencyType) {
        return dependencyStack.contains(dependencyType);
    }

    protected String describeCurrentDependencyStack() {
        return dependencyStack.toString();
    }
}
