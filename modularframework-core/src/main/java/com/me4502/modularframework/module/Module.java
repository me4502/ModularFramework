/*
 * Copyright (c) Me4502 (Madeline Miller)
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.me4502.modularframework.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a Module. Any class that is a module is capable of being loaded with this framework.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Module {

    /**
     * The id of this module. This is required.
     * @return The id of this module
     */
    String id();

    /**
     * The name of this module.
     * @return The name of this module
     */
    String name() default "";

    /**
     * The version of this module.
     * @return The version of this module
     */
    String version() default "1.0.0";

    /**
     * The authors of this module.
     * @return The authors of this module
     */
    String[] authors() default {};

    /**
     * Whether the module requires an event listener registration.
     * @return If the module listens to events
     */
    boolean eventListener() default true;

    /**
     * The name of a method called when this module is enabled.
     * @return The method to call when enabled.
     */
    String onEnable() default "";

    /**
     * The name of a method called when this module is disabled.
     * @return The method to call when disabled.
     */
    String onDisable() default "";
}
