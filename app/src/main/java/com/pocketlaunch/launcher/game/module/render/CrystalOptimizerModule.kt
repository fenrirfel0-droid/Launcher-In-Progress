package com.pocketlaunch.launcher.game.module.render

import android.content.Context
import com.pocketlaunch.launcher.game.Module
import com.pocketlaunch.launcher.game.ModuleCategory

class CrystalOptimizerModule : Module(
    id = "crystal_optimizer",
    name = "Crystal Optimization",
    description = "Optimizes end crystal explosion particles and animation ticks.",
    category = ModuleCategory.RENDER
) {
    var reduceExplosionParticles: Boolean = true
    var fastCrystalBreakAnim: Boolean = true
}
