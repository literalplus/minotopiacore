/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.module.inject;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to declare the need for dependency injection on a field.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-06-11
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InjectMe {
    /**
     * Gets whether this injection is a required injection. If a required injection cannot be
     * performed, some kind of error will be raised during the injection process.
     *
     * @return whether this injection is required
     */
    boolean required() default true;

    /**
     * Gets whether the module enabling should fail silently if the required dependency could not be
     * injected. This is helpful for submodules.
     *
     * @return whether missing required dependencies should about loading but not log messages
     */
    boolean failSilently() default false;

    /**
     * @return a hint to the injector
     */
    String hint() default "";
}
