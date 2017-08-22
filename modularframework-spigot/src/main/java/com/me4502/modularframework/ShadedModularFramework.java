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
package com.me4502.modularframework;

import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Wrapper class to call main functions from when used via Maven/Gradle shading.
 */
public class ShadedModularFramework {

    /**
     * Internal list of Module Controllers.
     */
    private static List<BukkitModuleController> controllerList = new ArrayList<>();

    /**
     * Register a new Module Controller.
     *
     * @param plugin The plugin object to register with.
     * @param <T> The plugin type.
     * @return The newly registered BukkitModuleController.
     */
    public static <T extends JavaPlugin> BukkitModuleController<T> registerModuleController(T plugin) {
        //If a real copy is installed, use that over this one.
        try {
            Class clazz = Class.forName("com.me4502.modularframework.ModularFramework");
            return (BukkitModuleController) clazz.getMethod("registerModuleController").invoke(null, plugin);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        BukkitModuleController<T> controller =  new BukkitModuleController<>(plugin);
        controllerList.add(controller);
        return controller;
    }

    /**
     * Gets an immutable list of all {@link BukkitModuleController}s.
     *
     * @return The immutable list
     */
    public static List<BukkitModuleController> getModuleControllers() {
        return Collections.unmodifiableList(controllerList);
    }

    /**
     * Unregisters a {@link BukkitModuleController}.
     *
     * @param controller The controller to unregister
     */
    public static void unregisterModuleController(BukkitModuleController controller) {
        controller.disableModules();
        controllerList.remove(controller);
    }

}
