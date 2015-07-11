package com.me4502.ModularFramework;

import org.spongepowered.api.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class to call main functions from when used via Maven/Gradle shading.
 */
public class ShadedModularFramework {

    /**
     * Internal list of Module Controllers.
     */
    protected static List<ModuleController> controllerList = new ArrayList<ModuleController>();

    /**
     * Register a new Module Controller.
     *
     * @param plugin The plugin object to register with.
     * @param game The game.
     * @return The newly registered ModuleController.
     */
    public static ModuleController registerModuleController(Object plugin, Game game) {
        //If a real copy is installed, use that over this one.
        try {
            Class clazz = Class.forName("com.me4502.ModularFramework.ModularFramework");
            return (ModuleController) clazz.getMethod("registerModuleController").invoke(null, plugin, game);
        } catch(Throwable t) {
        }

        ModuleController controller =  new ModuleController(plugin, game);
        controllerList.add(controller);
        return controller;
    }
}
