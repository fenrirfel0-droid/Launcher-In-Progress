package com.pocketlaunch.launcher.game.module.utilities

import android.content.Context
import android.util.Log
import com.pocketlaunch.launcher.game.Module
import com.pocketlaunch.launcher.game.ModuleCategory

class QuickDropModule : Module(
    id = "quick_drop",
    name = "Quick Drop",
    description = "Dedicated button to instantly drop item stacks.",
    category = ModuleCategory.UTILITIES
) {
    fun triggerDrop(context: Context) {
        if (!isEnabled) return
        Log.d("QuickDropModule", "Executed quick drop item action")
    }
}
