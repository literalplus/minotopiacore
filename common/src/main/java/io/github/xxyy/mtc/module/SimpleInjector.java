/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module;

import org.bukkit.plugin.java.JavaPlugin;

import io.github.xxyy.lib.intellij_annotations.NotNull;
import io.github.xxyy.mtc.api.module.ModuleManager;
import io.github.xxyy.mtc.api.module.inject.Injection;
import io.github.xxyy.mtc.api.module.inject.InjectionTarget;
import io.github.xxyy.mtc.api.module.inject.Injector;
import io.github.xxyy.mtc.api.module.inject.PluginDependency;
import io.github.xxyy.mtc.api.module.inject.SimpleInjectionTarget;

import java.util.HashSet;
import java.util.Set;

/**
 * Handles actual dependency injection on field level.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 11.6.16
 */
class SimpleInjector implements Injector {
    private final ModuleManager manager;
    private final Set<InjectionTarget<?>> targets = new HashSet<>(); //not necessarily initialised

    SimpleInjector(ModuleManager manager) {
        this.manager = manager;
    }

    @Override
    public void injectAll(InjectionTarget<?> injectee, Object value) {
        for (Injection<?> injection : injectee.getDependants().values()) {
            injectInto(injection, value);
        }
    }

    @Override
    public void injectInto(Injection<?> injection, Object value) {
        if (!injection.getField().isAccessible()) {
            injection.getField().setAccessible(true);
        }

        Object receivingInstance = injection.getDependant().getInstance();
        if (receivingInstance == null) {
            manager.getPlugin().getLogger().warning(String.format("Uninitialised injection target %s: %s",
                    injection.getDependency().getClazz().getName(), injection.getField().toString()));
            return;
        }

        try {
            injection.getField().set(receivingInstance, value);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e); //Actually, this should never happen, but.
        } catch (ExceptionInInitializerError e) {
            throw new IllegalStateException(String.format("Failed to inject %s into %s:",
                    String.valueOf(value), receivingInstance.getClass().getSimpleName()), e);
        }
    }

    @Override
    public <T> void registerInstance(T instance) {
        @SuppressWarnings("unchecked")
        InjectionTarget<T> target = getTarget((Class<T>) instance.getClass());
        if (!instance.equals(target.getInstance())) {
            target.setInstance(instance);
        }
    }

    @NotNull
    @SuppressWarnings("unchecked")
    @Override
    public <T> InjectionTarget<T> getTarget(@NotNull Class<T> clazz) {
        InjectionTarget<T> target = (InjectionTarget<T>) targets.stream()
                .filter(m -> m.getClazz().equals(clazz))
                .findFirst().orElse(null);

        if (target == null) {
            if (JavaPlugin.class.isAssignableFrom(clazz)) {
                target = (InjectionTarget<T>)
                        new PluginDependency<>(clazz.<JavaPlugin>asSubclass(JavaPlugin.class));
            } else {
                target = new SimpleInjectionTarget<>(clazz);
            }
            targets.add(target);
        }

        return target;
    }

    @Override
    public Set<InjectionTarget<?>> getTargets() {
        return targets;
    }
}
