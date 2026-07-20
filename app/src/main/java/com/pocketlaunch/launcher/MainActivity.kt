package com.pocketlaunch.launcher

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Pure Black & White Theme System
    private val bgDark = "#000000" // Pure black
    private val cardDark = "#111111" // Slightly elevated black
    private val textWhite = "#FFFFFF"
    private val textGray = "#888888" // Softer gray for subtitles
    private val borderGray = "#222222" // Very subtle borders

    private lateinit var contentContainer: FrameLayout
    private lateinit var launchView: ScrollView
    private lateinit var settingsView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(bgDark))
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        // --- TOP HEADER ---
        val topNav = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(60, 40, 60, 40)
            gravity = Gravity.CENTER_VERTICAL
            background = GradientDrawable().apply {
                setColor(Color.parseColor(bgDark))
                setStroke(1, Color.parseColor(borderGray))
            }
        }

        val appTitle = TextView(this@MainActivity).apply {
            text = "InkLauncher" 
            textSize = 22f
            setTextColor(Color.parseColor(textWhite))
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        val btnLaunchTab = createNavText("Dashboard", true)
        val btnSettingsTab = createNavText("Settings", false)

        topNav.addView(appTitle)
        topNav.addView(btnLaunchTab)
        topNav.addView(btnSettingsTab)

        // --- CONTENT FRAME ---
        contentContainer = FrameLayout(this@MainActivity).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
        }

        launchView = buildLaunchDashboard()
        settingsView = buildSettingsDashboard()

        contentContainer.addView(launchView)
        contentContainer.addView(settingsView)

        rootLayout.addView(topNav)
        rootLayout.addView(contentContainer)

        setContentView(rootLayout)
        enforceFullscreen()
        
        // Tab switching logic
        btnLaunchTab.setOnClickListener {
            launchView.visibility = View.VISIBLE
            settingsView.visibility = View.GONE
            btnLaunchTab.setTextColor(Color.parseColor(textWhite))
            btnSettingsTab.setTextColor(Color.parseColor(textGray))
        }
        btnSettingsTab.setOnClickListener {
            launchView.visibility = View.GONE
            settingsView.visibility = View.VISIBLE
            btnLaunchTab.setTextColor(Color.parseColor(textGray))
            btnSettingsTab.setTextColor(Color.parseColor(textWhite))
        }
    }

    // --- MAIN DASHBOARD (CLEAN UI) ---
    private fun buildLaunchDashboard(): ScrollView {
        val scroller = ScrollView(this@MainActivity).apply { layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT) }
        val mainLayout = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 50, 60, 50)
        }

        val header = TextView(this@MainActivity).apply {
            text = "Minecraft"
            textSize = 36f
            setTextColor(Color.parseColor(textWhite))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 40)
        }
        mainLayout.addView(header)

        val splitGrid = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.HORIZONTAL }

        val leftCol = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.2f)
            setPadding(0, 0, 30, 0)
        }

        val contentCard = createCleanCard("Modifications")
        contentCard.addView(createIconRow(android.R.drawable.ic_menu_gallery, "Resource Packs", "Manage visual assets"))
        contentCard.addView(createIconRow(android.R.drawable.ic_menu_manage, "Modules (.so)", "Native dynamic libraries"))
        
        val utilCard = createCleanCard("Utilities")
        utilCard.addView(createIconRow(android.R.drawable.ic_menu_compass, "CurseForge DB", "Browse repositories"))
        utilCard.addView(createIconRow(android.R.drawable.ic_menu_mylocation, "Quick Launch", "Bypass verification"))

        leftCol.addView(contentCard)
        leftCol.addView(utilCard)

        val rightCol = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            gravity = Gravity.BOTTOM
        }

        val bigLaunchBtn = Button(this@MainActivity).apply {
            text = "LAUNCH"
            setTextColor(Color.parseColor(bgDark))
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 160)
            background = GradientDrawable().apply {
                setColor(Color.parseColor(textWhite))
                cornerRadius = 12f 
            }
            setOnClickListener { triggerLaunch() }
        }

        rightCol.addView(bigLaunchBtn)
        splitGrid.addView(leftCol)
        splitGrid.addView(rightCol)
        mainLayout.addView(splitGrid)

        scroller.addView(mainLayout)
        return scroller
    }

    private fun buildSettingsDashboard(): ScrollView {
        val scroller = ScrollView(this@MainActivity).apply { visibility = View.GONE }
        val layout = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.VERTICAL; setPadding(60, 50, 60, 50) }
        
        layout.addView(TextView(this@MainActivity).apply {
            text = "Engine Settings"
            textSize = 28f
            setTextColor(Color.parseColor(textWhite))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 40)
        })

        layout.addView(createCleanCard("System").apply {
            addView(createSettingToggle("Enable Overlay HUD", true))
            addView(createSettingToggle("Force JNI Hooks", false))
        })

        scroller.addView(layout)
        return scroller
    }

    // --- UI HELPERS (Context FIXED here) ---
    private fun createNavText(textStr: String, isActive: Boolean): TextView {
        return TextView(this@MainActivity).apply {
            text = textStr
            textSize = 16f
            setTextColor(Color.parseColor(if (isActive) textWhite else textGray))
            setPadding(40, 0, 40, 0)
        }
    }

    private fun createCleanCard(title: String): LinearLayout {
        val card = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            background = GradientDrawable().apply {
                setColor(Color.parseColor(cardDark))
                setStroke(1, Color.parseColor(borderGray))
                cornerRadius = 16f
            }
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 30)
            }
        }
        card.addView(TextView(this@MainActivity).apply {
            text = title
            textSize = 14f
            setTextColor(Color.parseColor(textGray))
            setPadding(0, 0, 0, 30)
            isAllCaps = true
        })
        return card
    }

    private fun createIconRow(iconRes: Int, title: String, subtitle: String): LinearLayout {
        val row = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 20, 0, 20)
        }
        
        val icon = ImageView(this@MainActivity).apply {
            setImageResource(iconRes)
            setColorFilter(Color.parseColor(textWhite))
            layoutParams = LinearLayout.LayoutParams(60, 60).apply { setMargins(0, 0, 30, 0) }
        }

        val textBlock = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.VERTICAL }
        textBlock.addView(TextView(this@MainActivity).apply {
            text = title
            setTextColor(Color.parseColor(textWhite))
            textSize = 16f
        })
        textBlock.addView(TextView(this@MainActivity).apply {
            text = subtitle
            setTextColor(Color.parseColor(textGray))
            textSize = 12f
        })

        row.addView(icon)
        row.addView(textBlock)
        return row
    }

    private fun createSettingToggle(label: String, defaultState: Boolean): LinearLayout {
        val row = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 20, 0, 20)
            gravity = Gravity.CENTER_VERTICAL
        }
            
        row.addView(TextView(this@MainActivity).apply {
            text = label
            setTextColor(Color.parseColor(textWhite))
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })
        row.addView(Switch(this@MainActivity).apply { isChecked = defaultState })
        return row
    }

    private fun triggerLaunch() {
        if (!Settings.canDrawOverlays(this@MainActivity)) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, android.net.Uri.parse("package:$packageName")))
        } else {
            startService(Intent(this@MainActivity, FloatingMenuService::class.java))
            packageManager.getLaunchIntentForPackage("com.mojang.minecraftpe")?.let { startActivity(it) }
        }
    }

    private fun enforceFullscreen() {
        try {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        } catch (e: Exception) {}
    }
}
