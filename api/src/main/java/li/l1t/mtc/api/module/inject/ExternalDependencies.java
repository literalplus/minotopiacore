/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
