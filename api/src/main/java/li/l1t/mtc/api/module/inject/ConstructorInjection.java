/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
