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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Wraps a {@link Module} in a tangible object.
 */
public class ModuleWrapper<T> {

    private final ModuleController owner;

    private final String moduleClassName;
    private Class<T> moduleClass;
    private T module;

    private boolean enabled = false;

    public ModuleWrapper(ModuleController owner, String moduleClassName) {
        this.owner = owner;
        this.moduleClassName = moduleClassName;
    }

    @Deprecated
    public ModuleWrapper(ModuleController owner, Class<T> moduleClass) {
        this.owner = owner;
        this.moduleClassName = moduleClass.getName();
        this.moduleClass = moduleClass;
    }

    public Class<T> getModuleClass() throws ClassNotFoundException {
        if(moduleClass == null) {
            moduleClass = (Class<T>) Class.forName(moduleClassName);
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
                File legacyConfig = new File(this.owner.getConfigurationDirectory(), getName() + ".conf");
                File config = new File(this.owner.getConfigurationDirectory(), getId() + ".conf");
                if(!getAnnotation().name().equals(getAnnotation().id()) && legacyConfig.exists()) {
                    legacyConfig.renameTo(config);
                }
                if(!config.exists())
                    config.createNewFile();
                ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setFile(config).build();
                if(this.owner.isOverrideConfigurationNode()) {
                    field.set(module, configLoader.load(this.owner.getConfigurationOptions()));
                }
                configLoader.save((ConfigurationNode) field.get(module));
            }
        }
    }

    public ModuleController getOwner() {
        return this.owner;
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

    //Cache the annotation. I have no idea what the performance overhead for not doing this is, but meh.
    private Module annotation;

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

    public Module getAnnotation() throws ClassNotFoundException {
        if(annotation == null)
            annotation = getModuleClass().getAnnotation(Module.class);
        return annotation;
    }
}
