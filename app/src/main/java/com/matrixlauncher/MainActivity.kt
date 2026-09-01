package com.matrixlauncher

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.matrixlauncher.ui.MatrixLauncherApp
import com.matrixlauncher.ui.mvi.LauncherIntent
import com.matrixlauncher.ui.viewmodel.LauncherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    private lateinit var defaultLauncherRequest: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        defaultLauncherRequest = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            viewModel.onIntent(LauncherIntent.RefreshApps)
        }

        setContent {
            MatrixLauncherApp(
                viewModel = viewModel,
                onRequestSetDefaultLauncher = { requestDefaultLauncher() }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onIntent(LauncherIntent.RefreshApps)
    }

    private fun requestDefaultLauncher() {
        // Method 1: Android 10+ (API 29+) RoleManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(Context.ROLE_SERVICE) as? RoleManager
            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
                if (!roleManager.isRoleHeld(RoleManager.ROLE_HOME)) {
                    try {
                        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
                        defaultLauncherRequest.launch(intent)
                        return
                    } catch (e: Exception) {
                        // Fall through to fallback intents
                    }
                } else {
                    Toast.makeText(this, "MatrixLauncher is already your default launcher", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }

        // Method 2: Android Default Apps Settings
        try {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
            } else {
                Intent(Settings.ACTION_HOME_SETTINGS)
            }
            startActivity(intent)
            return
        } catch (e: Exception) {
            // Fall through
        }

        // Method 3: Home Settings Direct
        try {
            val intent = Intent(Settings.ACTION_HOME_SETTINGS)
            startActivity(intent)
            return
        } catch (e: Exception) {
            // Fall through
        }

        // Method 4: Generic System Home Settings (Xiaomi / Oppo / Vivo / Huawei)
        try {
            val intent = Intent("android.settings.HOME_SETTINGS")
            startActivity(intent)
            return
        } catch (e: Exception) {
            // Fall through
        }

        // Method 5: Application Details Settings Fallback
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
            Toast.makeText(this, "Open 'Home app' or 'Default app' to set MatrixLauncher", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            val intent = Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
        }
    }
}
