/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2018 Philipp Nowak (https://github.com/xxyy) and contributors.
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
import li.l1t.mtc.api.module.ModuleManager;
import li.l1t.mtc.api.module.inject.ConstructorInjection;
import li.l1t.mtc.api.module.inject.FieldInjection;
import li.l1t.mtc.api.module.inject.Injection;
import li.l1t.mtc.api.module.inject.InjectionTarget;
import li.l1t.mtc.api.module.inject.Injector;
import li.l1t.mtc.api.module.inject.PluginDependency;
import li.l1t.mtc.api.module.inject.SimpleInjectionTarget;
import li.l1t.mtc.api.module.inject.exception.InjectionException;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
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
    public void injectAvailableDependencies(InjectionTarget<?> target) {
        target.getDependencies().values().stream()
                .peek(this::failIfRequiredButNoInstance)
                .filter(inj -> inj.getDependency().hasInstance())
                .forEach(inj -> injectInto(inj, inj.getDependency().getInstance()));
    }

    private void failIfRequiredButNoInstance(Injection<?> injection) {
        if(!injection.getDependency().hasInstance() && injection.isRequired()) {
            throw new InjectionException(String.format(
                    "Unable to inject %s into %s - missing instance. (did you resolve the dependencies?)",
                    injection.getDependency().getClazz(), injection
            ));
        }
    }

    @Override
    public void injectAll(InjectionTarget<?> injectee, Object value) {
        for (Injection<?> injection : injectee.getDependants().values()) {
            injectInto(injection, value);
        }
    }

    @Override
    @SuppressWarnings("StatementWithEmptyBody")
    public void injectInto(Injection<?> injection, Object value) {
        if (injection instanceof FieldInjection<?>) {
            injectIntoField((FieldInjection<?>) injection, value);
        } else if (injection instanceof ConstructorInjection<?, ?>) {
            //no-op, since injection already occurred at construction time
        } else {
            throw new IllegalArgumentException("unsupported injection type: " + injection.getClass() + " " + injection);
        }
    }

    private void injectIntoField(FieldInjection<?> injection, Object value) {
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
            throw new AssertionError(e);
        } catch (ExceptionInInitializerError e) {
            throw new IllegalStateException(String.format("Failed to inject %s into %s:",
                    String.valueOf(value), receivingInstance.getClass().getSimpleName()), e);
        }
    }

    @Override
    public <T> void registerInstance(T instance, Class<? super T> clazz) {
        Preconditions.checkNotNull(clazz, "clazz");
        Preconditions.checkArgument(instance == null || clazz.isAssignableFrom(instance.getClass()), "instance must inherit from clazz", instance, clazz);
        InjectionTarget<? super T> target = getTarget(clazz);
        target.setInstance(instance);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    @Override
    public <T> InjectionTarget<T> getTarget(@Nonnull Class<T> clazz) {
        return (InjectionTarget<T>) targets.stream()
                .filter(m -> m.getClazz().equals(clazz))
                .findFirst().orElseGet(() -> createTargetFor(clazz));
    }

    @SuppressWarnings("unchecked")
    private <T> InjectionTarget<T> createTargetFor(@Nonnull Class<T> clazz) {
        InjectionTarget<T> target;
        if (JavaPlugin.class.isAssignableFrom(clazz) && !clazz.equals(JavaPlugin.class)) {
            target = (InjectionTarget<T>) //<-- unchecked
                    new PluginDependency<>(clazz.<JavaPlugin>asSubclass(JavaPlugin.class));
        } else {
            target = new SimpleInjectionTarget<>(clazz);
        }
        targets.add(target);
        return target;
    }

    @Override
    public Set<InjectionTarget<?>> getTargets() {
        return targets;
    }
}
