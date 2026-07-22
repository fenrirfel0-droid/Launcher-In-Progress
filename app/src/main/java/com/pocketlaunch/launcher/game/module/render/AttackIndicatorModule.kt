package com.pocketlaunch.launcher.game.module.render

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.pocketlaunch.launcher.game.Module
import com.pocketlaunch.launcher.game.ModuleCategory

class AttackIndicatorModule : Module(
    id = "attack_indicator",
    name = "Attack Indicator",
    description = "Highlights crosshair when an enemy entity is within attack range.",
    category = ModuleCategory.RENDER
) {
    var inRangeColor: Int = Color.parseColor("#FF3366")
    var defaultColor: Int = Color.WHITE

    private val crosshairPaint = Paint().apply {
        strokeWidth = 4f
        isAntiAlias = true
    }

    fun drawCustomCrosshair(canvas: Canvas, centerX: Float, centerY: Float, isTargetInReach: Boolean) {
        if (!isEnabled) return
        crosshairPaint.color = if (isTargetInReach) inRangeColor else defaultColor

        // Render crosshair lines
        canvas.drawLine(centerX - 15f, centerY, centerX + 15f, centerY, crosshairPaint)
        canvas.drawLine(centerX, centerY - 15f, centerX, centerY + 15f, crosshairPaint)
    }
}
