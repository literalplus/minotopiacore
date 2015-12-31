/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module;

import com.google.common.collect.ImmutableList;
import io.github.xxyy.common.util.PredicateHelper;
import io.github.xxyy.lib.guava17.base.Preconditions;
import io.github.xxyy.lib.intellij_annotations.NotNull;
import io.github.xxyy.lib.intellij_annotations.Nullable;
import io.github.xxyy.mtc.api.MTCPlugin;
import io.github.xxyy.mtc.api.module.MTCModule;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Handles loading, enabling, injecting and disabling of MTC nodules.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 18/06/15
 */
class ModuleLoader {
    private final MTCModuleManager manager;

    private final Set<MTCModuleRuntimeMeta<?>> moduleMetas = new HashSet<>(); //not necessarily initialised

    ModuleLoader(MTCModuleManager manager) {
        this.manager = manager;
    }


    /**
     * Gets a loaded module by its class.
     *
     * @param moduleClass the class to match
     * @return the loaded module, or null if there is no such module
     */
    @Nullable //amateur benchmark: traditional for is faster than streams, but not too much to worry about it
    public <T extends MTCModule> MTCModuleRuntimeMeta<T> getLoadedModule(@NotNull Class<T> moduleClass) {
        //noinspection unchecked
        return (MTCModuleRuntimeMeta<T>) moduleMetas.stream()
                .filter(m -> m.getClazz().equals(moduleClass) && m.isInitialised())
                .findFirst().orElse(null);
    }

    @NotNull
    private <T extends MTCModule> MTCModuleRuntimeMeta<T> getModuleMeta(@NotNull Class<T> moduleClass) {
        @SuppressWarnings("unchecked")
        MTCModuleRuntimeMeta<T> meta = (MTCModuleRuntimeMeta<T>) moduleMetas.stream()
                .filter(m -> m.getClazz().equals(moduleClass))
                .findFirst().orElse(null);

        if (meta == null) {
            meta = new MTCModuleRuntimeMeta<>(moduleClass);
            moduleMetas.add(meta);
        }

        return meta;
    }

    /**
     * @return the set of loaded modules
     */
    public Collection<MTCModuleRuntimeMeta<?>> getLoadedModules() {
        return moduleMetas.stream()
                .filter(MTCModuleRuntimeMeta::isInitialised)
                .collect(Collectors.toSet());
    }

    /**
     * Changes a module's enable state. If the module is being enabled, it will be injected into other modules that
     * depend on it and also its dependencies will be instantiated, if possible. If it is being disabled, all
     * injections are undone. {@link MTCModule#canBeEnabled(MTCPlugin)} is respected.
     *
     * @param module  the module to enable
     * @param enabled the new enable state
     * @return a list of MTC modules whose states have changed as result of this method call
     * @throws IllegalArgumentException if a module not managed by this loader is passed
     * @throws IllegalArgumentException if the module is not ready to be enabled according to
     *                                  {@link MTCModule#canBeEnabled(MTCPlugin)}
     * @throws IllegalStateException    if dependency injection fails
     */
    public List<MTCModule> setEnabled(@NotNull MTCModule module, boolean enabled) {
        Preconditions.checkNotNull(module, "module");
        MTCModuleRuntimeMeta<?> meta = getLoadedModule(module.getClass());

        Preconditions.checkNotNull(meta, "Module not known to this loader: %s", meta);
        Preconditions.checkState(meta.isInitialised(), "Cannot enable non-initialised module: %s", meta);
        //noinspection ConstantConditions
        Preconditions.checkArgument(module.equals(meta.getModule()),
                "Cannot enable/disable module not managed by this loader: %s (mine: %s)", meta, meta);

        return setEnabled(meta, enabled);
    }

    public List<MTCModule> setEnabled(@NotNull MTCModuleRuntimeMeta<?> meta, boolean enabled) {
        return setEnabled(meta, enabled, new Stack<>());
    }

    private List<MTCModule> setEnabled(@NotNull MTCModuleRuntimeMeta<?> meta, boolean enabled,
                                       @NotNull Stack<MTCModuleRuntimeMeta<?>> dependencyStack) {

        if (manager.isEnabled(meta.getModule()) == enabled) {
            return ImmutableList.of();
        }

        List<MTCModule> changedModules;
        dependencyStack.push(meta);

        if (enabled) {
            Preconditions.checkArgument(meta.getModule().canBeEnabled(manager.getPlugin()),
                    "Module not ready to be enabled: %s", meta);

            changedModules = meta.getRequiredDependencyStream()
                    .filter(PredicateHelper.not(dependencyStack::contains)) //prevents infinite recursion
                    .flatMap(d -> setEnabled(d, true, dependencyStack).stream())
                    .collect(Collectors.toList());

            inject(meta, meta.getModule());
        } else {
            //Disable everything that requires this module
            changedModules = meta.getInjectionTargets().stream()
                    .filter(t -> t.getAnnotation().required())
                    .filter(t -> !dependencyStack.contains(t.getMeta())) //prevents infinite recursion
                    .flatMap(target -> setEnabled(target.getMeta(), false, dependencyStack).stream())
                    .collect(Collectors.toList());

            inject(meta, null);
        }

        manager.registerEnabled(meta.getModule(), enabled);
        dependencyStack.pop();
        changedModules.add(meta.getModule());
        return changedModules;
    }

