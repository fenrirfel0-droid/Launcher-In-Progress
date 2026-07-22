package com.pocketlaunch.launcher.game

import android.content.Context
import android.util.Log
import com.pocketlaunch.launcher.game.module.render.*
import com.pocketlaunch.launcher.game.module.utilities.*

object ModuleManager {

    private val moduleRegistry = mutableMapOf<String, Module>()

    init {
        registerRenderModules()
        registerUtilitiesModules()
    }

    private fun registerRenderModules() {
        register(HitboxModule())
        register(InventoryHudModule())
        register(F3DebugModule())
        register(FogCustomizerModule())
        register(CrystalOptimizerModule())
        register(CpsFpsHudModule())
        register(AttackIndicatorModule())
    }

    private fun registerUtilitiesModules() {
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
