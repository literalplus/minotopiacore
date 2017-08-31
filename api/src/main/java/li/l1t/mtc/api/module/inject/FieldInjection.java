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
