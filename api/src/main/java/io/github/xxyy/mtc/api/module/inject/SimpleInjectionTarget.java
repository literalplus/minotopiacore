/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.api.module.inject;

import io.github.xxyy.lib.guava17.base.Preconditions;
import io.github.xxyy.mtc.api.MTCPlugin;
import io.github.xxyy.mtc.api.module.MTCModule;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Abstract base class for targets which may be injected into MTC modules.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 11.6.16
 */
public class SimpleInjectionTarget<T> implements InjectionTarget<T> {
    private final Class<T> clazz;
    private final Map<InjectionTarget<?>, Injection<T>> dependants = new HashMap<>();
    private final Map<InjectionTarget<?>, Injection<?>> dependencies = new HashMap<>();
    private final boolean isModule;
    private T instance;

    /**
     * Creates a new injection target.
     *
     * @param clazz the target class, e.g. what will be injected
     */
    public SimpleInjectionTarget(Class<T> clazz) {
        this.clazz = clazz;
        isModule = MTCModule.class.isAssignableFrom(clazz);
    }

    @Override
    public <V> Injection<T> registerDependant(InjectionTarget<V> dependant, Field field) {
        InjectMe annotation = field.getAnnotation(InjectMe.class);
        Preconditions.checkNotNull(annotation, "field must have @InjectModule annotation: %s", field);
        Injection<T> injection = new Injection<>(field, annotation, this, dependant);
        dependants.put(dependant, injection);
        dependant.getDependencies().put(this, injection);
        return injection;
    }

    @Override
    public boolean hasDependencyOn(InjectionTarget<?> dependency) {
        return dependencies.containsKey(dependency);
    }

    @Override
    public boolean hasRequiredDependencyOn(InjectionTarget<?> dependency) {
        return hasDependencyOn(dependency) && dependencies.get(dependency).getAnnotation().required();
    }

    @Override
    public Stream<Injection<?>> getRequiredDependencies() {
        return dependencies.entrySet().stream()
                .filter(e -> e.getValue().getAnnotation().required())
                .map(Map.Entry::getValue);
    }

    @Override
    public T createInstance() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Preconditions.checkState(instance == null, "Instance already present: %s", instance);
        Constructor<T> constructor;
        try {
            constructor = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            constructor = clazz.getDeclaredConstructor(MTCPlugin.class); //try MTCPlugin constructor
        }
        constructor.setAccessible(true);
        return this.instance = constructor.newInstance();
    }

    @Override
    public boolean isModule() {
        return isModule;
    }

    @Override
    public Class<T> getClazz() {
        return clazz;
    }

    @Override
    public T getInstance() {
        return instance;
    }

    @Override
    public boolean hasInstance() {
        return getInstance() != null;
    }

    @Override
    public void setInstance(T instance) {
        Preconditions.checkNotNull(instance, "instance");
        Preconditions.checkArgument(getClazz().isAssignableFrom(instance.getClass()));
        this.instance = instance;
    }

    @Override
    public Map<InjectionTarget<?>, Injection<T>> getDependants() {
        return dependants;
    }

    @Override
    public Map<InjectionTarget<?>, Injection<?>> getDependencies() {
        return dependencies;
    }
}
