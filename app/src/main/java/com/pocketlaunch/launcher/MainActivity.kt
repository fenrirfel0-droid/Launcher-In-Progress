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

    private val bgDark = "#000000"
    private val cardDark = "#111111"
    private val textWhite = "#FFFFFF"
    private val textGray = "#888888"
    private val borderGray = "#222222"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(bgDark))
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setPadding(60, 40, 60, 40)
        }

        val header = TextView(this@MainActivity).apply {
            text = "InkLauncher"
            textSize = 28f
            setTextColor(Color.parseColor(textWhite))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 40)
        }

        val splitGrid = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val leftCol = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.2f)
            setPadding(0, 0, 30, 0)
        }

        val contentCard = createCleanCard("Modifications")
        contentCard.addView(createIconRow(android.R.drawable.ic_menu_gallery, "Resource Packs", "Manage visual assets"))
        contentCard.addView(createIconRow(android.R.drawable.ic_menu_manage, "Modules (.so)", "Native dynamic libraries"))
        leftCol.addView(contentCard)

        val rightCol = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        val utilCard = createCleanCard("Utilities")
        utilCard.addView(createIconRow(android.R.drawable.ic_menu_compass, "CurseForge", "Browse repositories"))
        
        val launchBtn = Button(this@MainActivity).apply {
            text = "LAUNCH GAME"
            setTextColor(Color.parseColor(bgDark))
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150).apply { setMargins(0, 30, 0, 0) }
            background = GradientDrawable().apply {
                setColor(Color.parseColor(textWhite))
                cornerRadius = 16f
            }
            setOnClickListener { triggerLaunch() }
        }

        rightCol.addView(utilCard)
        rightCol.addView(launchBtn)

        splitGrid.addView(leftCol)
        splitGrid.addView(rightCol)

        rootLayout.addView(header)
        rootLayout.addView(splitGrid)

        setContentView(rootLayout)
        enforceFullscreen()
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
        
        row.addView(ImageView(this@MainActivity).apply {
            setImageResource(iconRes)
            setColorFilter(Color.parseColor(textWhite))
            layoutParams = LinearLayout.LayoutParams(60, 60).apply { setMargins(0, 0, 30, 0) }
        })

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

        row.addView(textBlock)
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
