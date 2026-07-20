package com.pocketlaunch.launcher

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
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
    private var detectedVersionName: String = "Not Detected"
    
    // UI Layout containers
    private lateinit var contentContainer: FrameLayout
    private lateinit var launchView: ScrollView
    private lateinit var instancesView: ScrollView
    private lateinit var aboutView: ScrollView
    private lateinit var settingsView: ScrollView

    // Nav Item references for highlighting active view
    private lateinit var navLaunch: TextView
    private lateinit var navInstances: TextView
    private lateinit var navAbout: TextView
    private lateinit var navSettings: TextView

    // Instance UI indicators
    private lateinit var instanceBadgeText: TextView
    private lateinit var instanceVersionDetailText: TextView

    // Theme Design System
    private val bgDark = "#09090B"
    private val cardDark = "#121214"
    private val accentTeal = "#00D8A1"
    private val textGray = "#A1A1AA"
    private val borderGray = "#27272A"

    // Custom File Importer Contract (.zip and .so)
    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { handleSelectedFile(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            autoDetectMinecraftVersion()

            // 1. Root Landscape Container
            val rootLayout = LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.parseColor(bgDark))
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }

            // 2. Top Header Navigation Bar (Matches LeviLauncher exactly)
            val topNav = LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(50, 30, 50, 30)
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                background = GradientDrawable().apply {
                    setColor(Color.parseColor(bgDark))
                    setStroke(2, Color.parseColor(borderGray))
                }
            }

            val appTitle = TextView(this@MainActivity).apply {
                text = "InkLauncher Unlocked"
                textSize = 20f
                setTextColor(Color.parseColor(accentTeal))
                typeface = Typeface.DEFAULT_BOLD
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }

            navLaunch = createNavButton("Launch", true) { switchTab(0) }
            navInstances = createNavButton("Instances", false) { switchTab(1) }
            navAbout = createNavButton("About", false) { switchTab(2) }
            navSettings = createNavButton("Settings", false) { switchTab(3) }

            topNav.addView(appTitle)
            topNav.addView(navLaunch)
            topNav.addView(navInstances)
            topNav.addView(navAbout)
            topNav.addView(navSettings)
            rootLayout.addView(topNav)

            // 3. Central Content Frame Switcher
            contentContainer = FrameLayout(this@MainActivity).apply {
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
            }

            // Build all active tab structures
            launchView = buildLaunchDashboard()
            instancesView = buildInstancesDashboard()
            aboutView = buildAboutDashboard()
            settingsView = buildSettingsDashboard()

            contentContainer.addView(launchView)
            contentContainer.addView(instancesView)
            contentContainer.addView(aboutView)
            contentContainer.addView(settingsView)

            rootLayout.addView(contentContainer)
            setContentView(rootLayout)

            // System setups
            enforceFullscreenSafely()
            switchTab(0) // Default to Launch Dashboard

        } catch (t: Throwable) {
            val sw = StringWriter()
            t.printStackTrace(PrintWriter(sw))
            setContentView(ScrollView(this@MainActivity).apply {
                setBackgroundColor(Color.WHITE)
                addView(TextView(this@MainActivity).apply {
                    text = "CRASH IN MAIN ACTIVITY LOG:\n\n$sw"
                    setTextColor(Color.RED)
                    setPadding(40, 40, 40, 40)
                })
            })
        }
    }

    // --- MAIN DASHBOARD (LAUNCH TAB) ---
    private fun buildLaunchDashboard(): ScrollView {
        val scroller = ScrollView(this@MainActivity).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            visibility = View.GONE
        }

        val mainLayout = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 40)
        }

        // Row containing Engine Branding and Instance State
        val headerRow = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 40)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val leftBrand = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }.apply {
            addView(TextView(this@MainActivity).apply {
                text = "Minecraft"
                textSize = 34f
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD
            })
            instanceVersionDetailText = TextView(this@MainActivity).apply {
                text = "Version Isolation: $detectedVersionName"
                textSize = 14f
                setTextColor(Color.parseColor(textGray))
            }
            addView(instanceVersionDetailText)
        }

        val rightBadgeContainer = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        instanceBadgeText = TextView(this@MainActivity).apply {
            text = "Current Instance\n$detectedVersionName"
            textSize = 12f
            setTextColor(Color.WHITE)
            setPadding(35, 20, 35, 20)
            gravity = Gravity.CENTER
            background = GradientDrawable().apply {
                setColor(Color.parseColor(cardDark))
                setStroke(2, Color.parseColor(borderGray))
                cornerRadius = 24f
            }
        }
        rightBadgeContainer.addView(instanceBadgeText)

        headerRow.addView(leftBrand)
        headerRow.addView(rightBadgeContainer)
        mainLayout.addView(headerRow)

        // Split columns layout (Content management vs Action utilities)
        val splitGrid = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        // Content Column
        val leftCol = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.4f)
            setPadding(0, 0, 20, 0)
        }

        val manageModsBtn = Button(this@MainActivity).apply {
            text = "Manage Mods"
            setTextColor(Color.WHITE)
            isAllCaps = false
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(40, 25, 40, 25)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#155F4D")) // Premium Dark Teal Tint
                cornerRadius = 20f
            }
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 30)
            }
            setOnClickListener { switchTab(1) } // Redirects to Instance Manager
        }

        val contentCard = createCardPanel("Content Management", "View All →")
        contentCard.addView(createListItemRow("Worlds", "Active Simulation", "2 >"))
        contentCard.addView(createListItemRow("Resource Packs", "Flarial Pack Importer", "27 >"))
        contentCard.addView(createListItemRow("Behavior Packs", "Script Hooks Enabled", "14 >"))

        leftCol.addView(manageModsBtn)
        leftCol.addView(contentCard)

        // Utilities Column
        val rightCol = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            setPadding(20, 0, 0, 0)
        }

        val miscCard = createCardPanel("Miscellaneous Tools", null)
        miscCard.addView(createListItemRow("CurseForge Launcher", "Mods database", "Open"))
        miscCard.addView(createListItemRow("Microsoft Accounts", "Authentication core", "Signed-in"))
        miscCard.addView(createListItemRow("Quick Launcher Mode", "Safe bypass mode", "Ready"))

        val bigLaunchBtn = Button(this@MainActivity).apply {
            text = "LAUNCH GAME"
            setTextColor(Color.BLACK)
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150).apply {
                setMargins(0, 40, 0, 0)
            }
            background = GradientDrawable().apply {
                setColor(Color.parseColor(accentTeal))
                cornerRadius = 24f
            }
            setOnClickListener { triggerGameLaunch() }
        }

        rightCol.addView(miscCard)
        rightCol.addView(bigLaunchBtn)

        splitGrid.addView(leftCol)
        splitGrid.addView(rightCol)
        mainLayout.addView(splitGrid)

        scroller.addView(mainLayout)
        return scroller
    }

    // --- INSTANCES & MOD FILE IMPORTER TAB ---
    private fun buildInstancesDashboard(): ScrollView {
        val scroller = ScrollView(this@MainActivity).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            visibility = View.GONE
        }

        val container = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 40)
        }

        val tabTitle = TextView(this@MainActivity).apply {
            text = "Active Mod & Pack Management"
            textSize = 24f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 30)
        }
        container.addView(tabTitle)

        // Upload Panel Card
        val uploadCard = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            background = GradientDrawable().apply {
                setColor(Color.parseColor(cardDark))
                setStroke(2, Color.parseColor(borderGray))
                cornerRadius = 24f
            }
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val uploadTitle = TextView(this@MainActivity).apply {
            text = "Import Custom Files"
            textSize = 18f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 10)
        }
        
        val uploadDesc = TextView(this@MainActivity).apply {
            text = "Directly load MCPE modules (.so native dynamic libraries) and resources (.zip packs) directly into your Ink Engine."
            textSize = 14f
            setTextColor(Color.parseColor(textGray))
            setPadding(0, 0, 0, 40)
        }

        val importBtn = Button(this@MainActivity).apply {
            text = "IMPORT SYSTEM FILE"
            setTextColor(Color.BLACK)
            textSize = 15f
            typeface = Typeface.DEFAULT_BOLD
            background = GradientDrawable().apply {
                setColor(Color.parseColor(accentTeal))
                cornerRadius = 20f
            }
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 130)
            setOnClickListener { filePickerLauncher.launch("*/*") }
        }

        uploadCard.addView(uploadTitle)
        uploadCard.addView(uploadDesc)
        uploadCard.addView(importBtn)
        container.addView(uploadCard)

        scroller.addView(container)
        return scroller
    }

    // --- ABOUT TAB ---
    private fun buildAboutDashboard(): ScrollView {
        val scroller = ScrollView(this@MainActivity).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            visibility = View.GONE
        }
        val layout = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 40)
        }
        layout.addView(TextView(this@MainActivity).apply {
            text = "About InkLauncher"
            textSize = 24f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 20)
        })
        layout.addView(TextView(this@MainActivity).apply {
            text = "InkLauncher Unlocked is built to offer the visual premium aesthetics of high-tier bedrock sandboxes like LeviLauncher and Atlas Client. Seamless native plugin configurations and in-game overlays are handled smoothly without high-overhead processes."
            textSize = 16f
            setTextColor(Color.parseColor(textGray))
            setLineSpacing(0f, 1.4f)
        })
        scroller.addView(layout)
        return scroller
    }

    // --- SETTINGS TAB ---
    private fun buildSettingsDashboard(): ScrollView {
        val scroller = ScrollView(this@MainActivity).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            visibility = View.GONE
        }
        val layout = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 40)
        }
        layout.addView(TextView(this@MainActivity).apply {
            text = "Launcher Settings"
            textSize = 24f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 30)
        })

        // Simple setting toggles
        layout.addView(createSettingToggle("Enable Client HUD Overlays", true))
        layout.addView(createSettingToggle("Deep Memory Hooks (Unsafe JNI)", false))
        layout.addView(createSettingToggle("Low Overhead Battery Saver", true))

        scroller.addView(layout)
        return scroller
    }

    // --- NATIVE FILE PICKER IMPORT FLOW ---
    private fun handleSelectedFile(uri: Uri) {
        val fileName = getFileNameFromUri(uri) ?: "UnknownModFile"
        if (fileName.endsWith(".so", ignoreCase = true) || fileName.endsWith(".zip", ignoreCase = true)) {
            showCustomImportDialog(fileName, uri)
        } else {
            Toast.makeText(this@MainActivity, "Unsupported format! Only .so and .zip allowed.", Toast.LENGTH_LONG).show()
        }
    }

    private fun showCustomImportDialog(fileName: String, uri: Uri) {
        val dialog = Dialog(this@MainActivity).apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        val container = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
            background = GradientDrawable().apply {
                setColor(Color.parseColor(cardDark))
                setStroke(2, Color.parseColor(borderGray))
                cornerRadius = 32f
            }
            layoutParams = ViewGroup.LayoutParams(550, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val title = TextView(this@MainActivity).apply {
            text = "Verify Import Package"
            textSize = 20f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 15)
        }

        val fileDetails = TextView(this@MainActivity).apply {
            text = "File Name: $fileName\nFormat Verified. Proceed with copy engine procedure?"
            textSize = 14f
            setTextColor(Color.parseColor(textGray))
            setPadding(0, 0, 0, 50)
        }

        val actionRow = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val cancelBtn = Button(this@MainActivity).apply {
            text = "Cancel"
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply { setColor(Color.TRANSPARENT) }
            layoutParams = LinearLayout.LayoutParams(0, 110, 1f)
            setOnClickListener { dialog.dismiss() }
        }

        val confirmBtn = Button(this@MainActivity).apply {
            text = "Import"
            setTextColor(Color.BLACK)
            typeface = Typeface.DEFAULT_BOLD
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = 16f
            }
            layoutParams = LinearLayout.LayoutParams(0, 110, 1f)
            setOnClickListener {
                copyFileToAppStorage(uri, fileName)
                dialog.dismiss()
            }
        }

        actionRow.addView(cancelBtn)
        actionRow.addView(confirmBtn)
        
        container.addView(title)
        container.addView(fileDetails)
        container.addView(actionRow)

        dialog.setContentView(container)
        dialog.show()
    }

    private fun copyFileToAppStorage(uri: Uri, fileName: String) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val destinationFile = File(getExternalFilesDir(null), fileName)
            val outputStream = FileOutputStream(destinationFile)
            
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            
            Toast.makeText(this@MainActivity, "$fileName successfully written to engine!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, "Failed to write package content.", Toast.LENGTH_LONG).show()
        }
    }

    // --- HELPER CONSTRUCTORS ---
    private fun createNavButton(title: String, isActive: Boolean, onClick: () -> Unit): TextView {
        return TextView(this@MainActivity).apply {
            text = title
            textSize = 16f
            setTextColor(if (isActive) Color.parseColor(accentTeal) else Color.WHITE)
            typeface = if (isActive) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            setPadding(35, 15, 35, 15)
            setOnClickListener {
                onClick()
            }
        }
    }

    private fun createCardPanel(title: String, actionText: String?): LinearLayout {
        val card = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            background = GradientDrawable().apply {
                setColor(Color.parseColor(cardDark))
                setStroke(2, Color.parseColor(borderGray))
                cornerRadius = 24f
            }
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 30)
            }
        }

        val headerRow = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, 20)
        }

        headerRow.addView(TextView(this@MainActivity).apply {
            text = title
            textSize = 16f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })

        if (actionText != null) {
            headerRow.addView(TextView(this@MainActivity).apply {
                text = actionText
                textSize = 14f
                setTextColor(Color.parseColor(accentTeal))
            })
        }

        card.addView(headerRow)
        return card
    }

    private fun createListItemRow(title: String, desc: String, count: String): LinearLayout {
        val row = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 20, 0, 20)
            gravity = Gravity.CENTER_VERTICAL
        }

        val textDetails = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        textDetails.addView(TextView(this@MainActivity).apply {
            text = title
            textSize = 15f
            setTextColor(Color.WHITE)
        })

        textDetails.addView(TextView(this@MainActivity).apply {
            text = desc
            textSize = 12f
            setTextColor(Color.parseColor(textGray))
        })

        val countText = TextView(this@MainActivity).apply {
            text = count
            textSize = 14f
            setTextColor(Color.parseColor(textGray))
        }

        row.addView(textDetails)
        row.addView(countText)
        return row
    }

    private fun createSettingToggle(label: String, default: Boolean): LinearLayout {
        return LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 25, 0, 25)
            gravity = Gravity.CENTER_VERTICAL
            
            addView(TextView(this@MainActivity).apply {
                text = label
                textSize = 16f
                setTextColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            })

            addView(Switch(this@MainActivity).apply {
                isChecked = default
            })
        }
    }

    private fun getFileNameFromUri(uri: Uri): String? {
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

    // --- SYSTEM & COMPILER TRIGGERS ---
    private fun autoDetectMinecraftVersion() {
        try {
            val pm = packageManager
            val packageInfo = pm.getPackageInfo("com.mojang.minecraftpe", 0)
            targetApkPath = packageInfo.applicationInfo.sourceDir
            detectedVersionName = "Minecraft " + packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            targetApkPath = null
            detectedVersionName = "Not Installed"
        }
    }

    private fun triggerGameLaunch() {
        if (!Settings.canDrawOverlays(this@MainActivity)) {
            Toast.makeText(this@MainActivity, "System Alert Window required for floating mod menu context!", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
        } else {
            // Fires up the custom UI controller overlay inside game space
            startService(Intent(this@MainActivity, FloatingMenuService::class.java))
            
            val launchIntent = packageManager.getLaunchIntentForPackage("com.mojang.minecraftpe")
            if (launchIntent != null) {
                startActivity(launchIntent)
            } else {
                Toast.makeText(this@MainActivity, "Minecraft: Bedrock Edition missing!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun switchTab(tabIndex: Int) {
        // Highlight active Top Bar item
        navLaunch.setTextColor(Color.parseColor(if (tabIndex == 0) accentTeal else "#FFFFFF"))
        navLaunch.typeface = if (tabIndex == 0) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        navInstances.setTextColor(Color.parseColor(if (tabIndex == 1) accentTeal else "#FFFFFF"))
        navInstances.typeface = if (tabIndex == 1) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        navAbout.setTextColor(Color.parseColor(if (tabIndex == 2) accentTeal else "#FFFFFF"))
        navAbout.typeface = if (tabIndex == 2) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        navSettings.setTextColor(Color.parseColor(if (tabIndex == 3) accentTeal else "#FFFFFF"))
        navSettings.typeface = if (tabIndex == 3) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        // Visibility switches
        launchView.visibility = if (tabIndex == 0) View.VISIBLE else View.GONE
        instancesView.visibility = if (tabIndex == 1) View.VISIBLE else View.GONE
        aboutView.visibility = if (tabIndex == 2) View.VISIBLE else View.GONE
        settingsView.visibility = if (tabIndex == 3) View.VISIBLE else View.GONE
    }

    private fun enforceFullscreenSafely() {
        try {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        } catch (e: Exception) {}
    }
}
