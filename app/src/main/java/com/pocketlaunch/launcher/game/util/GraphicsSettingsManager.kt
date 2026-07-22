package com.pocketlaunch.launcher.util

import android.content.Context
import android.util.Log
import java.io.File

class GraphicsSettingsManager(context: Context) {

    private val optionsFile = File(
        context.getExternalFilesDir(null),
        "game/com.mojang/minecraftpe/options.txt"
    )

    init {
        ensureOptionsFileExists()
    }

    private fun ensureOptionsFileExists() {
        if (!optionsFile.parentFile.exists()) {
            optionsFile.parentFile.mkdirs()
        }
        if (!optionsFile.exists()) {
            optionsFile.createNewFile()
        }
    }

    fun setVSyncDisabled(disable: Boolean): Boolean {
        return updateOptionKey("gfx_vsync", if (disable) "0" else "1")
    }

    fun setFpsUncapped(targetFps: Int): Boolean {
        return updateOptionKey("gfx_max_framerate", targetFps.toString())
    }

    fun setEntityCullingEnabled(enabled: Boolean): Boolean {
        return updateOptionKey("gfx_entity_culling", if (enabled) "1" else "0")
    }

    fun enableFastLoadingScreen(enabled: Boolean): Boolean {
        updateOptionKey("gfx_chunk_builder_threads", if (enabled) "4" else "2")
        updateOptionKey("gfx_pre_load_chunks", if (enabled) "1" else "0")
        return updateOptionKey("gfx_tex_streaming_budget", if (enabled) "1024" else "512")
    }

    private fun updateOptionKey(key: String, value: String): Boolean {
        return try {
            val lines = optionsFile.readLines().toMutableList()
            var found = false

            for (i in lines.indices) {
                if (lines[i].startsWith("$key:")) {
                    lines[i] = "$key:$value"
                    found = true
                    break
                }
            }

            if (!found) {
                lines.add("$key:$value")
            }

            optionsFile.writeText(lines.joinToString("\n"))
            Log.d("GraphicsSettings", "Set $key -> $value")
            true
        } catch (e: Exception) {
            Log.e("GraphicsSettings", "Failed writing to options.txt", e)
            false
        }
    }
}
