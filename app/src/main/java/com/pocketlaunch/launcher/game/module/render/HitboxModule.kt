package com.pocketlaunch.launcher.game.module.render

import android.content.Context
import android.util.Log
import com.pocketlaunch.launcher.game.Module
import com.pocketlaunch.launcher.game.ModuleCategory

class HitboxModule : Module(
    id = "hitbox",
    name = "Hitbox Display",
    description = "Renders bounding box visual outlines around entities.",
    category = ModuleCategory.RENDER
) {
    var showEyeLine: Boolean = true
    var showDirectionVector: Boolean = true

    override fun onEnable(context: Context) {
        Log.d("HitboxModule", "Hitbox visualization enabled")
    }

    override fun onDisable(context: Context) {
        Log.d("HitboxModule", "Hitbox visualization disabled")
    }
}
