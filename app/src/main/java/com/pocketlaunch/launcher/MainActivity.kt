package com.pocketlaunch.launcher

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request deep storage permission for external Minecraft mods
        checkStoragePermissions()

        // Create a rock-solid UI dynamically without needing buggy Compose layers
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(50, 50, 50, 50)
        }

        val titleText = TextView(this).apply {
            text = "Custom MCPE Loader"
            textSize = 24f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 80)
        }

        val importButton = Button(this).apply {
            text = "Import Unofficial APK / Shaders"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 20, 0, 20)
            }
            setOnClickListener {
                Toast.makeText(this@MainActivity, "Open File Picker...", Toast.LENGTH_SHORT).show()
            }
        }

        val launchButton = Button(this).apply {
            text = "Launch Engine Environment"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 20, 0, 20)
            }
            setOnClickListener {
                bootMinecraftEngine()
            }
        }

        rootLayout.addView(titleText)
        rootLayout.addView(importButton)
        rootLayout.addView(launchButton)

        setContentView(rootLayout)
    }

    private fun checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                runCatching {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                        data = Uri.parse("package:$packageName")
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun bootMinecraftEngine() {
        val launchIntent = packageManager.getLaunchIntentForPackage("com.mojang.minecraftpe")
        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            Toast.makeText(this, "Minecraft PE installation not detected!", Toast.LENGTH_SHORT).show()
        }
    }
}
