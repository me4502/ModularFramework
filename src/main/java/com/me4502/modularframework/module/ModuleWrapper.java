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
import com.me4502.modularframework.ModuleController;
import com.me4502.modularframework.exception.ModuleNotInstantiatedException;
import com.me4502.modularframework.module.guice.ModuleConfiguration;
import com.me4502.modularframework.module.guice.ModuleInjector;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Wraps a {@link Module} in a tangible object.
 */
public class ModuleWrapper {

    private final ModuleController owner;

    private final String moduleClassName;
    private Class<?> moduleClass;
    private Object module;

    private boolean enabled = false;

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

    public Class<?> getModuleClass() throws ClassNotFoundException {
        if(moduleClass == null) {
            moduleClass = Class.forName(moduleClassName);
            if(!moduleClass.isAnnotationPresent(Module.class))
                throw new IllegalArgumentException("Module " + moduleClassName + " is not a module!");
        }
        return moduleClass;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void enableModule() throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IOException {
        Injector injector = Guice.createInjector(new ModuleInjector(this));
        this.module = injector.getInstance(getModuleClass());

        if(getAnnotation().eventListener())
            owner.getGame().getEventManager().registerListeners(owner.getPlugin(), module);

        if(!getAnnotation().onEnable().isEmpty()) {
            Method meth = module.getClass().getMethod(getAnnotation().onEnable());
            meth.invoke(module);
        }

        loadAndSaveConfiguration();

        enabled = true;
    }

    public void disableModule() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, IOException {

        if(!getAnnotation().onDisable().isEmpty()) {
            Method meth = module.getClass().getMethod(getAnnotation().onDisable());
            meth.invoke(module);
        }

        loadAndSaveConfiguration();

        module = null;
        enabled = false;
    }

    private void loadAndSaveConfiguration() throws ClassNotFoundException, IllegalAccessException, IOException {
        for(Field field : module.getClass().getFields()) {
            if(field.isAnnotationPresent(ModuleConfiguration.class)) {
                File config = new File(this.owner.getConfigurationDirectory(), getAnnotation().moduleName() + ".conf");
                if(!config.exists())
                    config.createNewFile();
                ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setFile(config).build();
                if (this.owner.isOverrideConfigurationNode()) {
                    field.set(module, configLoader.load(this.owner.getConfigurationOptions()));
                }
                configLoader.save((ConfigurationNode) field.get(module));
            }
        }
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

    public String getId() {
        try {
            return getAnnotation().moduleId();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getName() {
        try {
            if(getAnnotation().moduleName().equals("")) {
                return getId();
            }
            return getAnnotation().moduleName();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getVersion() {
        try {
            return getAnnotation().moduleVersion();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<String> getAuthors() {
        try {
            return Arrays.asList(getAnnotation().moduleAuthors());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Module getAnnotation() throws ClassNotFoundException {
        if(annotation == null)
            annotation = getModuleClass().getAnnotation(Module.class);
        return annotation;
    }
}
