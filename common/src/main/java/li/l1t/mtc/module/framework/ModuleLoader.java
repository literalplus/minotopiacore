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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.MTCModule;
import li.l1t.mtc.api.module.inject.Injection;
import li.l1t.mtc.api.module.inject.InjectionTarget;
import li.l1t.mtc.api.module.inject.exception.SilentFailException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Handles loading, enabling, injecting and disabling of MTC nodules.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 18/06/15
 */
class ModuleLoader {
    private final SimpleModuleManager manager;

    ModuleLoader(SimpleModuleManager manager) {
        this.manager = manager;
    }

    /**
     * Gets a loaded module by its class.
     *
     * @param moduleClass the class to match
     * @return the loaded module, or null if there is no such module
     */
    @SuppressWarnings("unchecked")
    @Nullable
    //amateur benchmark: traditional for is faster than streams, but not too much to worry about it
    <T extends MTCModule> InjectionTarget<T> getLoadedModule(@Nonnull Class<T> moduleClass) {
        return (InjectionTarget<T>) manager.getInjector().getTargets().stream()
                .filter(m -> m.getClazz().equals(moduleClass) && m.hasInstance())
                .findFirst().orElse(null);
    }

    /**
     * @return the set of loaded modules
     */
    @SuppressWarnings("unchecked")
    Collection<InjectionTarget<? extends MTCModule>> getLoadedModules() {
        ImmutableSet.Builder<InjectionTarget<? extends MTCModule>> targets = ImmutableSet.builder();

        for (InjectionTarget<?> target : manager.getInjector().getTargets()) {
            if (target.hasInstance() && MTCModule.class.isAssignableFrom(target.getClazz())) {
                targets.add((InjectionTarget<? extends MTCModule>) target);
            }
        }

        return targets.build();
    }

    void enableAll(Collection<InjectionTarget<? extends MTCModule>> targets) {
        targets.forEach(target -> setEnabled(target, true));
    }

    /**
     * Changes a module's enable state. If the module is being enabled, it will be injected into
     * other modules that depend on it and also its dependencies will be instantiated, if possible.
     * If it is being disabled, all injections are undone. {@link MTCModule#canBeEnabled(MTCPlugin)}
     * is respected.
     *
     * @param module  the module to enable
     * @param enabled the new enable state
     * @return a list of MTC modules whose states have changed as result of this method call
     * @throws IllegalArgumentException if a module not managed by this loader is passed
     * @throws IllegalArgumentException if the module is not ready to be enabled according to {@link
     *                                  MTCModule#canBeEnabled(MTCPlugin)}
     * @throws IllegalStateException    if dependency injection fails
     */
    List<MTCModule> setEnabled(@Nonnull MTCModule module, boolean enabled) {
        Preconditions.checkNotNull(module, "module");
        InjectionTarget<? extends MTCModule> meta = getLoadedModule(module.getClass());

        Preconditions.checkNotNull(meta, "Module not known to this loader: %s", meta);
        Preconditions.checkState(meta.hasInstance(), "Cannot enable non-initialised module: %s", meta);
        //noinspection ConstantConditions
        Preconditions.checkArgument(module.equals(meta.getInstance()),
                "Cannot enable/disable module not managed by this loader: %s (mine: %s)", meta, meta);

        return setEnabled(meta, enabled);
    }

    List<MTCModule> setEnabled(@Nonnull InjectionTarget<? extends MTCModule> meta,
                               boolean enabled) {
        return setEnabled(meta, enabled, new Stack<>());
    }

    private List<MTCModule> setEnabled(@Nonnull InjectionTarget<?> target,
                                       boolean enabled,
                                       @Nonnull Stack<InjectionTarget<?>> dependencyStack) {
        Preconditions.checkArgument(target.hasInstance(),
                "injectable must have instance: %s", target.getClazz());

        MTCModule module;
        if (target.getInstance() instanceof MTCModule) {
            module = (MTCModule) target.getInstance();

            if (manager.isEnabled(module) == enabled) {
                return ImmutableList.of();
            }
            if (enabled) {
                Preconditions.checkArgument(module.canBeEnabled(manager.getPlugin()),
                        "Module not ready to be enabled: %s", target);
            }
        } else {
            module = null;
        }

        List<MTCModule> changedModules;
        dependencyStack.push(target);

        if (enabled) {

            changedModules = target.getDependencies().values().stream()
                    .filter(inj -> !dependencyStack.contains(inj.getDependency())) //prevents infinite recursion
                    .filter(this::needsEnabling)
                    .flatMap(inj -> setEnabled(inj.getDependency(), true, dependencyStack).stream())
                    .collect(Collectors.toList());

            manager.getInjector().injectAll(target, target.getInstance());
        } else {
            //Disable everything that requires this target
            changedModules = target.getDependants().values().stream()
                    .filter(Injection::isRequired)
                    .filter(inj -> !dependencyStack.contains(inj.getDependant())) //prevents infinite recursion
                    .flatMap(inj -> setEnabled(inj.getDependant(), false, dependencyStack).stream())
                    .collect(Collectors.toList());

            manager.getInjector().injectAll(target, null);
        }

        if (module != null) {
            manager.registerEnabled(module, enabled);
            changedModules.add(module);
        }
        dependencyStack.pop();
        return changedModules;
    }

    @SuppressWarnings("unchecked")
    private boolean needsEnabling(Injection<?> inj) {
        return isNotAModule(inj) || isNotEnabledAndCanBeEnabled((Injection<? extends MTCModule>) inj);
    }

    private boolean isNotAModule(Injection<?> inj) {
        return !MTCModule.class.isAssignableFrom(inj.getDependency().getClazz());
    }

    private boolean isNotEnabledAndCanBeEnabled(Injection<? extends MTCModule> inj) {
        MTCModule instance = inj.getDependency().getInstance();
        return !manager.isEnabled(instance) && instance.canBeEnabled(manager.getPlugin());
    }

    /**
     * Attempts to load MTC modules from a list of classes. If an error occurs while loading any
     * plugin, the error consumer will be called with the caught {@link Throwable} and loading will
     * be continued. This allows to deal with errors, (e.g. logging them) while not losing the
     * "sandbox" preventing faulty modules from crashing the whole plugin with an unexpected
     * exception or error.
     *
     * @param moduleClasses the classes to be loaded
     * @param errorConsumer a consumer which is called when an error is encountered
     */
    void loadAll(List<Class<? extends MTCModule>> moduleClasses,
                 BiConsumer<InjectionTarget<?>, Throwable> errorConsumer) {
        List<InjectionTarget> initializedTargets = moduleClasses.stream()
                .map(manager.getInjector()::getTarget)
                .collect(Collectors.toList()); //pre-register all modules for optional dependencies
        initializedTargets.forEach(meta -> {
            try {
                manager.getDependencyManager().initialise(meta);
            } catch (SilentFailException ignore) {
                //silent failure: This module doesn't make sense without the dependency, so
                // we don't need to send an error message
            } catch (Throwable t) {
                        /* this is kind of a sandbox, so that a single broken module can't break the
                           whole plugin. Not using Exception because of more nasty stuff like NoClassDefFoundError,
                           which might be raised when a module is missing external dependencies. */
                errorConsumer.accept(meta, t); //The main reason for using a consumer is proper unit testing
            }
        });
    }
}
