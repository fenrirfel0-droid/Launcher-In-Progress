package com.pocketlaunch.launcher.overlay

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.Gravity
import android.widget.Button

object MinecraftButtonStyle {

    fun createButton(context: Context, labelText: String, onClick: () -> Unit): Button {
        return Button(context).apply {
            text = labelText
            setTextColor(Color.WHITE)
            textSize = 12f
            typeface = Typeface.MONOSPACE
            gravity = Gravity.CENTER
            setPadding(24, 16, 24, 16)
            background = createMinecraftDrawable(isPressed = false)

            setOnTouchListener { v, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> v.background = createMinecraftDrawable(isPressed = true)
                    android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                        v.background = createMinecraftDrawable(isPressed = false)
                        if (event.action == android.view.MotionEvent.ACTION_UP) onClick()
                    }
                }
                true
            }
        }
    }

    private fun createMinecraftDrawable(isPressed: Boolean): LayerDrawable {
        val baseColor = if (isPressed) Color.parseColor("#801E1E1E") else Color.parseColor("#903B3B3B")
        val topHighlight = if (isPressed) Color.parseColor("#40000000") else Color.parseColor("#A08E8E8E")
        val bottomShadow = Color.parseColor("#D0111111")

        val borderLayer = GradientDrawable().apply {
            setColor(bottomShadow)
            cornerRadius = 4f
        }

        val highlightLayer = GradientDrawable().apply {
            setColor(topHighlight)
            cornerRadius = 4f
        }

        val faceLayer = GradientDrawable().apply {
            setColor(baseColor)
            cornerRadius = 2f
        }

        val layers = arrayOf(borderLayer, highlightLayer, faceLayer)
        return LayerDrawable(layers).apply {
            setLayerInset(1, 2, 2, 2, 2)
            setLayerInset(2, 4, 4, 4, 4)
        }
    }
}
