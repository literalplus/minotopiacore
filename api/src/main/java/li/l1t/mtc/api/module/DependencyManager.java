/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.module;

import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.api.module.inject.InjectionTarget;
import li.l1t.mtc.api.module.inject.exception.CyclicDependencyException;
import li.l1t.mtc.api.module.inject.exception.InjectionException;
import li.l1t.mtc.api.module.inject.exception.SilentFailException;

/**
 * Manages dependencies for dependency injection.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 12.6.16
 */
public interface DependencyManager {
    /**
     * Initialises given target with its declared dependencies. <p>Dependencies that have not yet
     * been initialised themselves are initialised when discovered. If such dependency fails to
     * initialise and is required by the target, an exception is thrown. If it is not required, it
     * is silently ignored.</p>
     *
     * @param meta the target to initialise with its dependencies according to {@link InjectMe}
     *             annotations
     * @throws InjectionException        if a general error occurs trying to inject a dependency
     * @throws CyclicDependencyException if an {@linkplain CyclicDependencyException illegal cyclic
     *                                   dependency} is detected
     * @throws SilentFailException       if a {@linkplain SilentFailException silent initialisation
     *                                   failure} occurs attempting to inject a direct dependency of
     *                                   the target or a required transiative dependency
     */
    void initialise(InjectionTarget<?> meta) throws InjectionException;
}
