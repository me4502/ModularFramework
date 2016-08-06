/*
 * Copyright (c) 2015-2016 Me4502 (Madeline Miller)
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

import java.io.File;
import java.io.IOException;

public class ModuleInjector extends AbstractModule {

    private ModuleWrapper moduleWrapper;

    public ModuleInjector(ModuleWrapper moduleWrapper) {
        this.moduleWrapper = moduleWrapper;
    }

    @Override
    protected void configure() {
        bind(ConfigurationNode.class).annotatedWith(ModuleConfiguration.class).toProvider(ConfigurationProvider.class);
    }

    private class ConfigurationProvider implements Provider<ConfigurationNode> {

        @Override
        public ConfigurationNode get() {
            ConfigurationNode configNode = null;

            if(moduleWrapper.getOwner().getConfigurationDirectory() != null) {
                try {
                    File config = new File(moduleWrapper.getOwner().getConfigurationDirectory(), moduleWrapper.getAnnotation().moduleName() + ".conf");
                    if(!config.exists())
                        config.createNewFile();
                    ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setFile(config).build();
                    configNode = configLoader.load(moduleWrapper.getOwner().getConfigurationOptions());
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }

            return configNode;
        }
    }
}
