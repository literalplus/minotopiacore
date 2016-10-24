/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.module.inject;

import com.google.common.base.Preconditions;
import li.l1t.mtc.api.module.MTCModule;
import li.l1t.mtc.api.module.inject.exception.InjectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a target of {@link InjectMe} injections, which may have dependencies and dependants
 * itself.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-06-11
 */
public class SimpleInjectionTarget<T> implements InjectionTarget<T> {
    private final Class<T> clazz;
    private final Map<InjectionTarget<?>, Injection<T>> dependants = new HashMap<>();
    private final Map<InjectionTarget<?>, Injection<?>> dependencies = new HashMap<>();
    private final boolean isModule;
    private Constructor<T> constructor;
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
        Preconditions.checkNotNull(annotation, "field must have @InjectMe annotation: %s", field);
        Injection<T> injection = new FieldInjection<>(field, annotation, this, dependant);
        dependants.put(dependant, injection);
        dependant.getDependencies().put(this, injection);
        return injection;
    }

    @Override
    public <V> Injection<T> registerDependant(InjectionTarget<V> dependant, Constructor<V> constructor) {
        Injection<T> injection = new ConstructorInjection<>(constructor, this, dependant);
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
        return hasDependencyOn(dependency) && dependencies.get(dependency).isRequired();
    }

    @Override
    public Stream<Injection<?>> getRequiredDependencies() {
        return dependencies.entrySet().stream()
                .filter(e -> e.getValue().isRequired())
                .map(Map.Entry::getValue);
    }

    @Override
    public T createInstance() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Preconditions.checkState(instance == null, "Instance already present: %s", instance);
        return this.instance = instantiate();
    }

    private T instantiate() throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<T> constructor = getInjectableConstructor();
        constructor.setAccessible(true);
        return constructInstance(constructor);
    }

    @Override
    public Constructor<T> getInjectableConstructor() {
        if (this.constructor == null) {
            this.constructor = findInjectMeConstructor()
                    .orElseGet(this::findDefaultConstructorOrFail);
        }
        return this.constructor;
    }

    @SuppressWarnings("unchecked")
    private Optional<Constructor<T>> findInjectMeConstructor() {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .map(constructor -> (Constructor<T>) constructor) // https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getConstructors--
                .filter(constructor -> constructor.getAnnotation(InjectMe.class) != null)
                .reduce((a, b) -> {
                    throw new InjectionException("can only have one @InjectMe constructor on " + clazz);
                });
    }

    private Constructor<T> findDefaultConstructorOrFail() {
        try {
            return clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new InjectionException(
                    "unable to instantiate " + clazz + ": Neither an @InjectMe annotated " +
                            "constructor nor a default constructor was found");
        }
    }

    private T constructInstance(Constructor<T> constructor) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        List<?> parameterList = Arrays.stream(constructor.getParameterTypes())
                .map(this::findConstructorDependency)
                .map(objectInjection -> objectInjection.getDependency().getInstance())
                .collect(Collectors.toList());
        Object[] parameters = parameterList.toArray(new Object[parameterList.size()]);
        return constructor.newInstance(parameters);
    }

    private Injection<?> findConstructorDependency(Class<?> dependency) {
        return getDependencies().entrySet().stream()
                .filter(entry -> dependency.isAssignableFrom(entry.getKey().getClazz()))
                .map(Map.Entry::getValue)
                .reduce((a, b) -> {
                    throw new InjectionException(String.format("Ambiguous constructor dependency: " +
                                    "%s matched at least [%s, %s] in %s",
                            dependency, a, b, constructor
                    ));
                })
                .orElseThrow(() -> new InjectionException(String.format(
                        "Unknown constructor dependency: %s in %s, required by %s",
                        dependency, getDependencies(), constructor
                )));
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
