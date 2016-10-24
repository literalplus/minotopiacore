/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.module.inject;

/**
 * Abstract base class for injections.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-24
 */
abstract class AbstractInjection<T> implements Injection<T> {
    private final InjectionTarget<T> dependency;
    private final InjectionTarget<?> dependant;

    public AbstractInjection(InjectionTarget<?> dependant, InjectionTarget<T> dependency) {
        this.dependant = dependant;
        this.dependency = dependency;
    }

    @Override
    public InjectionTarget<T> getDependency() {
        return dependency;
    }

    @Override
    public InjectionTarget<?> getDependant() {
        return dependant;
    }

    @Override
    public String toString() {
        return "{AbstractInjection: dependency=" + dependency +
                "of dependant=" + dependant +
                '}';
    }
}
