package com.pocketlaunch.launcher

import android.app.Activity
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class ProxyActivity : Activity() {
    
    private var guestAssetManager: AssetManager? = null
    private var guestResources: Resources? = null
    private var guestTheme: Resources.Theme? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val apkPath = intent.getStringExtra("APK_PATH")
        val className = intent.getStringExtra("CLASS_NAME")
        
        if (apkPath != null && className != null) {
            loadGuestResources(apkPath)
            
            // Temporary Proxy UI to prove the screen is intercepting the engine
            val layout = LinearLayout(this).apply {
                gravity = Gravity.CENTER
                setBackgroundColor(Color.parseColor("#000000"))
            }
            val text = TextView(this).apply {
                text = "ENGINE PROXY RUNNING\n\nHooking into:\n$className"
                setTextColor(Color.parseColor("#00E676"))
                gravity = Gravity.CENTER
                textSize = 18f
            }
            layout.addView(text)
            setContentView(layout)
            
            // TODO: Next we will map the Activity lifecycle to the guest class here
            
        } else {
            Toast.makeText(this, "Proxy Error: Missing APK Path", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // Tricks Android into reading textures/audio from the uninstalled APK
    private fun loadGuestResources(apkPath: String) {
        try {
            val assetManager = AssetManager::class.java.newInstance()
            val addAssetPathMethod = assetManager.javaClass.getMethod("addAssetPath", String::class.java)
            addAssetPathMethod.invoke(assetManager, apkPath)
            
            guestAssetManager = assetManager
            guestResources = Resources(assetManager, super.getResources().displayMetrics, super.getResources().configuration)
            guestTheme = guestResources?.newTheme()
            guestTheme?.setTo(super.getTheme())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getAssets(): AssetManager {
        return guestAssetManager ?: super.getAssets()
    }

    override fun getResources(): Resources {
        return guestResources ?: super.getResources()
    }
}
