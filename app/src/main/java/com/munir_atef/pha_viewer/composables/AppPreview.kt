package com.munir_atef.pha_viewer.composables

import android.content.Context
import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.munir_atef.pha_viewer.R
import com.munir_atef.pha_viewer.models.AppIconModel
import com.munir_atef.pha_viewer.local_server.ServerObject
import com.munir_atef.pha_viewer.service.ProvidedServices
import com.munir_atef.pha_viewer.service.ServiceDescription
import com.munir_atef.pha_viewer.shared.InUseFile
import com.munir_atef.pha_viewer.shared.Routes


@Preview(showBackground = true)
@Composable
fun AppPreviewPref() {
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
                                    tint = Color.White,
                                    contentDescription = "Back"
                                )
                            },
                            onClick = {}
                        )

                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                                .clip(RoundedCornerShape(20 * 0.1f))
                                .background(Color.White)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.file_icon),
                                contentDescription = "App Icon",
                                modifier = Modifier
                                    .padding((20 * 0.1f).dp)
                                    .align(Alignment.Center)
                            )
                        }


                        Text(
                            text = "App Name",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.W900,
                            fontFamily = FontFamily.Cursive,
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
                            try {

                            } catch (e: Exception) {
                                println(e.message)
                                println(e)
                            }
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
                        Text(text = "START HOSTING")
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
                    .height(40.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Auto start at next time",
                    fontWeight = FontWeight.W600
                )

                Switch(
                    checked = true,
                    onCheckedChange = { },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFFFF00FF),
                        uncheckedTrackColor = Color.Gray
                    )
                )
            }

            Divider()

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxHeight()
            ) {

                Box(modifier = Modifier.height(60.dp))
            }
        }
    }
}


@Composable
fun AppPreview(navController: NavHostController) {
    val hostedFile = InUseFile.hostedFileData ?: return

    val context: Context = LocalContext.current
    val name: String = hostedFile.manifest.appName()
    val permissions: List<String>? = hostedFile.manifest.permissions()
    val icon: AppIconModel? = hostedFile.manifest.appIcon()
    val iconPath: String? = if (icon?.iconPath() != null) "${hostedFile.rootPath}${icon.iconPath()}" else null
    val hasPermissions = permissions != null && permissions.isNotEmpty()


    val permissionsToBeAsked: MutableSet<ServiceDescription> = mutableSetOf()
    val missingPermissions: MutableSet<String> = mutableSetOf()

    val fileMetadata = hostedFile.metadata

    hostedFile.agreedPermissions = hostedFile.metadata.grantedPermission

    permissions?.forEach {
        val serviceDescription: ServiceDescription? = ProvidedServices.services[it]
        when {
            serviceDescription == null -> missingPermissions.add(it)
            serviceDescription.autoGranted -> hostedFile.agreedPermissions.add(it)
            else -> permissionsToBeAsked.add(serviceDescription)
        }
    }

    var autoStart: Boolean by  remember { mutableStateOf(false) }

    BackHandler(
        enabled = true,
        onBack = {
            InUseFile.clear()
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
                                    tint = Color.White,
                                    contentDescription = "Back"
                                )
                            },
                            onClick = { navController.popBackStack() }
                        )

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
                            try {
                                fileMetadata.autoStart = autoStart
                                fileMetadata.writeFile(true)

                                ServerObject.initialServer(context, hostedFile)

                                navController.popBackStack()
                                navController.navigate(Routes.WEB_VIEW)
                                SavedFilesViewModel.notInitialized = true
                            } catch (e: Exception) {
                                println(e.message)
                                println(e)
                            }
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
                        Text(text = "START HOSTING")
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
                    .height(40.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Auto start at next time",
                    fontWeight = FontWeight.W600
                )

                Switch(
                    checked = autoStart,
                    onCheckedChange = { autoStart = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFFFF00FF),
                        uncheckedTrackColor = Color.Gray
                    )
                )
            }

            Divider()

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
                    Image(
                        painter = painterResource(R.drawable.permissions),
                        contentDescription = "permissions",
                        modifier = Modifier
                            .padding(horizontal = 60.dp)
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


@Composable
fun NeededPermission(permission: ServiceDescription, grantedPermissions: MutableSet<String>) {
    var messageVisible: Boolean by remember { mutableStateOf(false) }
    val active: MutableState<Boolean> = remember { mutableStateOf(grantedPermissions.contains(permission.endPoint)) }
    println(grantedPermissions)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = permission.visibleName,
                fontSize = 16.sp,
                fontWeight = FontWeight.W600
            )

            Checkbox(
                checked = active.value,
                onCheckedChange = {
                    active.value = it

                    if (it) grantedPermissions.add(permission.endPoint)
                    else grantedPermissions.remove(permission.endPoint)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFFFF00FF),
                    uncheckedColor = Color.Gray
                )
            )
        }

        TextButton(
            onClick = { messageVisible = !messageVisible },
            modifier = Modifier.padding(start = 20.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (messageVisible) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                    contentDescription = ""
                )
                Text(text = "ABOUT")
            }
        }

        if (messageVisible) Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFEEEEEE))
//                .border(
//                    width = 2.dp,
//                    shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp),
//                    color = Color(0xFFFF00FF)
//                )
                .padding(10.dp)
        ) {
            Text(
                text = permission.description,
                fontWeight = FontWeight.W600
            )
        }

        Box(modifier = Modifier.height(5.dp))
        Divider()
        Box(modifier = Modifier.height(20.dp))
    }
}

