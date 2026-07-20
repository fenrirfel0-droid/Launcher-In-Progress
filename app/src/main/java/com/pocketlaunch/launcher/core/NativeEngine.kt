package com.pocketlaunch.launcher.core

object NativeEngine {
    init { System.loadLibrary("pocketlaunch") }
    external fun initEngine(): Boolean
    external fun toggleModule(moduleId: String, enabled: Boolean)
    external fun isModuleEnabled(moduleId: String): Boolean
}

