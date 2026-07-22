package com.pocketlaunch.launcher.game

import android.content.Context
import android.util.Log
import java.io.File

class GameDirectoryManager(context: Context) {

    companion object {
        private const val TAG = "GameDirectoryManager"
    }

    val gameRootDir: File = File(context.getExternalFilesDir(null), "game")

    val mojangDir: File = File(gameRootDir, "com.mojang")
    val minecraftWorldsDir: File = File(mojangDir, "minecraftWorlds")
    val resourcePacksDir: File = File(mojangDir, "resource_packs")
    val behaviorPacksDir: File = File(mojangDir, "behavior_packs")
    val skinPacksDir: File = File(mojangDir, "skin_packs")
    val minecraftPeOptionsDir: File = File(mojangDir, "minecraftpe")

    val clientDataDir: File = File(gameRootDir, "client")
    val clientConfigsDir: File = File(clientDataDir, "configs")
    val clientWaypointsDir: File = File(clientDataDir, "waypoints")
    val clientLogsDir: File = File(clientDataDir, "logs")

    val modsDir: File = File(gameRootDir, "mods")

    init {
        createGameStructure()
    }

    fun createGameStructure(): Boolean {
        val directories = listOf(
            gameRootDir,
            mojangDir,
            minecraftWorldsDir,
            resourcePacksDir,
            behaviorPacksDir,
            skinPacksDir,
            minecraftPeOptionsDir,
            clientDataDir,
            clientConfigsDir,
            clientWaypointsDir,
            clientLogsDir,
            modsDir
        )

        var allCreated = true
        for (dir in directories) {
            if (!dir.exists()) {
                val created = dir.mkdirs()
                if (created) {
                    Log.d(TAG, "Initialized directory: ${dir.name}")
                } else {
                    Log.e(TAG, "Failed to create directory: ${dir.absolutePath}")
                    allCreated = false
                }
            }
        }
        return allCreated
    }
}
