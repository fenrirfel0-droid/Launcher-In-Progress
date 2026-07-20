package com.pocketlaunch.launcher.core

import android.util.Log

object NativeEngine {

    private var isLoaded = false

    init {
        try {
            // This loads the C++ library compiled from native-lib.cpp
            System.loadLibrary("pocketlaunch")
            isLoaded = true
            Log.d("InkNative", "Native C++ Engine successfully loaded!")
        } catch (e: UnsatisfiedLinkError) {
            Log.e("InkNative", "Failed to load native library: ${e.message}")
            isLoaded = false
        }
    }

    fun isEngineReady(): Boolean = isLoaded

    // JNI Native Methods linked directly to the C++ code
    external fun initEngine(): Boolean
    external fun toggleModule(moduleId: String, enabled: Boolean)
    external fun isModuleEnabled(moduleId: String): Boolean
}
