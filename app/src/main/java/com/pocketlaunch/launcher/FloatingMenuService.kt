package com.pocketlaunch.launcher

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.io.File

class FloatingMenuService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: LinearLayout
    private lateinit var menuPanel: LinearLayout
    
    private lateinit var mainMenuContent: LinearLayout
    private lateinit var packChangerContent: LinearLayout
    private lateinit var packListContainer: LinearLayout

    private var isMenuOpen = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        floatingView = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val floatingIcon = Button(this).apply {
            text = "INK"
            setTextColor(Color.WHITE)
            textSize = 14f
            paint.isFakeBoldText = true
            setShadowLayer(4f, 3f, 3f, Color.BLACK) 
            background = StateListDrawable().apply {
                addState(intArrayOf(android.R.attr.state_pressed), mcpeButtonDrawable("#4A0E17", "#1A0508")) 
                addState(intArrayOf(), mcpeButtonDrawable("#8C1D2F", "#3A0A13")) 
            }
            layoutParams = LinearLayout.LayoutParams(130, 130)
        }

        menuPanel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
            setPadding(30, 30, 30, 30)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#1C1C1C"))
                setStroke(5, Color.parseColor("#4A4A4A"))
            }
            layoutParams = LinearLayout.LayoutParams(550, 480).apply { setMargins(15, 0, 0, 0) }
        }

        // Module Menu View
        mainMenuContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        val menuTitle = TextView(this).apply {
            text = "INK MOD MENU"
            setTextColor(Color.parseColor("#FFFF55")) 
            textSize = 16f
            paint.isFakeBoldText = true
            gravity = Gravity.CENTER
            setShadowLayer(4f, 2f, 2f, Color.BLACK)
            setPadding(0, 0, 0, 20)
        }

        val packChangerBtn = createMcpeButton("Pack Changer").apply { setOnClickListener { showPackChangerView() } }
        val hitboxesBtn = createMcpeButton("Render Hitboxes: OFF")
        var hitboxesOn = false
        hitboxesBtn.setOnClickListener {
            hitboxesOn = !hitboxesOn
            hitboxesBtn.text = if (hitboxesOn) "Render Hitboxes: ON" else "Render Hitboxes: OFF"
        }
        val closeMenuBtn = createMcpeButton("Close Menu").apply { 
            setOnClickListener { 
                isMenuOpen = false
                menuPanel.visibility = View.GONE
            }
        }

        mainMenuContent.addView(menuTitle)
        mainMenuContent.addView(packChangerBtn)
        mainMenuContent.addView(hitboxesBtn)
        mainMenuContent.addView(closeMenuBtn)

        // Pack Changer View
        packChangerContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        val packTitle = TextView(this).apply {
            text = "SELECT ACTIVE PACK"
            setTextColor(Color.parseColor("#55FF55"))
            textSize = 15f
            paint.isFakeBoldText = true
            gravity = Gravity.CENTER
            setShadowLayer(4f, 2f, 2f, Color.BLACK)
            setPadding(0, 0, 0, 15)
        }

        val scrollView = ScrollView(this).apply { layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f) }
        packListContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        scrollView.addView(packListContainer)

        val backBtn = createMcpeButton("Back to Modules").apply { setOnClickListener { showMainModulesView() } }

        packChangerContent.addView(packTitle)
        packChangerContent.addView(scrollView)
        packChangerContent.addView(backBtn)

        menuPanel.addView(mainMenuContent)
        menuPanel.addView(packChangerContent)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START; x = 100; y = 100 }

        floatingIcon.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0; private var initialY = 0; private var initialTouchX = 0f; private var initialTouchY = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x; initialY = params.y; initialTouchX = event.rawX; initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(floatingView, params)
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (Math.abs(event.rawX - initialTouchX) < 10 && Math.abs(event.rawY - initialTouchY) < 10) {
                            isMenuOpen = !isMenuOpen
                            menuPanel.visibility = if (isMenuOpen) View.VISIBLE else View.GONE
                            if (isMenuOpen) showMainModulesView() 
                        }
                        return true
                    }
                }
                return false
            }
        })

        windowManager.addView(floatingView, params)
    }

    private fun showMainModulesView() {
        packChangerContent.visibility = View.GONE
        mainMenuContent.visibility = View.VISIBLE
    }

    private fun showPackChangerView() {
        mainMenuContent.visibility = View.GONE
        packChangerContent.visibility = View.VISIBLE
        packListContainer.removeAllViews()

        val defaultBtn = createMcpeButton("Default Texture Pack").apply {
            setOnClickListener { Toast.makeText(this@FloatingMenuService, "Textures reset to Vanilla", Toast.LENGTH_SHORT).show() }
        }
        packListContainer.addView(defaultBtn)

        val launcherDir = getExternalFilesDir(null)
        val packFiles = launcherDir?.listFiles { file -> file.isFile && file.name.endsWith(".zip", ignoreCase = true) }

        packFiles?.forEach { zipFile ->
            val fileBtn = createMcpeButton(zipFile.nameWithoutExtension).apply {
                setOnClickListener { Toast.makeText(this@FloatingMenuService, "Swapping to: ${zipFile.name}", Toast.LENGTH_SHORT).show() }
            }
            packListContainer.addView(fileBtn)
        }
        
        if (packFiles.isNullOrEmpty()) {
            packListContainer.addView(TextView(this).apply {
                text = "(No .zip packs imported yet)"
                setTextColor(Color.GRAY)
                textSize = 12f
                gravity = Gravity.CENTER
                setPadding(0, 30, 0, 0)
            })
        }
    }

    private fun createMcpeButton(buttonText: String): Button {
        return Button(this).apply {
            text = buttonText
            setTextColor(Color.WHITE)
            textSize = 13f
            paint.isFakeBoldText = true
            isAllCaps = false
            setShadowLayer(4f, 3f, 3f, Color.BLACK)
            background = StateListDrawable().apply {
                addState(intArrayOf(android.R.attr.state_pressed), mcpeButtonDrawable("#3A3A3A", "#151515"))
                addState(intArrayOf(), mcpeButtonDrawable("#5A5A5A", "#262626"))
            }
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100).apply { setMargins(0, 6, 0, 6) }
        }
    }

    private fun mcpeButtonDrawable(fillColor: String, bottomShadowColor: String): GradientDrawable {
        return GradientDrawable().apply {
            setColor(Color.parseColor(fillColor))
            cornerRadius = 4f
            setStroke(5, Color.parseColor(bottomShadowColor))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingView.isInitialized) windowManager.removeView(floatingView)
    }
}
