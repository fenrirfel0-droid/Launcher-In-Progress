package com.pocketlaunch.launcher.game.module.utilities

import android.content.Context
import com.pocketlaunch.launcher.game.Module
import com.pocketlaunch.launcher.game.ModuleCategory

class PerspectiveModule : Module(
    id = "perspective",
    name = "Perspective Switcher",
    description = "Toggles 360-degree freecam and perspective modes.",
    category = ModuleCategory.UTILITIES
) {
    enum class CameraMode { FIRST_PERSON, THIRD_PERSON_BACK, THIRD_PERSON_FRONT }

    var currentMode = CameraMode.FIRST_PERSON

    fun cyclePerspective(): CameraMode {
        currentMode = when (currentMode) {
            CameraMode.FIRST_PERSON -> CameraMode.THIRD_PERSON_BACK
            CameraMode.THIRD_PERSON_BACK -> CameraMode.THIRD_PERSON_FRONT
            CameraMode.THIRD_PERSON_FRONT -> CameraMode.FIRST_PERSON
        }
        return currentMode
    }
}
