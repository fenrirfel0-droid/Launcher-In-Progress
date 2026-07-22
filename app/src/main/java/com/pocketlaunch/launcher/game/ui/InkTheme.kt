package com.pocketlaunch.launcher.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable

/**
 * InkClient Unified Design Tokens - Void Aesthetics & Modern Glassmorphism
 */
object InkTheme {
    val bgVoid = Color.parseColor("#06070B")
    val bgPanel = Color.parseColor("#0E1017")
    val bgCard = Color.parseColor("#151824")
    val bgCardHover = Color.parseColor("#1D2133")

    val borderDark = Color.parseColor("#1F2438")
    val borderLight = Color.parseColor("#2E3552")

    val accentPrimary = Color.parseColor("#7C3AED") // Electric Purple
    val accentSecondary = Color.parseColor("#3B82F6") // Cyber Blue
    val accentSuccess = Color.parseColor("#10B981") // Mint Green
    val accentDanger = Color.parseColor("#EF4444") // Coral Red

    val textPrimary = Color.parseColor("#FFFFFF")
    val textSecondary = Color.parseColor("#94A3B8")
    val textMuted = Color.parseColor("#64748B")

    fun createCardBackground(
        bgColor: Int = bgCard,
        borderColor: Int = borderDark,
        borderWidthPx: Int = 2,
        cornerRadiusPx: Float = 24f
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(bgColor)
            setStroke(borderWidthPx, borderColor)
            cornerRadius = cornerRadiusPx
        }
    }

    fun createGradientButton(
        startColor: Int = accentPrimary,
        endColor: Int = accentSecondary,
        cornerRadiusPx: Float = 18f
    ): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(startColor, endColor)
        ).apply {
            cornerRadius = cornerRadiusPx
        }
    }
}
