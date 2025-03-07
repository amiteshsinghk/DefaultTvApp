package com.amitesh.defaulttvapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.tv.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.amitesh.defaulttvapplication.ui.theme.DefaultTvApplicationTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Log.d("MainActivity","onCreate Event")
            val context = LocalContext.current
            requestOverlayPermission(context)
            requestBatteryOptimizationExemption(context)
            whitelistAppFromDoze(context)
            promptUserToSetDefaultLauncher(context)

            // Check if boot service should start

            DefaultTvApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    Greeting("Android")
                }
            }
        }
        Log.d("MainActivity", "App Launched Successfully")

//        // Check if this app is the default launcher
//        if (!isDefaultLauncher(this)) {
//            Log.d("MainActivity", "App is NOT the default launcher, prompting user...")
//            setDefaultLauncher(this)
//        } else {
//            Log.d("MainActivity", "App is already the default launcher.")
//        }
    }
}



private fun requestOverlayPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
        context.startActivity(intent)
        Log.d("MainActivity", "Requested SYSTEM_ALERT_WINDOW permission")
    }
}

private fun requestBatteryOptimizationExemption(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        context.startActivity(intent)
        Log.d("MainActivity", "Requested IGNORE_BATTERY_OPTIMIZATIONS permission")
    }
}

private fun whitelistAppFromDoze(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse("package:${context.packageName}")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            Log.d("MainActivity", "Requested Doze Mode Whitelist")
        }
    }
}

private fun promptUserToSetDefaultLauncher(context: Context) {
    if (!isDefaultLauncher(context)) {
        val intent = Intent(Settings.ACTION_HOME_SETTINGS)
        context.startActivity(intent)
        Log.d("MainActivity", "Prompting user to set app as Default Launcher")
    }
}

private fun startSystemAlertWindowPermission(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                Log.i("MainActivity", "[startSystemAlertWindowPermission] Requesting system alert window permission.")
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                context.startActivity(intent)
            }
        }
    } catch (e: Exception) {
        Log.e("MainActivity", "[startSystemAlertWindowPermission] Error:", e)
    }
}


private fun setDefaultLauncher(activity: Activity) {
    try {
        val intent = Intent("android.settings.HOME_SETTINGS")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(intent)
        Log.d("MainActivity", "Opened Home Settings for launcher selection")
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("MainActivity", "Error opening Home Settings: ${e.message}")
    }
}


// Function to check if the app is the default launcher
private fun isDefaultLauncher(context: Context): Boolean {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_HOME)
    val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
    return resolveInfo?.activityInfo?.packageName == context.packageName
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DefaultTvApplicationTheme {
        Greeting("Android")
    }
}