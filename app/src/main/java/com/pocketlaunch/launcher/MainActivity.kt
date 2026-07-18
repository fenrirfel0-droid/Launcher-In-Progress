package com.pocketlaunch.launcher

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    
    // We will save the authorized folder URI string in Android SharedPreferences
    private val PREFS_NAME = "LauncherPrefs"
    private val KEY_FOLDER_URI = "minecraft_folder_uri"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LauncherTheme {
                var isFolderAuthorized by remember { mutableStateOf(hasFolderPermission()) }

                // Listener to catch the folder permission result
                val folderLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocumentTree()
                ) { uri: Uri? ->
                    if (uri != null) {
                        // Persist the folder access permission across app restarts
                        contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                        saveFolderUri(uri.toString())
                        isFolderAuthorized = true
                        Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Permission Denied. Cannot optimize game.", Toast.LENGTH_LONG).show()
                    }
                }

                DashboardScreen(
                    isFolderAuthorized = isFolderAuthorized,
                    onLaunchClick = { launchMinecraft() },
                    onOptimizeClick = { 
                        val uriStr = getSavedFolderUri()
                        if (uriStr != null) {
                            optimizeGameSettings(Uri.parse(uriStr))
                        }
                    },
                    onRequestPermissionClick = {
                        // Direct the user straight into the Android data folder structure
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                            val hintUri = Uri.parse("content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fdata%2Fcom.mojang.minecraftpe")
                            putExtra(DocumentsContract.EXTRA_INITIAL_URI, hintUri)
                        }
                        folderLauncher.launch(null)
                    }
                )
            }
        }
    }

    private fun launchMinecraft() {
        val launchIntent = packageManager.getLaunchIntentForPackage("com.mojang.minecraftpe")
        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            Toast.makeText(this, "Minecraft Bedrock not found!", Toast.LENGTH_LONG).show()
        }
    }

    private fun optimizeGameSettings(folderUri: Uri) {
        val success = GameOptimizer.applyPvPConfigWithSAF(this, folderUri)
        if (success) {
            Toast.makeText(this, "🚀 PvP FPS Boost Profile Successfully Applied!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "❌ Failed to write options.txt!", Toast.LENGTH_LONG).show()
        }
    }

    private fun hasFolderPermission(): Boolean = getSavedFolderUri() != null
    private fun saveFolderUri(uri: String) = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putString(KEY_FOLDER_URI, uri).apply()
    private fun getSavedFolderUri(): String? = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(KEY_FOLDER_URI, null)
}

@Composable
fun LauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF00FF66), 
            background = Color(0xFF121212), 
            surface = Color(0xFF1E1E1E)
        ),
        content = content
    )
}

@Composable
fun DashboardScreen(
    isFolderAuthorized: Boolean,
    onLaunchClick: () -> Unit,
    onOptimizeClick: () -> Unit,
    onRequestPermissionClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "POCKET LAUNCH", fontSize = 32.sp, color = MaterialTheme.colorScheme.primary)
            Text(text = "Bedrock Mobile Edition", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 48.dp))

            Button(
                onClick = onLaunchClick,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "LAUNCH MINECRAFT", color = Color.Black, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isFolderAuthorized) {
                Button(
                    onClick = onRequestPermissionClick,
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "GRANT MINECRAFT FOLDER ACCESS", color = Color.White)
                }
            } else {
                OutlinedButton(
                    onClick = onOptimizeClick,
                    modifier = Modifier.fillMaxWidth().height(55.dp)
                ) {
                    Text(text = "APPLY PVP FPS BOOST", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
