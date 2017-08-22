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
import com.me4502.modularframework.module.SpongeModuleWrapper;
import ninja.leaping.configurate.ConfigurationOptions;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameState;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A per-plugin class for managing registered modules.
 */
public class SpongeModuleController<T> extends ModuleController<T> {

    private final Game game;

    private PluginContainer pluginContainer;
    private File configurationDirectory;
    private ConfigurationOptions configurationOptions;
    private boolean overrideConfigurationNode = false;

    private final Set<SpongeModuleWrapper> moduleSet = new LinkedHashSet<>();

    /**
     * Constructs a new SpongeModuleController.
     *
     * @param plugin The plugin instance.
     */
    SpongeModuleController(T plugin, Game game) {
        super(plugin);
        this.game = game;
    }

    public void setPluginContainer(PluginContainer pluginContainer) {
        this.pluginContainer = pluginContainer;
    }

    public PluginContainer getPluginContainer() {
        return this.pluginContainer;
    }

    public void setConfigurationDirectory(File configurationDirectory) {
        this.configurationDirectory = configurationDirectory;
    }

    public File getConfigurationDirectory() {
        return this.configurationDirectory;
    }

    public void setConfigurationOptions(ConfigurationOptions configurationOptions) {
        this.configurationOptions = configurationOptions;
    }

    public ConfigurationOptions getConfigurationOptions() {
        return this.configurationOptions == null ? ConfigurationOptions.defaults() : this.configurationOptions;
    }

    public boolean isOverrideConfigurationNode() {
        return this.overrideConfigurationNode;
    }

    public void setOverrideConfigurationNode(boolean overrideConfigurationNode) {
        this.overrideConfigurationNode = overrideConfigurationNode;
    }

    public Game getGame() {
        return this.game;
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

        moduleSet.add(new SpongeModuleWrapper<>(this, clazz));
    }

    /**
     * Register a class to be a module.
     * @param className The name of the class to use.
     */
    @Override
    public void registerModule(String className) {
        moduleSet.add(new SpongeModuleWrapper(this, className));
    }

    /**
     * Register a class to be a module with a load state.
     * @param className The name of the class to use.
     * @param loadState The state to load the module.
     */
    public void registerModule(String className, GameState loadState) {
        moduleSet.add(new SpongeModuleWrapper(this, className, loadState));
    }

}
