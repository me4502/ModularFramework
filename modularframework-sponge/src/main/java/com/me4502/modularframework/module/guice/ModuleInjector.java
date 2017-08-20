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
package com.me4502.modularframework.module.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.me4502.modularframework.module.ModuleWrapper;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.io.IOException;

public class ModuleInjector extends AbstractModule {

    private static ModuleWrapper moduleWrapper;

    public ModuleInjector(ModuleWrapper moduleWrapper) {
        ModuleInjector.moduleWrapper = moduleWrapper;
    }

    @Override
    protected void configure() {
        bind(PluginContainer.class).annotatedWith(ModuleContainer.class).toProvider(ContainerProvider.class);
        bind(ConfigurationNode.class).annotatedWith(ModuleConfiguration.class).toProvider(ConfigurationProvider.class);
    }

    private static class ContainerProvider implements Provider<PluginContainer> {

        @Override
        public PluginContainer get() {
            return moduleWrapper.getOwner().getPluginContainer();
        }
    }

    private static class ConfigurationProvider implements Provider<ConfigurationNode> {

        @Override
        public ConfigurationNode get() {
            ConfigurationNode configNode = null;

            if(moduleWrapper.getOwner().getConfigurationDirectory() != null) {
                try {
                    File legacyConfig = new File(moduleWrapper.getOwner().getConfigurationDirectory(), moduleWrapper.getName() + ".conf");
                    File config = new File(moduleWrapper.getOwner().getConfigurationDirectory(), moduleWrapper.getId()+ ".conf");
                    if(!moduleWrapper.getName().equals(moduleWrapper.getId()) && legacyConfig.exists()) {
                        legacyConfig.renameTo(config);
                    }
                    if(!config.exists())
                        config.createNewFile();
                    ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setFile(config).build();
                    configNode = configLoader.load(moduleWrapper.getOwner().getConfigurationOptions());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return configNode;
        }
    }
}
