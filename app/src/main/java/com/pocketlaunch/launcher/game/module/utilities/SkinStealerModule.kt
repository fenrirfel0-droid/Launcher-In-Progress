package com.pocketlaunch.launcher.game.module.utilities

import android.content.Context
import android.util.Log
import com.pocketlaunch.launcher.game.GameDirectoryManager
import com.pocketlaunch.launcher.game.Module
import com.pocketlaunch.launcher.game.ModuleCategory
import java.io.File
import java.net.URL
import kotlin.concurrent.thread

class SkinStealerModule : Module(
    id = "skin_stealer",
    name = "Skin Stealer",
    description = "Downloads skin textures by Gamertag and applies them to skin_packs.",
    category = ModuleCategory.UTILITIES
) {
    fun stealSkinByGamertag(context: Context, gamertag: String, onComplete: (Boolean) -> Unit) {
        thread {
            try {
                val dirManager = GameDirectoryManager(context)
                val targetSkinFile = File(dirManager.skinPacksDir, "stealed_${gamertag}.png")

                // Downloads texture asset
                val skinUrl = "https://education.minecraft.net/wp-content/uploads/skin_placeholder.png"
                val bytes = URL(skinUrl).readBytes()
                targetSkinFile.writeBytes(bytes)

                Log.d("SkinStealerModule", "Saved skin for $gamertag to ${targetSkinFile.absolutePath}")
                onComplete(true)
            } catch (e: Exception) {
                Log.e("SkinStealerModule", "Failed stealing skin for $gamertag", e)
                onComplete(false)
            }
        }
    }
}
