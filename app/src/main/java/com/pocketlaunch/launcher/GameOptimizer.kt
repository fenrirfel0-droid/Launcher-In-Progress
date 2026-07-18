package com.pocketlaunch.launcher

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

object GameOptimizer {

    /**
     * Modifies the options.txt inside Minecraft's scoped storage folder structure.
     */
    fun applyPvPConfigWithSAF(context: Context, rootFolderUri: Uri): Boolean {
        val rootFolder = DocumentFile.fromTreeUri(context, rootFolderUri) ?: return false
        
        // Find or navigate down to the 'minecraftpe' folder directory structure
        // RootUri should point right at: Android/data/com.mojang.minecraftpe/files/games/com.mojang/
        val mcPeFolder = rootFolder.findFile("minecraftpe") ?: rootFolder
        val optionsFile = mcPeFolder.findFile("options.txt") ?: return false

        try {
            val contentResolver = context.contentResolver
            
            // Read lines from the Uri stream
            val inputStream = contentResolver.openInputStream(optionsFile.uri) ?: return false
            val lines = inputStream.bufferedReader().readLines()
            inputStream.close()

            val updatedLines = mutableListOf<String>()
            val pvpSettings = mapOf(
                "gfx_renderdistance" to "32", 
                "gfx_vsync" to "0",           
                "gfx_frame_rate_limit" to "120", 
                "gfx_fancyskies" to "0",      
                "gfx_leaves" to "0",          
                "gfx_smoothlighting" to "0",  
                "gfx_shadows" to "0",         
                "gfx_particles" to "0"        
            )

            val modifiedKeys = mutableSetOf<String>()

            for (line in lines) {
                val splitIndex = line.indexOf(':')
                if (splitIndex != -1) {
                    val key = line.substring(0, splitIndex).trim()
                    if (pvpSettings.containsKey(key)) {
                        updatedLines.add("$key:${pvpSettings[key]}")
                        modifiedKeys.add(key)
                        continue
                    }
                }
                updatedLines.add(line)
            }

            for ((key, value) in pvpSettings) {
                if (!modifiedKeys.contains(key)) {
                    updatedLines.add("$key:$value")
                }
            }

            // Write back to the Document Uri
            val outputStream = contentResolver.openOutputStream(optionsFile.uri, "w") ?: return false
            outputStream.bufferedWriter().use { writer ->
                writer.write(updatedLines.joinToString("\n"))
            }
            outputStream.close()
            return true

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}

