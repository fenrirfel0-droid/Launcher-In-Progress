package com.pocketlaunch.launcher

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class MainActivity : AppCompatActivity() {

    private var targetApkPath: String? = null
    
    // UI Elements
    private lateinit var statusText: TextView
    private lateinit var homeBtn: TextView
    private lateinit var modsBtn: TextView
    private lateinit var settingsBtn: TextView
    
    // Screens
    private lateinit var homeLayout: LinearLayout
    private lateinit var modsLayout: LinearLayout
    private lateinit var settingsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)

            val rootLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setBackgroundColor(Color.parseColor("#000000")) 
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }

            // --- LEFT PANEL (Navigation Menu) ---
            val sideMenu = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.parseColor("#0A0A0A"))
                setPadding(40, 60, 40, 60)
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            }
            
            val appTitle = TextView(this).apply {
                text = "INK\nLAUNCHER"
                textSize = 22f
                setTextColor(Color.WHITE)
                paint.isFakeBoldText = true
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 80)
            }
            
            // Create Tabs
            homeBtn = createMenuButton("Home")
            modsBtn = createMenuButton("Mods")
            settingsBtn = createMenuButton("Settings")
            
            // Wire up Tab Clicks
            homeBtn.setOnClickListener { switchTab(0) }
            modsBtn.setOnClickListener { switchTab(1) }
            settingsBtn.setOnClickListener { switchTab(2) }
            
            sideMenu.addView(appTitle)
            sideMenu.addView(homeBtn)
            sideMenu.addView(modsBtn)
            sideMenu.addView(settingsBtn)

            // --- RIGHT PANEL (Main Container) ---
            val mainContent = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(80, 80, 80, 80)
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2.5f)
            }

            // Create the 3 different screens
            homeLayout = buildHomeLayout()
            modsLayout = buildPlaceholderScreen("Mods & Scripts Dashboard\n(Coming Soon)")
            settingsLayout = buildPlaceholderScreen("Launcher Settings\n(Coming Soon)")

            mainContent.addView(homeLayout)
            mainContent.addView(modsLayout)
            mainContent.addView(settingsLayout)

            rootLayout.addView(sideMenu)
            rootLayout.addView(mainContent)

            // Set layout and hide System UI
            setContentView(rootLayout)
            enforceFullscreen()

            // Initialize app state
            switchTab(0)
            autoDetectMinecraft() // Trigger scan instantly on launch

        } catch (t: Throwable) {
            val sw = StringWriter()
            t.printStackTrace(PrintWriter(sw))
            setContentView(ScrollView(this).apply {
                setBackgroundColor(Color.WHITE)
                addView(TextView(this@MainActivity).apply {
                    text = "CRASH LOG:\n\n$sw"
                    setTextColor(Color.RED)
                    setPadding(40, 40, 40, 40)
                })
            })
        }
    }

    private fun buildHomeLayout(): LinearLayout {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val launchCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 60, 60, 60)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#121212"))
                cornerRadius = 32f
                setStroke(2, Color.parseColor("#333333"))
            }
        }

        statusText = TextView(this).apply {
            text = "Status: Scanning for game engine..."
            textSize = 18f
            setTextColor(Color.parseColor("#888888"))
            setPadding(0, 0, 0, 60)
        }

        val launchBtn = Button(this).apply {
            text = "LAUNCH GAME"
            setTextColor(Color.BLACK)
            textSize = 16f
            paint.isFakeBoldText = true
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 140)
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = 24f
            }
            setOnClickListener { 
                if (targetApkPath != null) bootCustomMinecraftEngine(File(targetApkPath!!))
                else Toast.makeText(this@MainActivity, "Minecraft not found on this device!", Toast.LENGTH_SHORT).show()
            }
        }

        launchCard.addView(statusText)
        launchCard.addView(launchBtn)
        layout.addView(launchCard)
        return layout
    }

    private fun buildPlaceholderScreen(title: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            
            addView(TextView(this@MainActivity).apply {
                text = title
                textSize = 24f
                setTextColor(Color.parseColor("#555555"))
                gravity = Gravity.CENTER
            })
        }
    }

    private fun switchTab(tabIndex: Int) {
        // 1. Reset all buttons to unselected
        setTabVisuals(homeBtn, false)
        setTabVisuals(modsBtn, false)
        setTabVisuals(settingsBtn, false)

        // 2. Hide all screens
        homeLayout.visibility = View.GONE
        modsLayout.visibility = View.GONE
        settingsLayout.visibility = View.GONE

        // 3. Highlight the selected button and show its screen
        when (tabIndex) {
            0 -> {
                setTabVisuals(homeBtn, true)
                homeLayout.visibility = View.VISIBLE
            }
            1 -> {
                setTabVisuals(modsBtn, true)
                modsLayout.visibility = View.VISIBLE
            }
            2 -> {
                setTabVisuals(settingsBtn, true)
                settingsLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun setTabVisuals(button: TextView, isSelected: Boolean) {
        button.setTextColor(if (isSelected) Color.BLACK else Color.WHITE)
        button.background = GradientDrawable().apply {
            setColor(if (isSelected) Color.WHITE else Color.TRANSPARENT)
            cornerRadius = 16f
        }
    }

    private fun createMenuButton(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 16f
            gravity = Gravity.CENTER
            paint.isFakeBoldText = true
            setPadding(0, 30, 0, 30)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 20)
            }
        }
    }

    private fun autoDetectMinecraft() {
        try {
            val pm = packageManager
            val appInfo = pm.getApplicationInfo("com.mojang.minecraftpe", 0)
            targetApkPath = appInfo.sourceDir
            statusText.text = "Status: READY - Minecraft Hooked"
            statusText.setTextColor(Color.parseColor("#44FF44")) // Neon Green
        } catch (e: PackageManager.NameNotFoundException) {
            statusText.text = "Status: ERROR - Minecraft not installed"
            statusText.setTextColor(Color.parseColor("#FF5555")) // Red
        }
    }

    private fun enforceFullscreen() {
        window.decorView.post {
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    window.insetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    window.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                } else {
                    @Suppress("DEPRECATION")
                    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                }
            } catch (e: Exception) {}
        }
    }

    private fun bootCustomMinecraftEngine(apkFile: File) {
        Toast.makeText(this, "Injecting Native Launcher Payload...", Toast.LENGTH_SHORT).show()
    }
}
