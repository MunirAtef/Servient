package com.munir_atef.pha_viewer.composables


import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.munir_atef.pha_viewer.composables.dialogs.ExitPresenterDialog
import com.munir_atef.pha_viewer.models.AppIconModel
import com.munir_atef.pha_viewer.hosted_file.HostedFileData
import com.munir_atef.pha_viewer.local_server.ServerObject
import com.munir_atef.pha_viewer.shared.InUseFile
import com.munir_atef.pha_viewer.shared.SharedData
import com.munir_atef.pha_viewer.shared.Zipper



@SuppressLint("SetJavaScriptEnabled")
@Composable
fun Presenter(navController: NavHostController) {
    val hostedFile: HostedFileData = InUseFile.hostedFileData ?: return
    var webView: WebView? by remember { mutableStateOf(null) }
    var showExitDialog by remember { mutableStateOf(false) }
    val icon: AppIconModel? = hostedFile.manifest.appIcon()
    val iconPath: String? = if (icon?.iconPath() != null) "${hostedFile.rootPath}${icon.iconPath()}" else null

    BackHandler(
        enabled = true,

        onBack = {
            if (webView?.canGoBack() == true) webView?.goBack()
            else showExitDialog = true
        },
    )


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (iconPath != null) Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape((20 * icon!!.borderRadiusRatio()).dp))
                            .background(icon.background())
                    ) {
                        Image(
                            bitmap = BitmapFactory.decodeFile(iconPath).asImageBitmap(),
                            contentDescription = "App Icon",
                            modifier = Modifier
                                .padding((20 * icon.paddingRatio()).dp)
                                .align(Alignment.Center)
                        )
                    }

                    Text(
                        text = hostedFile.manifest.appName(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W900,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                },
                backgroundColor = Color(0xFF000000),
                actions = {
                    IconButton(
                        onClick = { webView?.reload() }
                    ) {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = "refresh",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = { showExitDialog = true }
                    ) {
                        Icon(
                            Icons.Filled.Cancel,
                            contentDescription = "cancel",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) {
        DisposableEffect(Unit) {
            onDispose {
                ServerObject.endServer()
                webView?.stopLoading()
                webView?.destroy()
            }
        }


        if (showExitDialog) {
            ExitPresenterDialog(
                onDismiss = { showExitDialog = false },
                onSave = {
                    showExitDialog = false
                    navController.popBackStack()
                    val metadata = InUseFile.hostedFileData?.metadata ?: return@ExitPresenterDialog

                    metadata.writeFile(false)

                    Zipper.zipAll(
                        directory = InUseFile.hostedFileData?.rootPath!!,
                        zipFilePath = metadata.externalZipFile!!
                    )
                },
                onSaveAs = {
                    InUseFile.clear()
                    navController.popBackStack()
                },
                onCancel = {
                    InUseFile.clear()
                    navController.popBackStack()
                }
            )
        }


        AndroidView(
            factory = {
                webView = WebView(it)
                webView!!.apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    settings.apply {
                        javaScriptCanOpenWindowsAutomatically = true
                        allowContentAccess = true
                        allowFileAccess = true
                        javaScriptEnabled = true
                        userAgentString = SharedData.PRIVATE_AGENT
                    }

                    webViewClient = WebViewClient()
                    loadUrl("http://localhost:8080${hostedFile.manifest.launchFile()}")
                }
            },
        )
    }
}

