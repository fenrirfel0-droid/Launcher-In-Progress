package com.pocketlaunch.launcher

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.pocketlaunch.launcher.game.GameDirectoryManager
import com.pocketlaunch.launcher.overlay.InkOverlayService
import com.pocketlaunch.launcher.util.GraphicsSettingsManager

class MainActivity : AppCompatActivity() {

    private val bgVoid = "#0A0B10"
    private val cardDark = "#131520"
    private val cardBorder = "#1E2235"
    private val accentPurple = "#7C3AED"
    private val textWhite = "#FFFFFF"
    private val textMuted = "#8A92B2"

    private lateinit var graphicsManager: GraphicsSettingsManager
    private lateinit var dirManager: GameDirectoryManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        graphicsManager = GraphicsSettingsManager(this)
        dirManager = GameDirectoryManager(this)

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(bgVoid))
            setPadding(48, 40, 48, 40)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        rootLayout.addView(TextView(this).apply {
            text = "InkClient Launcher & Engine Settings"
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor(textWhite))
            setPadding(0, 0, 0, 24)
        })

        val scroll = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
            isVerticalScrollBarEnabled = false
        }

        val cardContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        // Setting 1: VSync Disabler
        cardContainer.addView(createSettingRow(
            title = "Disable V-Sync",
            subtitle = "Removes frame timing constraints to reduce latency.",
            widget = Switch(this).apply {
                setOnCheckedChangeListener { _, isChecked -> graphicsManager.setVSyncDisabled(isChecked) }
            }
        ))

        // Setting 2: FPS Uncapper
        val fpsLabel = TextView(this).apply {
            text = "Unlimited FPS"
            setTextColor(Color.parseColor(accentPurple))
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
        }
        val fpsSeek = SeekBar(this).apply {
            max = 240
            progress = 0
            layoutParams = LinearLayout.LayoutParams(280, ViewGroup.LayoutParams.WRAP_CONTENT)
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar?, valVal: Int, fromUser: Boolean) {
                    fpsLabel.text = if (valVal == 0) "Unlimited" else "$valVal FPS"
                    graphicsManager.setFpsUncapped(valVal)
                }
                override fun onStartTrackingTouch(sb: SeekBar?) {}
                override fun onStopTrackingTouch(sb: SeekBar?) {}
            })
        }
        val fpsBlock = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.END
            addView(fpsLabel)
            addView(fpsSeek)
        }
        cardContainer.addView(createSettingRow("FPS Limit / Uncapper", "Set Bedrock target framerate", fpsBlock))

        // Setting 3: Entity Culling
        cardContainer.addView(createSettingRow(
            title = "Entity Culling Optimization",
            subtitle = "Hides non-visible entities to maximize FPS.",
            widget = Switch(this).apply {
                isChecked = true
                setOnCheckedChangeListener { _, isChecked -> graphicsManager.setEntityCullingEnabled(isChecked) }
            }
        ))

        // Setting 4: Fast Loading Screen
        cardContainer.addView(createSettingRow(
            title = "Fast Loading Screen / Pre-Loader",
            subtitle = "Buffers chunk assets to speed up world/server joins.",
            widget = Switch(this).apply {
                isChecked = true
                setOnCheckedChangeListener { _, isChecked -> graphicsManager.enableFastLoadingScreen(isChecked) }
            }
        ))

        scroll.addView(cardContainer)
        rootLayout.addView(scroll)

        val launchBtn = Button(this).apply {
            text = "LAUNCH INK CLIENT"
            setTextColor(Color.parseColor(textWhite))
            typeface = Typeface.DEFAULT_BOLD
            background = GradientDrawable().apply {
                setColor(Color.parseColor(accentPurple))
                cornerRadius = 16f
            }
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 130).apply {
                setMargins(0, 20, 0, 0)
            }
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
                } else {
                    startService(Intent(this@MainActivity, InkOverlayService::class.java))
                    packageManager.getLaunchIntentForPackage("com.mojang.minecraftpe")?.let { startActivity(it) }
                        ?: Toast.makeText(this@MainActivity, "Minecraft PE was not found.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        rootLayout.addView(launchBtn)

        setContentView(rootLayout)
        enforceFullscreen()
    }

    private fun createSettingRow(title: String, subtitle: String, widget: View): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(32, 24, 32, 24)
            gravity = Gravity.CENTER_VERTICAL
            background = GradientDrawable().apply {
                setColor(Color.parseColor(cardDark))
                setStroke(1, Color.parseColor(cardBorder))
                cornerRadius = 18f
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }

            val labelBlock = LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }
            labelBlock.addView(TextView(this@MainActivity).apply {
                text = title
                setTextColor(Color.parseColor(textWhite))
                textSize = 14f
                typeface = Typeface.DEFAULT_BOLD
            })
            labelBlock.addView(TextView(this@MainActivity).apply {
                text = subtitle
                setTextColor(Color.parseColor(textMuted))
                textSize = 11f
            })

            addView(labelBlock)
            addView(widget)
        }
    }

    private fun enforceFullscreen() {
        try {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        } catch (_: Exception) {}
    }
}
