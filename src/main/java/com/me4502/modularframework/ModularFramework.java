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

import org.spongepowered.api.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The base class of the Modular Framework. If this library is shaded into your plugin, use {@link ShadedModularFramework}.
 */
public class ModularFramework {

    /**
     * Internal list of Module Controllers.
     */
    private static final List<ModuleController> controllerList = new ArrayList<>();

    /**
     * Register a new Module Controller.
     *
     * @param plugin The plugin object to register with.
     * @param game The game.
     * @return The newly registered ModuleController.
     */
    public static <T> ModuleController<T> registerModuleController(T plugin, Game game) {
        ModuleController<T> controller = new ModuleController<>(plugin, game);
        controllerList.add(controller);
        return controller;
    }

    /**
     * Gets an immutable list of all {@link ModuleController}s.
     *
     * @return The immutable list
     */
    public static List<ModuleController> getModuleControllers() {
        return Collections.unmodifiableList(controllerList);
    }

    /**
     * Unregisters a {@link ModuleController}.
     *
     * @param controller The controller to unregister
     */
    public static void unregisterModuleController(ModuleController controller) {
        controller.disableModules();
        controllerList.remove(controller);
    }
}
