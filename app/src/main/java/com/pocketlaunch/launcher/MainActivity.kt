package com.pocketlaunch.launcher

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private var targetApkPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🎨 Cleaner, Smoother, Advanced UI
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#0F0F13")) // Sleeker, darker background
            setPadding(80, 80, 80, 80)
        }

        val titleText = TextView(this).apply {
            text = "POCKET LAUNCH\nPRO"
            textSize = 32f
            setTextColor(Color.parseColor("#00FFA6")) // Cyan-Green glow
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 20)
        }

        val subtitleText = TextView(this).apply {
            text = "Advanced Engine Injector"
            textSize = 14f
            setTextColor(Color.parseColor("#888888"))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 100)
        }

        val statusText = TextView(this).apply {
            text = "Status: Idle\nNo Engine Detected"
            textSize = 14f
            setTextColor(Color.parseColor("#FF5555")) // Red until loaded
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 60)
        }

        val autoDetectButton = createStyledButton("Auto-Detect Installed Minecraft", "#2A2A35") {
            autoDetectMinecraft(statusText)
        }

        val launchButton = createStyledButton("Inject & Launch Engine", "#00FFA6", isPrimary = true) {
            if (targetApkPath != null) {
                bootCustomMinecraftEngine(File(targetApkPath!!))
            } else {
                Toast.makeText(this, "Please auto-detect an engine first!", Toast.LENGTH_LONG).show()
            }
        }

        rootLayout.addView(titleText)
        rootLayout.addView(subtitleText)
        rootLayout.addView(statusText)
        rootLayout.addView(autoDetectButton)
        rootLayout.addView(launchButton)

        setContentView(rootLayout)
    }

    // 🔍 Auto-Detects Minecraft Installed on the Device
    private fun autoDetectMinecraft(statusTextView: TextView) {
        try {
            val pm = packageManager
            // Target the default Bedrock package name
            val appInfo = pm.getApplicationInfo("com.mojang.minecraftpe", 0)
            targetApkPath = appInfo.sourceDir // Grabs the base APK path

            statusTextView.text = "Status: Ready\nDetected: com.mojang.minecraftpe"
            statusTextView.setTextColor(Color.parseColor("#00FFA6"))
            
            Toast.makeText(this, "Engine Found!", Toast.LENGTH_SHORT).show()

        } catch (e: PackageManager.NameNotFoundException) {
            statusTextView.text = "Status: Error\nMinecraft not installed on device."
            statusTextView.setTextColor(Color.parseColor("#FF5555"))
            Toast.makeText(this, "Could not find Minecraft installed on your system.", Toast.LENGTH_LONG).show()
        }
    }

    // ⚙️ UI Component Builder (FIXED: Now properly accepts the onClick function)
    private fun createStyledButton(text: String, colorHex: String, isPrimary: Boolean = false, onClick: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            setTextColor(if (isPrimary) Color.BLACK else Color.WHITE)
            isAllCaps = false
            textSize = 16f
            
            background = GradientDrawable().apply {
                setColor(Color.parseColor(colorHex))
                cornerRadius = 32f // Smoother, rounder edges
            }
            
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                150
            ).apply {
                setMargins(0, 20, 0, 20)
            }
            // Properly binds the click event
            setOnClickListener { onClick() }
        }
    }

    private fun bootCustomMinecraftEngine(apkFile: File) {
        Toast.makeText(this, "Warning: C++ Native Libraries not yet mapped.", Toast.LENGTH_SHORT).show()
        
        // We will build the native .so extractor here in the next step
    }
}
