package com.pocketlaunch.launcher.game.module.utilities

import android.content.Context
import android.util.Log
import com.pocketlaunch.launcher.game.GameDirectoryManager
import com.pocketlaunch.launcher.game.Module
import com.pocketlaunch.launcher.game.ModuleCategory
import java.io.File

class PackChangerModule : Module(
    id = "pack_changer",
    name = "In-Game Pack Changer",
    description = "Switch active resource packs dynamically while on servers or in worlds.",
    category = ModuleCategory.UTILITIES
) {
    fun swapResourcePack(context: Context, packZipFile: File): Boolean {
        if (!isEnabled || !packZipFile.exists()) return false

        val dirManager = GameDirectoryManager(context)
        val targetPack = File(dirManager.resourcePacksDir, packZipFile.name)

        return try {
            packZipFile.copyTo(targetPack, overwrite = true)
            Log.d("PackChangerModule", "Activated pack: ${packZipFile.name}")
            true
        } catch (e: Exception) {
            Log.e("PackChangerModule", "Failed to swap resource pack", e)
            false
        }
    }
}
