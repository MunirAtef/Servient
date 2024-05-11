package com.munir_atef.pha_viewer


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.munir_atef.pha_viewer.composables.*
import com.munir_atef.pha_viewer.composables.SplashScreen
import com.munir_atef.pha_viewer.shared.Routes
import com.munir_atef.pha_viewer.shared.SharedData



// WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
// window.setSoftInputMode(16)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController: NavHostController = rememberNavController()
            SharedData.rootForUnzipped = "${SharedData.externalStoragePath}/temp-pha-root"

            NavHost(navController, startDestination = Routes.SPLASH) {
                composable(Routes.SPLASH) { SplashScreen(navController) }
                composable(Routes.PICK_FILE) { FileExplorer(navController, false) }
                composable(Routes.SAVE_FILE) { FileExplorer(navController, true) }
                composable(Routes.PREVIEW) { AppPreview(navController) }
                composable(Routes.SAVED_FILES) { BrowseSavedFiles(navController) }
                composable(Routes.FILE_SETTINGS) { AppSettings(navController) }
                composable(Routes.WEB_VIEW) { Presenter(navController) }
            }
        }
    }
}
