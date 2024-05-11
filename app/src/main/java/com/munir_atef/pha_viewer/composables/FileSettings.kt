package com.munir_atef.pha_viewer.composables


import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.munir_atef.pha_viewer.R
import com.munir_atef.pha_viewer.models.AppIconModel
import com.munir_atef.pha_viewer.hosted_file.HostedFileData
import com.munir_atef.pha_viewer.service.ProvidedServices
import com.munir_atef.pha_viewer.service.ServiceDescription
import com.munir_atef.pha_viewer.shared.Routes
import com.munir_atef.pha_viewer.shared.SharedData
import com.munir_atef.pha_viewer.shared.Zipper
import com.munir_atef.pha_viewer.models.SavedFileModel
import java.io.File


object AppSettingsArgs {
    var fileMetadata: SavedFileModel? = null
    var rootPath: String? = null

    fun clear() {
        fileMetadata = null
        rootPath = null
    }
}


@Composable
fun AppSettings(navController: NavHostController) {
    val fileMetadata = AppSettingsArgs.fileMetadata
    val rootPath = AppSettingsArgs.rootPath
    if (fileMetadata == null || rootPath == null) return

    val hostedFile = HostedFileData(rootPath)
    val name: String = hostedFile.manifest.appName()
    val permissions: List<String>? = hostedFile.manifest.permissions()
    val icon: AppIconModel? = hostedFile.manifest.appIcon()
    val iconPath: String? = if (icon?.iconPath() != null) "${hostedFile.rootPath}${icon.iconPath()}" else null
    val hasPermissions = permissions != null && permissions.isNotEmpty()
    val externalPath: String? by remember { mutableStateOf(fileMetadata.externalZipFile) }
    hostedFile.agreedPermissions = fileMetadata.grantedPermission


    val permissionsToBeAsked: MutableSet<ServiceDescription> = mutableSetOf()
    val missingPermissions: MutableSet<String> = mutableSetOf()

    permissions?.forEach {
        val serviceDescription: ServiceDescription? = ProvidedServices.services[it]
        when {
            serviceDescription == null -> missingPermissions.add(it)
            serviceDescription.autoGranted -> hostedFile.agreedPermissions.add(it)
            else -> permissionsToBeAsked.add(serviceDescription)
        }
    }

    val unzippedFolderName = rootPath.split("/").last()
    val jsonFilePath = "${SharedData.rootForUnzipped}/data/$unzippedFolderName.json"
    var autoStart: Boolean by  remember { mutableStateOf(fileMetadata.autoStart) }


    BackHandler(
        enabled = true,
        onBack =  {
            AppSettingsArgs.clear()
            navController.popBackStack()
        }
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.offset((-10).dp, 0.dp)
                    ) {
                        IconButton(
                            content = {
                                Icon(
                                    Icons.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            },
                            onClick = {
                                AppSettingsArgs.clear()
                                navController.popBackStack()
                            }
                        )

                        if (iconPath != null)
                            Box(
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
                            text = name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W900,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                },
                backgroundColor = Color(0xFF000000),
                modifier = Modifier.height(65.dp)
            )
        },

        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(70.dp),
                backgroundColor = Color.White,
                contentPadding = PaddingValues(bottom = 20.dp, start = 30.dp, end = 30.dp),
                content = {
                    Button(
                        onClick = {
                            fileMetadata.grantedPermission = hostedFile.agreedPermissions
                            fileMetadata.autoStart = autoStart
                            File(jsonFilePath).writeText(fileMetadata.toJsonString())

                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFCC00CC),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        shape = CircleShape
                    ) {
                        Text(text = "SAVE CHANGES")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .background(Color(0xFFEEEEEE))
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Auto start",
                    fontWeight = FontWeight.W600,
                    color = Color.Black,
                    fontSize = 16.sp
                )

                Switch(
                    checked = autoStart,
                    onCheckedChange = { autoStart = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFF00FF))
                )
            }

            Divider(color = Color.Black)

            Column(modifier = Modifier.background(Color(0xFFEEEEEE))) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "External Path",
                        fontWeight = FontWeight.W600,
                        color = Color.Black,
                        fontSize = 16.sp
                    )

                    TextButton(
                        onClick = { navController.navigate(Routes.SAVE_FILE) }
                    ) {
                        Text(
                            text = "CHANGE",
                            fontWeight = FontWeight.W600,
                            color = Color.Red
                        )
                    }
                }

                Text(
                    text = externalPath ?: "Not specified",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                        .background(Color(0xFF222222))
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .horizontalScroll(rememberScrollState())
                )

                Row(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Button(
                        onClick = {
                            fileMetadata.writeFile(false)

                            Zipper.zipAll(
                                directory = rootPath,
                                zipFilePath = fileMetadata.externalZipFile!!
                            )
                        },
                        modifier = Modifier
                            .height(35.dp)
                            .padding(end = (0.5).dp)
                            .fillMaxWidth(0.5f),

                        shape = RoundedCornerShape(bottomStart = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFFF00FF),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "SAVE", fontWeight = FontWeight.W600, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {},
                        modifier = Modifier
                            .height(35.dp)
                            .padding(start = (0.5).dp)
                            .fillMaxWidth(),

                        shape = RoundedCornerShape(bottomEnd = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFAA00AA),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "SAVE AS", fontWeight = FontWeight.W600, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            Divider(color = Color.Black)

            if (hasPermissions) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    text = "Needed Permissions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    textAlign = TextAlign.Left,
                    color = Color(0xFFEE0000)
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
//                    val width: Dp = getScreenWidth()
                    Image(
                        painter = painterResource(R.drawable.permissions),
                        contentDescription = "permissions",
                        modifier = Modifier
                            .padding(horizontal = 60.dp)
//                            .width(width - 120.dp)
//                            .height(width - 120.dp)
                            .fillMaxSize()
                            .padding(horizontal = 30.dp)
                    )
                    Text(
                        text = "No Permissions Required",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.W600,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF00AA00)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxHeight()
            ) {
                if (hasPermissions)
                    for (permission in permissionsToBeAsked)
                        NeededPermission(permission, hostedFile.agreedPermissions)

                Box(modifier = Modifier.height(60.dp))
            }
        }
    }
}

