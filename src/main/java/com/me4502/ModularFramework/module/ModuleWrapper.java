/*
 * The MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.me4502.ModularFramework.module;

import com.me4502.ModularFramework.ModuleController;
import com.me4502.ModularFramework.exception.ModuleNotInstantiatedException;

/**
 * Wraps a {@link Module} in a tangible object.
 */
public class ModuleWrapper {

    ModuleController owner;

    String moduleClassName;
    Class<?> moduleClass;
    Object module;

    public ModuleWrapper(ModuleController owner, String moduleClassName) {
        this.owner = owner;
        this.moduleClassName = moduleClassName;
    }

    @Deprecated
    public ModuleWrapper(ModuleController owner, Class moduleClass) {
        this.owner = owner;
        this.moduleClassName = moduleClass.getName();
        this.moduleClass = moduleClass;
    }

    private Class<?> getModuleClass() throws ClassNotFoundException {
        if(moduleClass == null) {
            moduleClass = Class.forName(moduleClassName);
            if(!moduleClass.isAnnotationPresent(Module.class))
                throw new IllegalArgumentException("Module " + moduleClassName + " is not a module!");
        }
        return moduleClass;
    }

    public void enableModule() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        this.module = getModuleClass().newInstance();
        if(getAnnotation().eventListener())
            owner.getGame().getEventManager().register(owner.getPlugin(), module);
    }

    public ModuleController getOwner() {
        return this.owner;
    }

    public Object getModule() throws ModuleNotInstantiatedException {
        if(this.module == null)
            throw new ModuleNotInstantiatedException();
        return this.module;
    }

    //Cache the annotation. I have no idea what the performance overhead for not doing this is, but meh.
    private Module annotation;

    public String getName() {
        try {
            return getAnnotation().moduleName();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return moduleClassName;
    }

    public Module getAnnotation() throws ClassNotFoundException {
        if(annotation == null)
            annotation = getModuleClass().getAnnotation(Module.class);
        return annotation;
    }
}
