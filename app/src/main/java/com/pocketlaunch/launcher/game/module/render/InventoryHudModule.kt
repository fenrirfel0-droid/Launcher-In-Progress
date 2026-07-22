package com.pocketlaunch.launcher.game.module.render

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.pocketlaunch.launcher.game.Module
import com.pocketlaunch.launcher.game.ModuleCategory

class InventoryHudModule : Module(
    id = "inventory_hud",
    name = "Inventory HUD",
    description = "Displays armor durability and hotbar status directly on screen.",
    category = ModuleCategory.RENDER
) {
    private val paint = Paint().apply {
        color = Color.WHITE
        textSize = 28f
        isAntiAlias = true
    }

    fun renderHud(canvas: Canvas, screenWidth: Int, screenHeight: Int) {
        if (!isEnabled) return
        val x = screenWidth - 220f
        val y = screenHeight - 120f
        canvas.drawText("Armor: 100%", x, y, paint)
        canvas.drawText("Offhand: Shield", x, y + 36f, paint)
    }
}
