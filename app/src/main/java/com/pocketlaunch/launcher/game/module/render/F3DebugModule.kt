package com.pocketlaunch.launcher.game.module.render

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.pocketlaunch.launcher.game.Module
import com.pocketlaunch.launcher.game.ModuleCategory

class F3DebugModule : Module(
    id = "f3_debug",
    name = "F3 Debug Screen",
    description = "Shows real-time coordinates, biome, and frame performance data.",
    category = ModuleCategory.RENDER
) {
    private val paint = Paint().apply {
        color = Color.parseColor("#00FF66")
        textSize = 26f
        isAntiAlias = true
    }

    fun renderDebugInfo(canvas: Canvas, fps: Int, x: Double, y: Double, z: Double) {
        if (!isEnabled) return
        val startX = 30f
        var startY = 60f

        canvas.drawText("InkClient v1.0 (Bedrock)", startX, startY, paint)
        startY += 32f
        canvas.drawText("FPS: $fps", startX, startY, paint)
        startY += 32f
        canvas.drawText(String.format("XYZ: %.1f / %.1f / %.1f", x, y, z), startX, startY, paint)
        startY += 32f
        canvas.drawText("Facing: North (Negative Z)", startX, startY, paint)
    }
}
