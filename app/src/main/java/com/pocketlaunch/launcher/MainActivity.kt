package com.pocketlaunch.launcher

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private var targetApkPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 📜 Main ScrollView to allow dashboard scrolling
        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#050505")) // Deep Black Background
        }

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 80, 50, 80)
        }

        // 🏷️ Top Header: "Ink Launcher"
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(10, 0, 0, 60)
        }

        val titleText = TextView(this).apply {
            text = "Ink Launcher"
            textSize = 32f
            setTextColor(Color.WHITE)
            paint.isFakeBoldText = true 
        }
        headerLayout.addView(titleText)

        // 🗂️ Card 1: Engine Status
        val statusCard = createCardLayout()
        val statusTitle = createCardTitle("Engine Status")
        val statusText = TextView(this).apply {
            text = "Idle - No Engine Detected"
            setTextColor(Color.parseColor("#888888"))
            setPadding(0, 20, 0, 40)
        }
        val autoDetectButton = createStyledButton("Auto-Detect Minecraft", isOutline = true) {
            autoDetectMinecraft(statusText)
        }
        statusCard.addView(statusTitle)
        statusCard.addView(statusText)
        statusCard.addView(autoDetectButton)

        // 🚀 Card 2: Quick Launch
        val launchCard = createCardLayout()
        val launchTitle = createCardTitle("Quick Launch")
        val launchDesc = TextView(this).apply {
            text = "Inject C++ Engine and boot environment."
            setTextColor(Color.parseColor("#888888"))
            setPadding(0, 20, 0, 40)
        }
        val launchButton = createStyledButton("Launch Engine", isOutline = false) {
            if (targetApkPath != null) {
                bootCustomMinecraftEngine(File(targetApkPath!!))
            } else {
                Toast.makeText(this, "Please auto-detect an engine first!", Toast.LENGTH_LONG).show()
            }
        }
        launchCard.addView(launchTitle)
        launchCard.addView(launchDesc)
        launchCard.addView(launchButton)
        
        // 🧩 Card 3: Content Management (Mod Vibe Placeholder)
        val modsCard = createCardLayout()
        val modsTitle = createCardTitle("Content Management")
        val modsDesc = TextView(this).apply {
            text = "Resource Packs: 0\nBehavior Packs: 0"
            setTextColor(Color.parseColor("#888888"))
            setPadding(0, 20, 0, 40)
            setLineSpacing(10f, 1f)
        }
        val modsButton = createStyledButton("Manage Mods (Coming Soon)", isOutline = true) {
            Toast.makeText(this, "Mod management requires engine hook first.", Toast.LENGTH_SHORT).show()
        }
        modsCard.addView(modsTitle)
        modsCard.addView(modsDesc)
        modsCard.addView(modsButton)

        // Assemble the UI
        rootLayout.addView(headerLayout)
        rootLayout.addView(statusCard)
        rootLayout.addView(launchCard)
        rootLayout.addView(modsCard)

        scrollView.addView(rootLayout)
        setContentView(scrollView)
    }

    // 🔍 Auto-Detects Minecraft Installed on the Device
    private fun autoDetectMinecraft(statusTextView: TextView) {
        try {
            val pm = packageManager
            val appInfo = pm.getApplicationInfo("com.mojang.minecraftpe", 0)
            targetApkPath = appInfo.sourceDir

            statusTextView.text = "Ready - Detected: com.mojang.minecraftpe"
            statusTextView.setTextColor(Color.WHITE)
            Toast.makeText(this, "Engine Found!", Toast.LENGTH_SHORT).show()
        } catch (e: PackageManager.NameNotFoundException) {
            statusTextView.text = "Error - Minecraft not installed on device."
            statusTextView.setTextColor(Color.WHITE) 
            Toast.makeText(this, "Could not find Minecraft installed.", Toast.LENGTH_LONG).show()
        }
    }

    // --- 🛠️ UI Component Builders ---

    // Creates the dark, rounded background cards
    private fun createCardLayout(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 60, 60, 60)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 40)
            }
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#121212")) // Dark grey card background
                cornerRadius = 32f
                setStroke(2, Color.parseColor("#2A2A2A")) // Subtle border
            }
        }
    }

    private fun createCardTitle(title: String): TextView {
        return TextView(this).apply {
            text = title
            textSize = 20f
            setTextColor(Color.WHITE)
            paint.isFakeBoldText = true
        }
    }

    // Creates either a solid white button, or a black button with a white outline
    private fun createStyledButton(text: String, isOutline: Boolean = false, onClick: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            setTextColor(if (isOutline) Color.WHITE else Color.BLACK)
            isAllCaps = false
            textSize = 15f
            paint.isFakeBoldText = true

            background = GradientDrawable().apply {
                if (isOutline) {
                    setColor(Color.TRANSPARENT)
                    setStroke(4, Color.WHITE)
                } else {
                    setColor(Color.WHITE)
                }
                cornerRadius = 20f
            }

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                140
            )
            setOnClickListener { onClick() }
        }
    }

    private fun bootCustomMinecraftEngine(apkFile: File) {
        Toast.makeText(this, "Warning: C++ Native Libraries not yet mapped.", Toast.LENGTH_SHORT).show()
    }
}
