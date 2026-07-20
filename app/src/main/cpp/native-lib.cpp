#include <jni.h>
#include <string>
#include <unordered_map>
#include <mutex>
#include <android/log.h>

#define LOG_TAG "InkEngineNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

// Thread-safe state storage for active client modules
static std::unordered_map<std::string, bool> g_ModuleStates;
static std::mutex g_StateMutex;

// Initializes the engine and sets default states
extern "C" JNIEXPORT jboolean JNICALL
Java_com_pocketlaunch_launcher_core_NativeEngine_initEngine(JNIEnv* env, jobject /* this */) {
    std::lock_guard<std::mutex> lock(g_StateMutex);
    
    // Register default client modules (Flarial / Atlas style)
    g_ModuleStates["Hitbox Expander"] = false;
    g_ModuleStates["Reach (3.0 blocks)"] = false;
    g_ModuleStates["AutoClicker"] = false;
    g_ModuleStates["ESP / Wallhack"] = false;
    g_ModuleStates["Fullbright"] = false;
    g_ModuleStates["No Render"] = false;
    g_ModuleStates["AirJump"] = false;
    g_ModuleStates["Bhop (Speed)"] = false;
    g_ModuleStates["Jetpack"] = false;

    LOGI("Native Engine initialized with %zu active modules.", g_ModuleStates.size());
    return JNI_TRUE;
}

// Toggles a specific module on or off in memory
extern "C" JNIEXPORT void JNICALL
Java_com_pocketlaunch_launcher_core_NativeEngine_toggleModule(JNIEnv* env, jobject /* this */, jstring moduleId, jboolean enabled) {
    const char* idStr = env->GetStringUTFChars(moduleId, nullptr);
    if (!idStr) return;

    std::string id(idStr);
    env->ReleaseStringUTFChars(moduleId, idStr);

    std::lock_guard<std::mutex> lock(g_StateMutex);
    g_ModuleStates[id] = (enabled == JNI_TRUE);

    LOGI("Module [%s] set to state: %s", id.c_str(), enabled ? "ENABLED" : "DISABLED");
}

// Checks if a module is currently active
extern "C" JNIEXPORT jboolean JNICALL
Java_com_pocketlaunch_launcher_core_NativeEngine_isModuleEnabled(JNIEnv* env, jobject /* this */, jstring moduleId) {
    const char* idStr = env->GetStringUTFChars(moduleId, nullptr);
    if (!idStr) return JNI_FALSE;

    std::string id(idStr);
    env->ReleaseStringUTFChars(moduleId, idStr);

    std::lock_guard<std::mutex> lock(g_StateMutex);
    auto it = g_ModuleStates.find(id);
    if (it != g_ModuleStates.end()) {
        return it->second ? JNI_TRUE : JNI_FALSE;
    }

    return JNI_FALSE;
}
