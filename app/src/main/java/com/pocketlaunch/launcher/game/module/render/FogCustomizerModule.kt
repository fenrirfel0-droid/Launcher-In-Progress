package com.pocketlaunch.launcher.game.module.render

import android.content.Context
import com.pocketlaunch.launcher.game.Module
import com.pocketlaunch.launcher.game.ModuleCategory
import com.pocketlaunch.launcher.util.GraphicsSettingsManager

class FogCustomizerModule : Module(
    id = "fog_customizer",
    name = "Fog Customization",
    description = "Adjusts or completely removes distance render fog.",
    category = ModuleCategory.RENDER
) {
    var fogDistanceMultiplier: Float = 2.0f

    override fun onEnable(context: Context) {
        val settings = GraphicsSettingsManager(context)
        settings.enableFastLoadingScreen(true)
    }
}
