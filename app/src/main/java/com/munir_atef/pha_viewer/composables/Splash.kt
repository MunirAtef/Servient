package com.munir_atef.pha_viewer.composables

import android.Manifest
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.munir_atef.pha_viewer.R
import com.munir_atef.pha_viewer.shared.Routes
import kotlinx.coroutines.delay


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SplashScreen(navController: NavHostController) {
    println("hi rebuild")
    val writePermissionState: PermissionState =
        rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)

    var hasPermission by remember { mutableStateOf(writePermissionState.hasPermission) }
    var startAnimation: Boolean by remember { mutableStateOf(false) }

    val devName = "Munir M. Atef"
    val devNameLength = devName.length


    LaunchedEffect(Unit) {
        println("hi launch effect")
        delay(3000)
        if (hasPermission) navController.navigate(Routes.SAVED_FILES) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        } else {
            writePermissionState.launchPermissionRequest()
            while (true) {
                println("repeat: $hasPermission")
                if (writePermissionState.hasPermission) {
                    println("stopped")
                    hasPermission = true
                    navController.navigate(Routes.SAVED_FILES) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }

                delay(500)
            }
        }
    }

    val subNameLength: Int by animateIntAsState(
        targetValue = if (startAnimation) devNameLength else 1,
        animationSpec = tween(durationMillis = 2500)
    )
    val opacity: Float by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000)
    )

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF000066), Color(0xFF990000))
                    )
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (!startAnimation) startAnimation = true

                    Image(
                        painter = painterResource(R.drawable.pha_logo_gradient),
                        contentDescription = "pha logo",
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(top = 50.dp, bottom = 20.dp)
                            .alpha(opacity)
                            .clip(RoundedCornerShape(40.dp))
                            .background(Color(0xFFFFFFFF))
                            .padding(10.dp)
                    )

                    Text(
                        text = "PHA VIEWER",
                        fontWeight = FontWeight.W900,
                        fontSize = 25.sp,
                        fontFamily = FontFamily.Cursive,
                        color = Color.White
                    )
                }


                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "DEVELOPED BY",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W600,
                    color = Color.White
                )

                Text(
                    text = devName.substring(0, subNameLength),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W900,
                    fontFamily = FontFamily.Cursive,
                    color = Color.White,
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 60.dp)
                        .shadow(10.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(Color.DarkGray)
                        .padding(horizontal = 30.dp, vertical = 10.dp)
                )
            }
        }
    }
}



