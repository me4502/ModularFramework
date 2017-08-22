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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.me4502.modularframework.BukkitModuleController;
import com.me4502.modularframework.exception.ModuleNotInstantiatedException;
import com.me4502.modularframework.module.guice.ModuleInjector;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BukkitModuleWrapper<T> extends ModuleWrapper<T> {

    private T module;

    private boolean enabled = false;

    public BukkitModuleWrapper(BukkitModuleController owner, String moduleClassName) {
        super(owner, moduleClassName);
    }

    @Deprecated
    public BukkitModuleWrapper(BukkitModuleController owner, Class<T> moduleClass) {
        super(owner, moduleClass);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void enableModule() throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IOException {
        Injector injector = Guice.createInjector(new ModuleInjector(this));
        this.module = injector.getInstance(getModuleClass());

        if(getAnnotation().eventListener())
            Bukkit.getPluginManager().registerEvents((Listener) this.module, (JavaPlugin) this.getOwner().getPlugin());

        if(!getAnnotation().onEnable().isEmpty()) {
            Method meth = this.module.getClass().getMethod(getAnnotation().onEnable());
            meth.invoke(this.module);
        }

        this.enabled = true;
    }

    public void disableModule() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, IOException {

        if(!getAnnotation().onDisable().isEmpty()) {
            Method meth = this.module.getClass().getMethod(getAnnotation().onDisable());
            meth.invoke(this.module);
        }

        this.module = null;
        this.enabled = false;
    }

    /**
     * Gets the module object, if it exists.
     *
     * @return The module object
     */
    public Optional<T> getModule() {
        return Optional.ofNullable(this.module);
    }

    public T getModuleUnchecked() throws ModuleNotInstantiatedException {
        if(this.module == null)
            throw new ModuleNotInstantiatedException();
        return this.module;
    }

}
