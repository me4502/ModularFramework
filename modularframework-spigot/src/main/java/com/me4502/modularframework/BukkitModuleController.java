/*
 * Copyright (c) 2015-2017 Me4502 (Madeline Miller)
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
package com.me4502.modularframework;

import com.me4502.modularframework.module.BukkitModuleWrapper;
import com.me4502.modularframework.module.Module;

import java.io.File;

/**
 * A per-plugin class for managing registered modules.
 */
public class BukkitModuleController<T> extends ModuleController<T> {

    private File configurationDirectory;
    private boolean overrideConfigurationNode = false;

    BukkitModuleController(T plugin) {
        super(plugin);
    }

    public void setConfigurationDirectory(File configurationDirectory) {
        this.configurationDirectory = configurationDirectory;
    }

    public File getConfigurationDirectory() {
        return this.configurationDirectory;
    }

    /**
     * Register a class to be a module.
     * @param clazz The class containing the module.
     */
    @Override
    @Deprecated
    public void registerModule(Class<?> clazz) {
        if(!clazz.isAnnotationPresent(Module.class))
            throw new IllegalArgumentException("Passed class is not a Module!");

        this.moduleSet.add(new BukkitModuleWrapper<>(this, clazz));
    }

    /**
     * Register a class to be a module.
     * @param className The name of the class to use.
     */
    @Override
    public void registerModule(String className) {
        moduleSet.add(new BukkitModuleWrapper(this, className));
    }

}
