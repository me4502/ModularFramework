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
package com.me4502.ModularFramework;

import com.me4502.ModularFramework.module.Module;
import com.me4502.ModularFramework.module.ModuleWrapper;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A per-plugin class for managing registered modules.
 */
public class ModuleController {

    Object plugin;

    private Set<ModuleWrapper> moduleSet = new HashSet<ModuleWrapper>();

    /**
     * Constructs a new ModuleController.
     *
     * @param plugin The plugin instance.
     */
    ModuleController(Object plugin) {
            this.plugin = plugin;
    }

    /**
     * Register a class to be a module.
     * @param clazz The class containing the module.
     */
    public void registerModule(Class<?> clazz) {
        if(!clazz.isAnnotationPresent(Module.class))
            throw new IllegalArgumentException("Passed class is not a Module!");

        moduleSet.add(new ModuleWrapper(this, clazz));
    }

    /**
     * Gets a set of all registered modules in this controller.
     * @return A set of registered modules
     */
    public Set<ModuleWrapper> getModules() {
        return new HashSet<ModuleWrapper>(moduleSet); //TODO Make immutable when I have internet
    }

    /**
     * Enable all registered modules in this controller.
     */
    public void enableModules() {
        for(ModuleWrapper wrapper : moduleSet) {
            try {
                wrapper.enableModule();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Enable registered modules if they pass a supplied predicate.
     * @param modulePredicate The predicate to test if a module is enabled
     */
    public void enableModules(Predicate<ModuleWrapper> modulePredicate) { //TODO Don't use JDK Predicate
        for(ModuleWrapper wrapper : moduleSet) {
            if (modulePredicate.test(wrapper)) {
                try {
                    wrapper.enableModule();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
