/*
 * Copyright (c) 2015-2017 Me4502 (Matthew Miller)
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

import com.me4502.modularframework.ModuleController;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Wraps a {@link Module} in a tangible object.
 */
public abstract class ModuleWrapper<T> {

    private final ModuleController owner;
    private final String moduleClassName;

    private Class<T> moduleClass;
    private Module annotation;

    public ModuleWrapper(ModuleController owner, String moduleClassName) {
        this.owner = owner;
        this.moduleClassName = moduleClassName;
    }

    public ModuleWrapper(ModuleController owner, Class<T> clazz) {
        this.owner = owner;
        this.moduleClassName = clazz.getName();
        this.moduleClass = clazz;
    }

    public Class<T> getModuleClass() throws ClassNotFoundException {
        if(this.moduleClass == null) {
            this.moduleClass = (Class<T>) Class.forName(this.moduleClassName);
            if(!this.moduleClass.isAnnotationPresent(Module.class))
                throw new IllegalArgumentException("Module " + moduleClassName + " is not a module!");
        }
        return this.moduleClass;
    }

    public abstract boolean isEnabled();

    public ModuleController getOwner() {
        return this.owner;
    }

    /**
     * Gets the module object, if it exists.
     *
     * @return The module object
     */
    public abstract Optional<T> getModule();

    public abstract void enableModule() throws IllegalAccessException, InstantiationException, ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException, IOException;

    public abstract void disableModule() throws IllegalAccessException, ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, IOException;

    public Module getAnnotation() throws ClassNotFoundException {
        if (this.annotation == null)
            this.annotation = this.getModuleClass().getAnnotation(Module.class);
        return this.annotation;
    }

    public String getId() {
        try {
            return getAnnotation().id().toLowerCase();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getName() {
        try {
            if(getAnnotation().name().isEmpty()) {
                return getId();
            }
            return getAnnotation().name();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getVersion() {
        try {
            return getAnnotation().version();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<String> getAuthors() {
        try {
            return Arrays.asList(getAnnotation().authors());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
