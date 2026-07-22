package com.pocketlaunch.launcher.game

import android.content.Context

/**
 * Base abstract class for all InkClient features and overlays.
 */
abstract class Module(
    val id: String,
    val name: String,
    val description: String,
    val category: ModuleCategory
) {
    var isEnabled: Boolean = false
        private set

    open fun onEnable(context: Context) {}
    open fun onDisable(context: Context) {}
    open fun onTick(context: Context) {}

    fun toggle(context: Context): Boolean {
        isEnabled = !isEnabled
        if (isEnabled) {
            onEnable(context)
        } else {
            onDisable(context)
        }
        return isEnabled
    }

    fun setEnabledState(state: Boolean, context: Context) {
        if (isEnabled != state) {
            toggle(context)
        }
    }
}
