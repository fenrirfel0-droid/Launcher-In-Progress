package com.pocketlaunch.launcher.game.module.utilities

import android.content.Context
import com.pocketlaunch.launcher.game.Module
import com.pocketlaunch.launcher.game.ModuleCategory

class ZoomModule : Module(
    id = "zoom",
    name = "Zoom",
    description = "Magnifies field of view for scouting and aiming.",
    category = ModuleCategory.UTILITIES
) {
    var isZooming: Boolean = false
    var zoomFovTarget: Int = 30

    fun toggleZoom(): Int {
        isZooming = !isZooming
        return if (isZooming) zoomFovTarget else 70
    }
}
