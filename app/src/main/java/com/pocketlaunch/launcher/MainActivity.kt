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
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)

            // FORCE FULLSCREEN (Hides Battery, Clock, and Navigation Bar)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                window.insetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                window.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }

            // THE MAIN CONTAINER (Horizontal split)
            val rootLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setBackgroundColor(Color.parseColor("#000000")) // Pure Black
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            // --- LEFT PANEL (Navigation Menu) ---
            val sideMenu = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.parseColor("#0A0A0A")) // Very dark grey
                setPadding(40, 60, 40, 60)
                layoutParams = LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 1f
                )
            }
            
            val appTitle = TextView(this).apply {
                text = "INK\nLAUNCHER"
                textSize = 22f
                setTextColor(Color.WHITE)
                paint.isFakeBoldText = true
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 80)
            }
            
            val homeBtn = createMenuButton("Home", isSelected = true)
            val modsBtn = createMenuButton("Mods", isSelected = false)
            val settingsBtn = createMenuButton("Settings", isSelected = false)
            
            sideMenu.addView(appTitle)
            sideMenu.addView(homeBtn)
            sideMenu.addView(modsBtn)
            sideMenu.addView(settingsBtn)

            // --- RIGHT PANEL (Main Dashboard Content) ---
            val mainContent = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(80, 80, 80, 80)
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 2.5f
                )
            }

            val launchCard = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(60, 60, 60, 60)
                background = GradientDrawable().apply {
                    setColor(Color.parseColor("#121212"))
                    cornerRadius = 32f
                    setStroke(2, Color.parseColor("#333333"))
                }
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            statusText = TextView(this).apply {
                text = "Status: Idle - Engine Unlinked"
                textSize = 18f
                setTextColor(Color.parseColor("#888888"))
                setPadding(0, 0, 0, 60)
            }

            val btnLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }

            val autoDetectBtn = createActionButton("Auto-Detect", isPrimary = false).apply {
                layoutParams = LinearLayout.LayoutParams(0, 140, 1f).apply { setMargins(0, 0, 30, 0) }
                setOnClickListener { autoDetectMinecraft() }
            }

            val launchBtn = createActionButton("LAUNCH GAME", isPrimary = true).apply {
                layoutParams = LinearLayout.LayoutParams(0, 140, 1.5f)
                setOnClickListener { 
                    if (targetApkPath != null) bootCustomMinecraftEngine(File(targetApkPath!!))
                    else Toast.makeText(this@MainActivity, "Please auto-detect an engine first!", Toast.LENGTH_SHORT).show()
                }
            }
            
            btnLayout.addView(autoDetectBtn)
            btnLayout.addView(launchBtn)
            
            launchCard.addView(statusText)
            launchCard.addView(btnLayout)
            mainContent.addView(launchCard)

            rootLayout.addView(sideMenu)
            rootLayout.addView(mainContent)

            setContentView(rootLayout)

        } catch (t: Throwable) {
            // DIAGNOSTIC SCREEN: Captures initialization crashes instantly
            val sw = StringWriter()
            t.printStackTrace(PrintWriter(sw))
            val errorLayout = ScrollView(this).apply {
                setBackgroundColor(Color.WHITE)
                addView(TextView(this@MainActivity).apply {
                    text = "INK LAUNCHER ENGINE EXCEPTION REPORT:\n\n$sw"
                    setTextColor(Color.RED)
                    textSize = 14f
                    setPadding(40, 40, 40, 40)
                })
            }
            setContentView(errorLayout)
        }
    }

    private fun autoDetectMinecraft() {
        try {
            val pm = packageManager
            val appInfo = pm.getApplicationInfo("com.mojang.minecraftpe", 0)
            targetApkPath = appInfo.sourceDir
            statusText.text = "Status: READY - Engine Hooked"
            statusText.setTextColor(Color.WHITE)
            Toast.makeText(this, "Engine Found!", Toast.LENGTH_SHORT).show()
        } catch (e: PackageManager.NameNotFoundException) {
            statusText.text = "Status: ERROR - Minecraft not installed"
            statusText.setTextColor(Color.parseColor("#FF5555"))
            Toast.makeText(this, "Could not find Minecraft installed.", Toast.LENGTH_LONG).show()
        }
    }

    private fun createMenuButton(text: String, isSelected: Boolean): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 16f
            setTextColor(if (isSelected) Color.BLACK else Color.WHITE)
            gravity = Gravity.CENTER
            paint.isFakeBoldText = true
            setPadding(0, 30, 0, 30)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 20)
            }
            background = GradientDrawable().apply {
                setColor(if (isSelected) Color.WHITE else Color.TRANSPARENT)
                cornerRadius = 16f
            }
        }
    }

    private fun createActionButton(text: String, isPrimary: Boolean): Button {
        return Button(this).apply {
            this.text = text
            setTextColor(if (isPrimary) Color.BLACK else Color.WHITE)
            textSize = 16f
            paint.isFakeBoldText = true
            isAllCaps = false
            background = GradientDrawable().apply {
                setColor(if (isPrimary) Color.WHITE else Color.TRANSPARENT)
                if (!isPrimary) setStroke(3, Color.WHITE)
                cornerRadius = 24f
            }
        }
    }

    private fun bootCustomMinecraftEngine(apkFile: File) {
        Toast.makeText(this, "Injecting native payload...", Toast.LENGTH_SHORT).show()
    }
}
