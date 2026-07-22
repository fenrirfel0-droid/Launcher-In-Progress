package com.pocketlaunch.launcher.ui

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Reusable Glassmorphism card window supporting drag movement, header titles, and smooth close buttons.
 */
open class BaseWindowView(
    context: Context,
    val titleText: String,
    widthPx: Int = 700,
    heightPx: Int = 850
) : FrameLayout(context) {

    val headerBar: LinearLayout
    val titleTextView: TextView
    val contentContainer: LinearLayout
    val closeButton: TextView

    private var initialX = 0
    private var initialY = 0
    private var touchX = 0f
    private var touchY = 0f

    init {
        layoutParams = LayoutParams(widthPx, heightPx)
        background = InkTheme.createCardBackground(
            bgColor = InkTheme.bgPanel,
            borderColor = InkTheme.borderDark,
            borderWidthPx = 2,
            cornerRadiusPx = 28f
        )
        setPadding(24, 20, 24, 20)
        elevation = 20f

        val mainLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        // Header
        headerBar = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(16, 12, 16, 20)
        }

        titleTextView = TextView(context).apply {
            text = titleText
            setTextColor(InkTheme.textPrimary)
            textSize = 15f
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        closeButton = TextView(context).apply {
            text = "✕"
            setTextColor(InkTheme.textMuted)
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(16, 8, 16, 8)
            setOnClickListener { closeWindow() }
        }

        headerBar.addView(titleTextView)
        headerBar.addView(closeButton)
        mainLayout.addView(headerBar)

        // Scrollable Content Region
        contentContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        mainLayout.addView(contentContainer)

        addView(mainLayout)
        makeDraggable()
    }

    private fun makeDraggable() {
        headerBar.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchX = event.rawX
                    touchY = event.rawY
                    initialX = this.x.toInt()
                    initialY = this.y.toInt()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = (event.rawX - touchX).toInt()
                    val deltaY = (event.rawY - touchY).toInt()
                    this.x = (initialX + deltaX).toFloat()
                    this.y = (initialY + deltaY).toFloat()
                    true
                }
                else -> false
            }
        }
    }

    open fun openWindow() {
        UiAnimationUtils.scaleIn(this)
    }

    open fun closeWindow() {
        UiAnimationUtils.scaleOut(this)
    }
}