    protected void inject(MTCModuleRuntimeMeta<?> meta, Object value) {
        for (MTCModuleRuntimeMeta.InjectionTarget target : meta.getInjectionTargets()) {
            if (!target.getField().isAccessible()) {
                target.getField().setAccessible(true);
            }

            MTCModule targetModule = target.getMeta().getModule();
            if (targetModule == null) {
                manager.getPlugin().getLogger().warning(String.format("Uninitialised injection target %s: %s",
                        target.getMeta().getClazz().getName(), target.getField().toString()));
                continue;
            }

            try {
                target.getField().set(targetModule, value);
            } catch (IllegalAccessException e) {
                throw new AssertionError(e); //Actually, this should never happen, but.
            } catch (ExceptionInInitializerError e) {
                throw new IllegalStateException(String.format("Failed to inject %s into %s:",
                        meta.getClazz().getSimpleName(), targetModule.getName()), e);
            }
        }
    }

    /**
     * Attempts to load MTC modules from a list of classes. If an error occurrs while loading any plugin, the error
     * consumer will be called with the caught {@link Throwable} and loading will be continued. This allows to deal
     * with errors, (e.g. logging them) while not losing the "sandbox" preventing faulty modules from crashing the
     * whole plugin with an unexpected exception or error.
     *
     * @param moduleClasses the classes to be loaded
     * @param errorConsumer a consumer which is called when an error is encountered
     */
    public void loadAll(List<Class<? extends MTCModule>> moduleClasses,
                        BiConsumer<MTCModuleRuntimeMeta<?>, Throwable> errorConsumer) {

        Stack<Class<? extends MTCModule>> dependencyStack = new Stack<>();

        moduleClasses.stream()
                .map(this::getModuleMeta)
                .forEach(meta -> {
                    try {
                        initialise(meta, dependencyStack);
                    } catch (Throwable t) {
                        /* this is kind of a sandbox, so that a single broken module can't break the
                           whole plugin. Not using Exception because of more nasty stuff like NoClassDefFoundError,
                           which might be raised when a module is missing external dependencies. */
                        errorConsumer.accept(meta, t); //The main reason for using a consumer is proper unit testing
                    }
                });
    }

    private void initialise(MTCModuleRuntimeMeta<?> meta, Stack<Class<? extends MTCModule>> dependencyStack)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {

        if (meta.isInitialised()) {
            return;
        }

        try {
            discoverInjections(meta, dependencyStack); //throws IllegalArgumentException

            meta.createInstance();
            meta.setInitialised();

        } catch (NoSuchMethodException e) {
            //Wrap this specific problem with a more helpful error message to simplify usage for developers
            throw new IllegalArgumentException("Modules must specify a default constructor!", e);
        }
    }

    private boolean discoverInjections(MTCModuleRuntimeMeta<?> meta, Stack<Class<? extends MTCModule>> dependencyStack)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {

        dependencyStack.push(meta.getClazz());

        for (Field field : meta.getClazz().getDeclaredFields()) {
            InjectModule annotation = field.getAnnotation(InjectModule.class);
            if (annotation == null) {
                continue;
            }

            if (!MTCModule.class.isAssignableFrom(field.getType())) {
                manager.getPlugin().getLogger().severe(String.format("Uninjectable type annotated @InjectModule %s at %s",
                        field.getType().getName(), field.getName()));
                continue;
            }

            Class<? extends MTCModule> dependencyType = field.getType().asSubclass(MTCModule.class);
            MTCModuleRuntimeMeta<?> dependency = getModuleMeta(dependencyType);
            dependency.registerForInjection(meta, field);

            if (!dependency.isInitialised()) {
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

            if (annotation.required() && !dependency.isInitialised()) {
                throw new IllegalArgumentException(String.format("Unable to load required module dependency: %s (from %s)",
                        dependencyType.getSimpleName(), dependencyStack.toString()));
            }
        }

        dependencyStack.pop();
        return true;
    }
}
