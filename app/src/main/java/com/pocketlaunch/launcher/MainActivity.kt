package com.pocketlaunch.launcher

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.StringWriter

class MainActivity : AppCompatActivity() {

    private var targetApkPath: String? = null
    
    private lateinit var homeBtn: TextView
    private lateinit var modsBtn: TextView
    private lateinit var settingsBtn: TextView
    
    private lateinit var homeLayout: LinearLayout
    private lateinit var modsLayout: LinearLayout
    private lateinit var settingsLayout: LinearLayout

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { handleSelectedFile(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            autoDetectMinecraft()

            val rootLayout = LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                setBackgroundColor(Color.parseColor("#000000")) 
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }

            val sideMenu = LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.parseColor("#0A0A0A"))
                setPadding(40, 60, 40, 60)
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            }
            
            val appTitle = TextView(this@MainActivity).apply {
                text = "INK\nLAUNCHER"
                textSize = 22f
                setTextColor(Color.WHITE)
                paint.isFakeBoldText = true
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 80)
            }
            
            homeBtn = createMenuButton("Home")
            modsBtn = createMenuButton("Mods")
            settingsBtn = createMenuButton("Settings")
            
            homeBtn.setOnClickListener { switchTab(0) }
            modsBtn.setOnClickListener { switchTab(1) }
            settingsBtn.setOnClickListener { switchTab(2) }
            
            sideMenu.addView(appTitle)
            sideMenu.addView(homeBtn)
            sideMenu.addView(modsBtn)
            sideMenu.addView(settingsBtn)

            val mainContent = LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(80, 80, 80, 80)
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2.5f)
            }

            homeLayout = buildHomeLayout()
            modsLayout = buildModsLayout()
            settingsLayout = buildPlaceholderScreen("Launcher Settings\n(Coming Soon)")

            mainContent.addView(homeLayout)
            mainContent.addView(modsLayout)
            mainContent.addView(settingsLayout)

            rootLayout.addView(sideMenu)
            rootLayout.addView(mainContent)

            setContentView(rootLayout)
            enforceFullscreenSafely()
            switchTab(0)

        } catch (t: Throwable) {
            val sw = StringWriter()
            t.printStackTrace(PrintWriter(sw))
            setContentView(ScrollView(this@MainActivity).apply {
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
        return LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            val launchBtn = Button(this@MainActivity).apply {
                text = "LAUNCH GAME & INJECT MENU"
                setTextColor(Color.BLACK)
                textSize = 18f
                paint.isFakeBoldText = true
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 160)
                background = GradientDrawable().apply {
                    setColor(Color.WHITE)
                    cornerRadius = 24f
                }
                setOnClickListener { 
                    if (targetApkPath != null) {
                        if (!Settings.canDrawOverlays(this@MainActivity)) {
                            Toast.makeText(this@MainActivity, "Allow 'Display over other apps' to use the Mod Menu!", Toast.LENGTH_LONG).show()
                            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
                        } else {
                            startService(Intent(this@MainActivity, FloatingMenuService::class.java))
                            val launchIntent = packageManager.getLaunchIntentForPackage("com.mojang.minecraftpe")
                            if (launchIntent != null) startActivity(launchIntent)
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "Minecraft not found on device!", Toast.LENGTH_LONG).show()
                    }
                }
            }
            addView(launchBtn)
        }
    }

    private fun buildModsLayout(): LinearLayout {
        return LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            
            val importBtn = Button(this@MainActivity).apply {
                text = "IMPORT PLUGIN (.so / .zip)"
                setTextColor(Color.WHITE)
                textSize = 16f
                paint.isFakeBoldText = true
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 140)
                background = GradientDrawable().apply {
                    setColor(Color.parseColor("#1A1A1A"))
                    setStroke(3, Color.parseColor("#444444"))
                    cornerRadius = 24f
                }
                setOnClickListener { filePickerLauncher.launch("*/*") }
            }
            addView(importBtn)
        }
    }

    private fun buildPlaceholderScreen(title: String): LinearLayout {
        return LinearLayout(this@MainActivity).apply {
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

    private fun handleSelectedFile(uri: Uri) {
        val fileName = getFileName(uri) ?: "Unknown File"
        if (fileName.endsWith(".so", ignoreCase = true) || fileName.endsWith(".zip", ignoreCase = true)) {
            showImportDialog(fileName, uri)
        } else {
            Toast.makeText(this, "Invalid file! Only .so and .zip allowed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImportDialog(fileName: String, uri: Uri) {
        val dialog = Dialog(this@MainActivity).apply { window?.setBackgroundDrawableResource(android.R.color.transparent) }
        val container = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 60, 60, 60)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#121212"))
                cornerRadius = 32f
                setStroke(2, Color.parseColor("#333333"))
            }
            layoutParams = ViewGroup.LayoutParams(600, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val title = TextView(this@MainActivity).apply {
            text = "Import Plugin?"
            textSize = 20f
            setTextColor(Color.WHITE)
            paint.isFakeBoldText = true
            setPadding(0, 0, 0, 20)
        }

        val fileText = TextView(this@MainActivity).apply {
            text = fileName
            textSize = 14f
            setTextColor(Color.parseColor("#888888"))
            setPadding(0, 0, 0, 60)
        }

        val btnLayout = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.HORIZONTAL }
        val cancelBtn = Button(this@MainActivity).apply {
            text = "Cancel"
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply { setColor(Color.TRANSPARENT) }
            layoutParams = LinearLayout.LayoutParams(0, 120, 1f)
            setOnClickListener { dialog.dismiss() }
        }

        val confirmBtn = Button(this@MainActivity).apply {
            text = "Import"
            setTextColor(Color.BLACK)
            background = GradientDrawable().apply { setColor(Color.WHITE); cornerRadius = 16f }
            layoutParams = LinearLayout.LayoutParams(0, 120, 1f)
            setOnClickListener {
                copyFileToInternalStorage(uri, fileName)
                dialog.dismiss()
            }
        }

        btnLayout.addView(cancelBtn)
        btnLayout.addView(confirmBtn)
        container.addView(title)
        container.addView(fileText)
        container.addView(btnLayout)
        
        dialog.setContentView(container)
        dialog.show()
    }

    private fun copyFileToInternalStorage(uri: Uri, fileName: String) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val outFile = File(getExternalFilesDir(null), fileName)
            val outputStream = FileOutputStream(outFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            Toast.makeText(this, "Plugin Saved!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to import file.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) result = cursor.getString(index)
                }
            }
        }
        if (result == null) result = uri.path?.substringAfterLast('/')
        return result
    }

    private fun autoDetectMinecraft() {
        try {
            val appInfo = packageManager.getApplicationInfo("com.mojang.minecraftpe", 0)
            targetApkPath = appInfo.sourceDir
        } catch (e: PackageManager.NameNotFoundException) {
            targetApkPath = null
        }
    }

    private fun switchTab(tabIndex: Int) {
        setTabVisuals(homeBtn, tabIndex == 0)
        setTabVisuals(modsBtn, tabIndex == 1)
        setTabVisuals(settingsBtn, tabIndex == 2)

        homeLayout.visibility = if (tabIndex == 0) View.VISIBLE else View.GONE
        modsLayout.visibility = if (tabIndex == 1) View.VISIBLE else View.GONE
        settingsLayout.visibility = if (tabIndex == 2) View.VISIBLE else View.GONE
    }

    private fun setTabVisuals(button: TextView, isSelected: Boolean) {
        button.setTextColor(if (isSelected) Color.BLACK else Color.WHITE)
        button.background = GradientDrawable().apply {
            setColor(if (isSelected) Color.WHITE else Color.TRANSPARENT)
            cornerRadius = 16f
        }
    }

    private fun createMenuButton(text: String): TextView {
        return TextView(this@MainActivity).apply {
            this.text = text
            textSize = 16f
            gravity = Gravity.CENTER
            paint.isFakeBoldText = true
            setPadding(0, 30, 0, 30)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) }
        }
    }

    private fun enforceFullscreenSafely() {
        try {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN 
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION 
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        } catch (e: Exception) {
            // Failsafe so the app won't crash even if hiding the bar fails
        }
    }
}
