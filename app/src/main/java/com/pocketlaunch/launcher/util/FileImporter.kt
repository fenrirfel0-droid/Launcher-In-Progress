package com.pocketlaunch.launcher.util

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object FileImporter {

    private const val TAG = "InkFileImporter"

    /**
     * Safely copies a .so or .zip file from a user's selection into the launcher's internal storage
     */
    fun importFileToWorkspace(context: Context, uri: Uri, fileName: String): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return false
            val workspaceDir = getWorkspaceDir(context)
            
            // Create target file in workspace
            val targetFile = File(workspaceDir, fileName)
            val outputStream = FileOutputStream(targetFile)

            inputStream.copyTo(outputStream)
            
            inputStream.close()
            outputStream.close()
            
            Log.d(TAG, "Successfully imported $fileName to workspace.")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to import file: ${e.message}")
            false
        }
    }

    /**
     * Extracts a .zip file (like a Resource Pack) into a dedicated folder
     */
    fun extractZipPack(context: Context, zipFile: File, destinationFolderName: String): Boolean {
        val destDir = File(getWorkspaceDir(context), destinationFolderName)
        if (!destDir.exists()) destDir.mkdirs()

        return try {
            ZipInputStream(zipFile.inputStream()).use { zis ->
                var zipEntry: ZipEntry? = zis.nextEntry
                while (zipEntry != null) {
                    val newFile = File(destDir, zipEntry.name)
                    
                    if (zipEntry.isDirectory) {
                        newFile.mkdirs()
                    } else {
                        // Ensure parent directories exist
                        newFile.parentFile?.mkdirs()
                        FileOutputStream(newFile).use { fos ->
                            zis.copyTo(fos)
                        }
                    }
                    zis.closeEntry()
                    zipEntry = zis.nextEntry
                }
            }
            Log.d(TAG, "Successfully extracted pack to ${destDir.absolutePath}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract zip: ${e.message}")
            false
        }
    }

    /**
     * Gets the secure, isolated directory where InkLauncher stores its mods and packs
     */
    private fun getWorkspaceDir(context: Context): File {
        val dir = File(context.getExternalFilesDir(null), "InkWorkspace")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }
}
