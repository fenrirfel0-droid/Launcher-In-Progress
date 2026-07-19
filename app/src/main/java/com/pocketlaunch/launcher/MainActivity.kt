package com.pocketlaunch.launcher

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private var importedApkFile: File? = null

    // Copies the selected APK into your launcher's private storage
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            Toast.makeText(this, "Importing APK, please wait...", Toast.LENGTH_SHORT).show()
            importFileToStorage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkStoragePermissions()

        // Sleek Dark Mode Background
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#121212")) // Deep dark grey/black
            setPadding(60, 60, 60, 60)
        }

        val titleText = TextView(this).apply {
            text = "POCKET LAUNCH"
            textSize = 28f
            setTextColor(Color.parseColor("#00E676")) // Neon Green Gamer vibe
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 100)
        }

        val statusText = TextView(this).apply {
            text = "Ready to Import"
            textSize = 14f
            setTextColor(Color.parseColor("#AAAAAA"))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 40)
        }

        val importButton = createStyledButton("Import Unofficial APK") {
            // Only look for APK files in the file picker
            filePickerLauncher.launch("application/vnd.android.package-archive") 
        }

        val launchButton = createStyledButton("Launch Engine Environment") {
            if (importedApkFile != null && importedApkFile!!.exists()) {
                bootCustomMinecraftEngine(importedApkFile!!)
            } else {
                Toast.makeText(this, "You must import an APK first!", Toast.LENGTH_LONG).show()
            }
        }

        rootLayout.addView(titleText)
        rootLayout.addView(statusText)
        rootLayout.addView(importButton)
        rootLayout.addView(launchButton)

        setContentView(rootLayout)
    }

    private fun createStyledButton(text: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            setTextColor(Color.WHITE)
            isAllCaps = false
            textSize = 16f
            
            // Custom Rounded Button Style
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#1E1E1E"))
                cornerRadius = 24f
                setStroke(2, Color.parseColor("#333333"))
            }
            
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                160
            ).apply {
                setMargins(0, 20, 0, 20)
            }
            setOnClickListener { onClick() }
        }
    }

    private fun importFileToStorage(uri: Uri) {
        Thread {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val targetFile = File(filesDir, "custom_minecraft.apk")
                val outputStream = FileOutputStream(targetFile)

                inputStream?.copyTo(outputStream)
                
                inputStream?.close()
                outputStream.close()

                importedApkFile = targetFile

                runOnUiThread {
                    Toast.makeText(this, "APK Imported Successfully!", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Import Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
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

    private fun bootCustomMinecraftEngine(apkFile: File) {
        Toast.makeText(this, "Extracting and hooking ${apkFile.name}...", Toast.LENGTH_SHORT).show()

        Thread {
            try {
                val optimizedDexOutputDir = getDir("mcpe_dex_cache", MODE_PRIVATE)

                val dexClassLoader = dalvik.system.DexClassLoader(
                    apkFile.absolutePath,
                    optimizedDexOutputDir.absolutePath,
                    null, 
                    classLoader
                )

                val mcActivityClass = dexClassLoader.loadClass("com.mojang.minecraftpe.MainActivity")

                runOnUiThread {
                    // Fire up the Proxy Screen and pass the target game data to it
                    val proxyIntent = Intent(this@MainActivity, ProxyActivity::class.java).apply {
                        putExtra("APK_PATH", apkFile.absolutePath)
                        putExtra("CLASS_NAME", mcActivityClass.name)
                    }
                    startActivity(proxyIntent)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Engine Hook Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }
}
