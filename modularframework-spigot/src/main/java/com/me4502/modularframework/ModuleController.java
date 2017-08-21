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

import com.me4502.modularframework.module.Module;
import com.me4502.modularframework.module.ModuleWrapper;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A per-plugin class for managing registered modules.
 */
public class ModuleController<T extends JavaPlugin> {

    private final T plugin;
    private File configurationDirectory;
    private boolean overrideConfigurationNode = false;

    private final Set<ModuleWrapper> moduleSet = new LinkedHashSet<>();

    ModuleController(T plugin) {
        this.plugin = plugin;
    }

    public void setConfigurationDirectory(File configurationDirectory) {
        this.configurationDirectory = configurationDirectory;
    }

    public File getConfigurationDirectory() {
        return this.configurationDirectory;
    }

    public T getPlugin() {
        return this.plugin;
    }

    /**
     * Register a class to be a module.
     * @param clazz The class containing the module.
     */
    @Deprecated
    public void registerModule(Class<?> clazz) {
        if(!clazz.isAnnotationPresent(Module.class))
            throw new IllegalArgumentException("Passed class is not a Module!");

        moduleSet.add(new ModuleWrapper<>(this, clazz));
    }

    /**
     * Register a class to be a module.
     * @param className The name of the class to use.
     */
    public void registerModule(String className) {
        moduleSet.add(new ModuleWrapper(this, className));
    }


    /**
     * Gets an immutable set of all registered modules in this controller.
     * @return A set of registered modules
     */
    public Set<ModuleWrapper> getModules() {
        return Collections.unmodifiableSet(moduleSet);
    }

    /**
     * Gets the {@link ModuleWrapper} of the specified class, if it exists.
     *
     * @param clazz The class of the {@link Module}
     * @param <M> The module type.
     * @return The {@link ModuleWrapper}, if it exists
     */
    public <M> Optional<ModuleWrapper<M>> getModule(Class<M> clazz) {
        for(ModuleWrapper wrapper : moduleSet) {
            try {
                if(clazz.isInstance(wrapper.getModuleClass().getClass()))
                    return Optional.of(wrapper);
            } catch (ClassNotFoundException ignored) {
            }
        }

        return Optional.empty();
    }

    /**
     * Gets the {@link ModuleWrapper} by its id, if it exists.
     *
     * @param moduleId The id of the {@link Module}
     * @return The {@link ModuleWrapper}, if it exists
     */
    public Optional<ModuleWrapper> getModule(String moduleId) {
        return moduleSet.stream().filter(wrapper -> wrapper.getId().equals(moduleId)).findFirst();
    }

    /**
     * Enable all registered modules in this controller.
     */
    public void enableModules() {
        for(ModuleWrapper wrapper : moduleSet) {
            try {
                wrapper.enableModule();
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Enable registered modules if they pass a supplied predicate.
     * @param modulePredicate The predicate to test if a module is enabled
     */
    public void enableModules(Predicate<ModuleWrapper> modulePredicate) {
        moduleSet.stream().filter(modulePredicate).forEach(wrapper -> {
            try {
                wrapper.enableModule();
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Disable all registered modules in this controller.
     */
    public void disableModules() {
        moduleSet.stream().filter(ModuleWrapper::isEnabled).forEach(wrapper -> {
            try {
                wrapper.disableModule();
            } catch (IllegalAccessException | IOException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Disable registered modules if they pass a supplied predicate.
     * @param modulePredicate The predicate to test if a module is disabled
     */
    public void disableModules(Predicate<ModuleWrapper> modulePredicate) {
        moduleSet.stream().filter(ModuleWrapper::isEnabled).filter(modulePredicate).forEach(wrapper -> {
            try {
                wrapper.disableModule();
            } catch (IllegalAccessException | IOException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}
