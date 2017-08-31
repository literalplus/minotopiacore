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
