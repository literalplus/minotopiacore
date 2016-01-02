/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module;

import java.lang.annotation.*;

/**
 * This annotation is used to declare the need for module injection for fields in different modules.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 18/06/15
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InjectModule {
    /**
     * @return whether this injection is required for operation (meaning if an reference cannot be acquired,
     * the operation will fail)
     */
    boolean required() default false;
}
