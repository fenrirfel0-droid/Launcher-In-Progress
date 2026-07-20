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

    // Pure Black & White Levi-Style Theme System
    private val bgDark = "#050505"      // Deepest black background
    private val cardDark = "#111111"    // Slightly elevated black for cards
    private val textWhite = "#FFFFFF"   // Pure white text
    private val textGray = "#888888"    // Subtitle gray
    private val borderGray = "#222222"  // Subtle card borders
    
    // Core Layouts
    private lateinit var contentContainer: FrameLayout
    private lateinit var launchView: ScrollView
    private lateinit var settingsView: ScrollView

    // Nav Tabs
    private lateinit var tabLaunch: LinearLayout
    private lateinit var tabSettings: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(bgDark))
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        // --- TOP NAVIGATION BAR ---
        val topNav = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(50, 30, 50, 30)
            gravity = Gravity.CENTER_VERTICAL
            background = GradientDrawable().apply {
                setColor(Color.parseColor(bgDark))
                setStroke(1, Color.parseColor(borderGray))
            }
        }

        val appTitle = TextView(this@MainActivity).apply {
            text = "InkLauncher"
            textSize = 20f
            setTextColor(Color.parseColor(textWhite))
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        // Center Tabs
        val centerTabs = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2f)
        }

        tabLaunch = createNavTab("Launch", true)
        tabSettings = createNavTab("Settings", false)
        centerTabs.addView(tabLaunch)
        centerTabs.addView(tabSettings)

        // Top Right Instance Badge
        val rightBadge = TextView(this@MainActivity).apply {
            text = "Current Instance\nMinecraft_1.26"
            textSize = 12f
            setTextColor(Color.parseColor(textWhite))
            setPadding(40, 15, 40, 15)
            gravity = Gravity.CENTER
            background = GradientDrawable().apply {
                setColor(Color.parseColor(cardDark))
                setStroke(1, Color.parseColor(borderGray))
                cornerRadius = 100f
            }
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        topNav.addView(appTitle)
        topNav.addView(centerTabs)
        topNav.addView(rightBadge)

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

        // Tab Switching Logic
        tabLaunch.setOnClickListener { switchTab(0) }
        tabSettings.setOnClickListener { switchTab(1) }
    }

    // --- MAIN DASHBOARD (LEVI UI RECREATION) ---
    private fun buildLaunchDashboard(): ScrollView {
        val scroller = ScrollView(this@MainActivity).apply { 
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT) 
        }
        val mainLayout = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 40, 60, 40)
        }

        // Header Title
        mainLayout.addView(TextView(this@MainActivity).apply {
            text = "Minecraft"
            textSize = 34f
            setTextColor(Color.parseColor(textWhite))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 5)
        })
        mainLayout.addView(TextView(this@MainActivity).apply {
            text = "Version Isolation Sandbox"
            textSize = 14f
            setTextColor(Color.parseColor(textGray))
            setPadding(0, 0, 0, 40)
        })

        // Grid Split (Left and Right Columns)
        val splitGrid = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.HORIZONTAL }

        // Left Column (Content Management)
        val leftCol = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.2f)
            setPadding(0, 0, 20, 0)
        }

        val manageModsBtn = Button(this@MainActivity).apply {
            text = "Manage Mods"
            setTextColor(Color.parseColor(textWhite))
            isAllCaps = false
            textSize = 12f
            background = GradientDrawable().apply {
                setColor(Color.parseColor(borderGray))
                cornerRadius = 12f
            }
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 90).apply { setMargins(0, 0, 0, 20) }
        }

        val contentCard = createCleanCard("Content Management")
        contentCard.addView(createIconRow(android.R.drawable.ic_menu_gallery, "Worlds", "2 >"))
        contentCard.addView(createIconRow(android.R.drawable.ic_menu_view, "Resource Packs", "27 >"))
        contentCard.addView(createIconRow(android.R.drawable.ic_menu_manage, "Behavior Packs", "14 >"))

        leftCol.addView(manageModsBtn)
        leftCol.addView(contentCard)

        // Right Column (Miscellaneous & Launch)
        val rightCol = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            setPadding(20, 0, 0, 0)
        }

        val utilCard = createCleanCard("Miscellaneous")
        utilCard.addView(createIconRow(android.R.drawable.ic_menu_compass, "CurseForge", "Open"))
        utilCard.addView(createIconRow(android.R.drawable.ic_menu_myplaces, "Microsoft Accounts", "Signed In"))
        utilCard.addView(createIconRow(android.R.drawable.ic_menu_send, "Quick Launch", "Ready"))

        val launchGameBtn = Button(this@MainActivity).apply {
            text = "LAUNCH GAME"
            setTextColor(Color.parseColor(bgDark))
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 140).apply { setMargins(0, 20, 0, 0) }
            background = GradientDrawable().apply {
                setColor(Color.parseColor(textWhite))
                cornerRadius = 16f
            }
            setOnClickListener { triggerLaunch() }
        }

        rightCol.addView(utilCard)
        rightCol.addView(launchGameBtn)

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
            text = "Launcher Settings"
            textSize = 28f
            setTextColor(Color.parseColor(textWhite))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 40)
        })

        val sysCard = createCleanCard("System Configuration")
        sysCard.addView(createSettingToggle("Enable Overlay HUD (Levi Menu)", true))
        sysCard.addView(createSettingToggle("Native JNI Hooks (C++ Core)", false))
        
        layout.addView(sysCard)
        scroller.addView(layout)
        return scroller
    }

    // --- UI COMPONENT BUILDERS (Context-Safe) ---
    private fun createNavTab(title: String, isActive: Boolean): LinearLayout {
        return LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(30, 20, 30, 20)
            
            addView(TextView(this@MainActivity).apply {
                text = title
                textSize = 15f
                setTextColor(Color.parseColor(if (isActive) textWhite else textGray))
                typeface = if (isActive) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                tag = "text" // Tag for easy retrieval during switching
            })
        }
    }

    private fun createCleanCard(title: String): LinearLayout {
        val card = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            background = GradientDrawable().apply {
                setColor(Color.parseColor(cardDark))
                setStroke(1, Color.parseColor(borderGray))
                cornerRadius = 24f
            }
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 30)
            }
        }
        
        val header = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, 30)
            
            addView(TextView(this@MainActivity).apply {
                text = title
                textSize = 15f
                setTextColor(Color.parseColor(textWhite))
                typeface = Typeface.DEFAULT_BOLD
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            })
        }
        card.addView(header)
        return card
    }

    private fun createIconRow(iconRes: Int, title: String, subtitle: String): LinearLayout {
        val row = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 25, 0, 25)
        }
        
        row.addView(ImageView(this@MainActivity).apply {
            setImageResource(iconRes)
            setColorFilter(Color.parseColor(textWhite))
            layoutParams = LinearLayout.LayoutParams(50, 50).apply { setMargins(0, 0, 30, 0) }
        })

        row.addView(TextView(this@MainActivity).apply {
            text = title
            setTextColor(Color.parseColor(textWhite))
            textSize = 14f
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })

        row.addView(TextView(this@MainActivity).apply {
            text = subtitle
            setTextColor(Color.parseColor(textGray))
            textSize = 12f
        })

        return row
    }

    private fun createSettingToggle(label: String, defaultState: Boolean): LinearLayout {
        return LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 25, 0, 25)
            gravity = Gravity.CENTER_VERTICAL
            
            addView(TextView(this@MainActivity).apply {
                text = label
                setTextColor(Color.parseColor(textWhite))
                textSize = 15f
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            })
            addView(Switch(this@MainActivity).apply { isChecked = defaultState })
        }
    }

    private fun switchTab(index: Int) {
        val launchText = tabLaunch.findViewWithTag<TextView>("text")
        val settingsText = tabSettings.findViewWithTag<TextView>("text")

        launchText.setTextColor(Color.parseColor(if (index == 0) textWhite else textGray))
        launchText.typeface = if (index == 0) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        
        settingsText.setTextColor(Color.parseColor(if (index == 1) textWhite else textGray))
        settingsText.typeface = if (index == 1) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        launchView.visibility = if (index == 0) View.VISIBLE else View.GONE
        settingsView.visibility = if (index == 1) View.VISIBLE else View.GONE
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
