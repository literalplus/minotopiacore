/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.module.inject;

import java.lang.reflect.Field;

/**
 * Stores information about a single dependency injection into a single field of a single class.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-06-11
 */
public class FieldInjection<T> extends AbstractInjection<T> {
    private final Field field;
    private final InjectMe annotation;

    /**
     * Creates a new injection.
     *
     * @param field      the field this injection targets
     * @param annotation the injection annotation on field
     * @param dependency the dependency of the injection, e.g. what we inject
     * @param dependant  the dependant of the injection, e.g. what we inject into
     */
    FieldInjection(Field field, InjectMe annotation, InjectionTarget<T> dependency, InjectionTarget<?> dependant) {
        super(dependant, dependency);
        this.field = field;
        this.annotation = annotation;
    }

    /**
     * @return the field this injection targets, e.g. what we inject into
     */
    public Field getField() {
        return field;
    }

    public InjectMe getAnnotation() {
        return annotation;
    }

    @Override
    public boolean isRequired() {
        return getAnnotation().required();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldInjection)) return false;

        FieldInjection<?> injection = (FieldInjection<?>) o;
        return field != null ? field.equals(injection.field) : injection.field == null;
    }

    @Override
    public String toString() {
        return "{FieldInjection" +
                " into field=" + field +
                " with annotation=" + annotation +
                '}';
    }

    @Override
    public int hashCode() {
        return field != null ? field.hashCode() : 0;
    }

}
