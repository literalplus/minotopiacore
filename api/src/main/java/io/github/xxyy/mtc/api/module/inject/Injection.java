/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.api.module.inject;

import java.lang.reflect.Field;

/**
 * Stores information about a single dependency injection into a single field of a single class.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-06-11
 */
public class Injection<T> {
    private final Field field;
    private final InjectMe annotation;
    private final InjectionTarget<T> dependency;
    private final InjectionTarget<?> dependant;

    /**
     * Creates a new injection.
     *
     * @param field      the field this injection targets
     * @param annotation the injection annotation on field
     * @param dependency the dependency of the injection, e.g. what we inject
     * @param dependant  the dependant of the injection, e.g. what we inject into
     */
    Injection(Field field, InjectMe annotation, InjectionTarget<T> dependency, InjectionTarget<?> dependant) {
        this.field = field;
        this.annotation = annotation;
        this.dependency = dependency;
        this.dependant = dependant;
    }

    /**
     * @return the field this injection targets, e.g. what we inject into
     */
    public Field getField() {
        return field;
    }

    /**
     * @return the injection annotation on the field, e.g. how we inject
     */
    public InjectMe getAnnotation() {
        return annotation;
    }

    /**
     * @return the dependency of the injection, e.g. what we inject
     */
    public InjectionTarget<T> getDependency() {
        return dependency;
    }

    /**
     * @return the dependant of this injection, e.g. what we inject into
     */
    public InjectionTarget<?> getDependant() {
        return dependant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Injection)) return false;

        Injection<?> injection = (Injection<?>) o;

        return field != null ? field.equals(injection.field) : injection.field == null;

    }

    @Override
    public int hashCode() {
        return field != null ? field.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Injection: dependency=" + dependency +
                "into field=" + field +
                "of dependant=" + dependant +
                "with annotation=" + annotation +
                '}';
    }
}
