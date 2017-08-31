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

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.Optional;

/**
 * Stores information about a single dependency injection into a constructor of a class.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-24
 */
public class ConstructorInjection<T, V> extends AbstractInjection<T> {
    private final Constructor<V> constructor;
    @Nullable
    private final InjectMe annotation;

    /**
     * Creates a new constructor injection.
     *
     * @param constructor the constructor this injection targets
     * @param dependency  the dependency of the injection
     * @param dependant   the dependant of the injection
     */
    ConstructorInjection(Constructor<V> constructor, InjectionTarget<T> dependency, InjectionTarget<V> dependant) {
        super(dependant, dependency);
        this.constructor = constructor;
        this.annotation = constructor.getAnnotation(InjectMe.class);
    }

    /**
     * @return the constructor this injection targets
     */
    public Constructor<V> getConstructor() {
        return constructor;
    }

    public Optional<InjectMe> getAnnotation() {
        return Optional.ofNullable(annotation);
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConstructorInjection)) return false;
        ConstructorInjection<?, ?> injection = (ConstructorInjection<?, ?>) o;
        return constructor != null ? constructor.equals(injection.constructor) : injection.constructor == null;
    }

    @Override
    public String toString() {
        return "{ConstructorInjection" +
                " into constructor=" + constructor +
                " with annotation=" + annotation +
                '}';
    }

    @Override
    public int hashCode() {
        return constructor != null ? constructor.hashCode() : 0;
    }
}
