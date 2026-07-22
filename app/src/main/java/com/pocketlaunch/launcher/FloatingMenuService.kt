package com.pocketlaunch.launcher

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import kotlin.math.abs

class FloatingMenuService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var rootOverlayView: FrameLayout
    private lateinit var floatingIcon: TextView
    private lateinit var expandedMenu: LinearLayout
    private var isMenuOpen = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        rootOverlayView = FrameLayout(this@FloatingMenuService).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        floatingIcon = TextView(this@FloatingMenuService).apply {
            text = "Ink"
            setTextColor(Color.WHITE)
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#000000"))
                setStroke(2, Color.parseColor("#222222"))
                cornerRadius = 100f
            }
            layoutParams = FrameLayout.LayoutParams(130, 130)
        }

        expandedMenu = LinearLayout(this@FloatingMenuService).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
            setPadding(40, 40, 40, 40)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#111111"))
                setStroke(2, Color.parseColor("#222222"))
                cornerRadius = 24f
            }
            layoutParams = FrameLayout.LayoutParams(550, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                setMargins(20, 150, 0, 0)
            }
        }

        expandedMenu.addView(TextView(this@FloatingMenuService).apply {
            text = "MODULES"
            setTextColor(Color.parseColor("#888888"))
            textSize = 12f
            setPadding(0, 0, 0, 20)
        })

        expandedMenu.addView(createModuleToggle("Hitbox Expander"))
        expandedMenu.addView(createModuleToggle("No Render"))
        expandedMenu.addView(createModuleToggle("ESP"))

        val closeBtn = TextView(this@FloatingMenuService).apply {
            text = "Close Engine"
            setTextColor(Color.parseColor("#FF4444"))
            gravity = Gravity.CENTER
            setPadding(0, 30, 0, 10)
            setOnClickListener { stopSelf() }
        }
        expandedMenu.addView(closeBtn)

        rootOverlayView.addView(expandedMenu)
        rootOverlayView.addView(floatingIcon)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        floatingIcon.setOnTouchListener(object : View.OnTouchListener {
            private var initX = 0; private var initY = 0; private var initTouchX = 0f; private var initTouchY = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initX = params.x; initY = params.y; initTouchX = event.rawX; initTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initX + (event.rawX - initTouchX).toInt()
                        params.y = initY + (event.rawY - initTouchY).toInt()
                        windowManager.updateViewLayout(rootOverlayView, params)
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (abs(event.rawX - initTouchX) < 15 && abs(event.rawY - initTouchY) < 15) {
                            isMenuOpen = !isMenuOpen
                            expandedMenu.visibility = if (isMenuOpen) View.VISIBLE else View.GONE
                        }
                        return true
                    }
                }
                return false
            }
        })

        windowManager.addView(rootOverlayView, params)
    }

    private fun createModuleToggle(name: String): LinearLayout {
        return LinearLayout(this@FloatingMenuService).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 20, 0, 20)
            gravity = Gravity.CENTER_VERTICAL
            addView(TextView(this@FloatingMenuService).apply {
                text = name
                setTextColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            })
            addView(Switch(this@FloatingMenuService))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::rootOverlayView.isInitialized) windowManager.removeView(rootOverlayView)
    }
}
