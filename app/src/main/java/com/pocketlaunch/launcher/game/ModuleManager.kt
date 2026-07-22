package com.pocketlaunch.launcher.game

import android.content.Context
import android.util.Log

object ModuleManager {

    private val moduleRegistry = mutableMapOf<String, Module>()

    fun register(module: Module) {
        moduleRegistry[module.id] = module
        Log.d("ModuleManager", "Registered [${module.category.displayName}]: ${module.name}")
    }

    fun getModule(id: String): Module? = moduleRegistry[id]

    fun getAllModules(): List<Module> = moduleRegistry.values.toList()

    fun getModulesByCategory(category: ModuleCategory): List<Module> {
        return moduleRegistry.values.filter { it.category == category }
    }

    fun toggleModule(id: String, context: Context): Boolean {
        val module = moduleRegistry[id] ?: return false
        return module.toggle(context)
    }
}
