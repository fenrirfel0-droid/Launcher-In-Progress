package com.pocketlaunch.launcher.game.module.render

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import com.pocketlaunch.launcher.game.Module
import com.pocketlaunch.launcher.game.ModuleCategory

class CpsFpsHudModule : Module(
    id = "cps_fps_hud",
    name = "CPS & FPS Counter",
    description = "Displays live CPS and frame rates on screen.",
    category = ModuleCategory.RENDER
) {
    private val clickTimestamps = mutableListOf<Long>()
    private var lastFps = 60

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 30f
        isAntiAlias = true
    }

    fun registerClick() {
        val now = System.currentTimeMillis()
        clickTimestamps.add(now)
    }

    fun getCps(): Int {
        val now = System.currentTimeMillis()
        clickTimestamps.removeAll { now - it > 1000 }
        return clickTimestamps.size
    }

    fun renderHud(canvas: Canvas, currentFps: Int) {
        if (!isEnabled) return
        lastFps = currentFps
        val displayStr = "FPS: $lastFps | CPS: ${getCps()}"
        canvas.drawText(displayStr, 40f, 160f, textPaint)
    }
}
