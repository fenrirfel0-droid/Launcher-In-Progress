package com.pocketlaunch.launcher.game.module.utilities

import android.content.Context
import android.util.Log
import com.pocketlaunch.launcher.game.GameDirectoryManager
import com.pocketlaunch.launcher.game.Module
import com.pocketlaunch.launcher.game.ModuleCategory
import java.io.File

class ShaderLoaderModule : Module(
    id = "shader_loader",
    name = "Shader Loader",
    description = "Injects custom materials and patched render dragon shaders.",
    category = ModuleCategory.UTILITIES
) {
    fun loadShaderPack(context: Context, shaderFolder: File): Boolean {
        if (!isEnabled) return false
        val dirManager = GameDirectoryManager(context)
        val targetFolder = File(dirManager.resourcePacksDir, "ink_shader_pack")

        return try {
            if (shaderFolder.exists()) {
                shaderFolder.copyRecursively(targetFolder, overwrite = true)
                Log.d("ShaderLoaderModule", "Injected shader pack successfully")
                true
            } else false
        } catch (e: Exception) {
            Log.e("ShaderLoaderModule", "Shader loading error", e)
            false
        }
    }
}
