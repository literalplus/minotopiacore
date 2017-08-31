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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates an injectable class that depends upon external APIs that may not be available at
 * runtime, such as external plugins.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-30
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExternalDependencies {
    /**
     * This parameter allows developers to specify class name patterns of external dependencies. If
     * a {@link NoClassDefFoundError} is encountered during the loading of the injectable class
     * containing any of these patterns, it is silently ignored. <p> Example usage: </p>
     * <pre>
     * import external.plugin.some.Yolo;
     * import external.plugin.something.Example;
     * import ru.tehkode.permissions.PermissionManager;
     *
     * &#64;ExternalDependencies({"external.plugin"})
     * public class MyFaultTolerantModule {
     *     &#64;InjectMe
     *     private Yolo yolo = Yolo.getInstance();
     *
     *     //note that non-injected fields also work, even if they are just present
     *     //no need to initialise them
     *     private PermissionManager permissionManager;
     *
     *     &#64;InjectMe
     *     public MyFaultTolerantModule(Example example) {
     *         //we can use Example and Yolo here just like any normal
     *         //class, except that if they are missing, the injector
     *         //notices and silently ignores it and doesn't load our
     *         //module
     *     }
     * }
     * </pre>
     *
     * @return the array of class name patterns to be ignored in {@link NoClassDefFoundError}s
     */
    String[] value();
}
