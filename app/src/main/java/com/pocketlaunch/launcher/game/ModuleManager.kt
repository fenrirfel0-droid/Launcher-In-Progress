package com.pocketlaunch.launcher.game

import android.content.Context
import android.util.Log
import com.pocketlaunch.launcher.game.module.render.*
import com.pocketlaunch.launcher.game.module.utilities.*

object ModuleManager {

    private const val TAG = "ModuleManager"
    private val moduleRegistry = LinkedHashMap<String, Module>()

    init {
        // Guarantee all modules are populated when ModuleManager is accessed
        registerDefaultModules()
    }

    private fun registerDefaultModules() {
        // --- RENDER MODULES ---
        register(HitboxModule())
        register(InventoryHudModule())
        register(F3DebugModule())
        register(FogCustomizerModule())
        register(CrystalOptimizerModule())
        register(CpsFpsHudModule())
        register(AttackIndicatorModule())

        // --- UTILITIES MODULES ---
        register(QuickDropModule())
        register(ZoomModule())
        register(PerspectiveModule())
        register(FovModifierModule())
        register(PackChangerModule())
        register(ShaderLoaderModule())
        register(SkinStealerModule())
    }

    fun register(module: Module) {
        moduleRegistry[module.id] = module
        Log.d(TAG, "Registered module [${module.category.displayName}]: ${module.name}")
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
