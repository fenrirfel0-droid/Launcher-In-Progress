package com.pocketlaunch.launcher.game

import android.content.Context
import android.graphics.Canvas
import android.util.Log

class InkClientGameEngine(private val context: Context) {

    var isSessionActive: Boolean = false
        private set

    fun startSession() {
        isSessionActive = true
        Log.d("InkClientGameEngine", "Started InkClient session")
    }

    fun endSession() {
        isSessionActive = false
        Log.d("InkClientGameEngine", "Ended InkClient session")
    }

    fun onTick() {
        if (!isSessionActive) return
        for (module in ModuleManager.getAllModules()) {
            if (module.isEnabled) {
                module.onTick(context)
            }
        }
    }

    fun onRenderOverlay(canvas: Canvas) {
        if (!isSessionActive) return
    }
}
