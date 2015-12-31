/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module;

import io.github.xxyy.lib.guava17.base.Preconditions;
import io.github.xxyy.mtc.api.module.MTCModule;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Provides runtime metadata related to loading, unloading, enabling and disabling {@link MTCModule}s.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 23/06/15
 */
class MTCModuleRuntimeMeta<T extends MTCModule> {
    static {
        InjectionTarget.class.getName(); //Prevents a NoClassDefFoundError on disable if MTC jar is replaced
    }

    private final Class<T> clazz;
    private final Set<InjectionTarget> injectionTargets = new HashSet<>();
    private final Map<MTCModuleRuntimeMeta<?>, Boolean> dependants = new HashMap<>();
    //dependency -> required?
    private final Map<MTCModuleRuntimeMeta<?>, Boolean> dependencies = new HashMap<>();
    private T module;
    private boolean initialised = false;

    public MTCModuleRuntimeMeta(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void registerForInjection(MTCModuleRuntimeMeta<?> dependant, Field field) {
        InjectModule annotation = field.getAnnotation(InjectModule.class);
        Preconditions.checkNotNull(annotation, "field must have @InjectModule annotation: %s", field);
        injectionTargets.add(new InjectionTarget(field, annotation, dependant));
        dependants.put(dependant, annotation.required());
        dependant.getDependencies().put(this, annotation.required());
    }

    public boolean hasDependency(MTCModuleRuntimeMeta<?> dependency) {
        return dependencies.containsKey(dependency);
    }

    public boolean hasRequiredDependency(MTCModuleRuntimeMeta<?> dependency) {
        return hasDependency(dependency) && dependencies.get(dependency); //apparently null == true -> error: http://ideone.com/Y6GtNS
    }

    public Stream<MTCModuleRuntimeMeta<?>> getRequiredDependencyStream() {
        return dependencies.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey);
    }

    public T createInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Preconditions.checkState(module == null, "Instance already present: %s", module);
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return this.module = constructor.newInstance();
    }

    ///////////////////////////////////////////////////////////////////////////////// getters, setters, standard methods

    public Class<T> getClazz() {
        return clazz;
    }

    public Set<InjectionTarget> getInjectionTargets() {
        return injectionTargets;
    }

    public T getModule() {
        return module;
    }

    public void setModule(T module) {
        Preconditions.checkState(this.module == null || module != null, "Cannot remove already-set module reference!");
        this.module = module;
    }

    public Map<MTCModuleRuntimeMeta<?>, Boolean> getDependants() {
        return dependants;
    }

    public Set<MTCModuleRuntimeMeta<?>> getDependantSet() {
        return dependants.keySet();
    }

    public Map<MTCModuleRuntimeMeta<?>, Boolean> getDependencies() {
        return dependencies;
    }

    public Set<MTCModuleRuntimeMeta<?>> getDependencySet() {
        return dependencies.keySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MTCModuleRuntimeMeta)) {
            return false;
        }

        MTCModuleRuntimeMeta<?> that = (MTCModuleRuntimeMeta<?>) o;

        return clazz.equals(that.clazz);

    }

    @Override
    public int hashCode() {
        return clazz.hashCode();
    }

    @Override
    public String toString() {
        return "MTCModuleRuntimeMeta{" +
                "clazz=" + clazz +
                ", module=" + module +
                ", initialised=" + initialised + '}';
    }

    public boolean isInitialised() {
        return initialised;
    }

    public void setInitialised() {
        initialised = true;
    }

    public static class InjectionTarget {
        private final Field field;
        private final InjectModule annotation;
        private final MTCModuleRuntimeMeta<?> meta;


        public InjectionTarget(Field field, InjectModule annotation, MTCModuleRuntimeMeta<?> meta) {
            this.field = field;
            this.annotation = annotation;
            this.meta = meta;
        }

        public Field getField() {
            return field;
        }

        public InjectModule getAnnotation() {
            return annotation;
        }

        public MTCModuleRuntimeMeta<?> getMeta() {
            return meta;
        }
    }
}
