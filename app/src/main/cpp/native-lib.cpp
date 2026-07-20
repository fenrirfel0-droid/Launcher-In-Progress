#include <jni.h>
#include <string>
#include <unordered_map>
#include <mutex>
#include <android/log.h>

#define LOG_TAG "InkEngineNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

static std::unordered_map<std::string, bool> g_ModuleStates;
static std::mutex g_StateMutex;

extern "C" JNIEXPORT jboolean JNICALL
Java_com_pocketlaunch_launcher_core_NativeEngine_initEngine(JNIEnv* env, jobject /* this */) {
    std::lock_guard<std::mutex> lock(g_StateMutex);
    g_ModuleStates["Hitbox Expander"] = false;
    g_ModuleStates["Reach (3.0 blocks)"] = false;
    g_ModuleStates["AutoClicker"] = false;
    g_ModuleStates["ESP / Wallhack"] = false;
    return JNI_TRUE;
}

extern "C" JNIEXPORT void JNICALL
Java_com_pocketlaunch_launcher_core_NativeEngine_toggleModule(JNIEnv* env, jobject /* this */, jstring moduleId, jboolean enabled) {
    const char* idStr = env->GetStringUTFChars(moduleId, nullptr);
    if (!idStr) return;
    std::string id(idStr);
    env->ReleaseStringUTFChars(moduleId, idStr);
    std::lock_guard<std::mutex> lock(g_StateMutex);
    g_ModuleStates[id] = (enabled == JNI_TRUE);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_pocketlaunch_launcher_core_NativeEngine_isModuleEnabled(JNIEnv* env, jobject /* this */, jstring moduleId) {
    const char* idStr = env->GetStringUTFChars(moduleId, nullptr);
    if (!idStr) return JNI_FALSE;
    std::string id(idStr);
    env->ReleaseStringUTFChars(moduleId, idStr);
    std::lock_guard<std::mutex> lock(g_StateMutex);
    auto it = g_ModuleStates.find(id);
    return (it != g_ModuleStates.end() && it->second) ? JNI_TRUE : JNI_FALSE;
}

