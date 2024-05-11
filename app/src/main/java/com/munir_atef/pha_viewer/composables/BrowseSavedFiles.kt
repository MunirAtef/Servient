package com.munir_atef.pha_viewer.composables

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.munir_atef.pha_viewer.hosted_file.HostedFileData
import com.munir_atef.pha_viewer.local_server.ServerObject
import com.munir_atef.pha_viewer.shared.InUseFile
import com.munir_atef.pha_viewer.shared.Routes
import com.munir_atef.pha_viewer.shared.SharedData
import com.munir_atef.pha_viewer.models.SavedFileModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


data class SavedFile(val file: File) {
    val fileModel: SavedFileModel = SavedFileModel(file.readText())
}

class SavedFilesViewModel(private val navController: NavHostController): ViewModel() {
    companion object {
        var savedFiles: MutableList<SavedFile> = mutableListOf()
        var size: MutableState<Int> = mutableStateOf(0)
        var notInitialized: Boolean = true
    }

    init {
        if (notInitialized) {
            notInitialized = false
            println("Initialized")

            val listedFiles = File("${SharedData.rootForUnzipped}/data").listFiles {
                file -> file.extension == "json" } ?: emptyArray()

            savedFiles.clear()
            listedFiles.forEach { if (it != null) savedFiles.add(SavedFile(it)) }

            savedFiles.apply { sortBy { -it.fileModel.lastOpened } }
            size.value = savedFiles.size
        }
    }

    private val expandedIndex: MutableState<Int> = mutableStateOf(-1)
    private val dateFormatter = SimpleDateFormat("dd MMM yyyy  HH:mm", Locale.ENGLISH)

    fun deleteFile(index: Int) {
        try {
            val savedFile: SavedFile = savedFiles.removeAt(index)
            size.value--
            savedFile.file.delete()
            val folderPath = "${SharedData.rootForUnzipped}/${savedFile.fileModel.folderName}"
            File(folderPath).deleteRecursively()

            expandedIndex.value = -1
        } catch (e: Exception) {
            println(e)
        }
    }

    fun openFile(index: Int, context: Context) {
        val fileMetadata: SavedFileModel = savedFiles[index].fileModel
        expandedIndex.value = -1
        val folderPath = "${SharedData.rootForUnzipped}/${fileMetadata.folderName}"
        val hostedFileData: HostedFileData = HostedFileData(folderPath).apply {
            metadata = fileMetadata
            agreedPermissions = metadata.grantedPermission
        }
        InUseFile.hostedFileData = hostedFileData

        println(fileMetadata.toJsonString())

        if (fileMetadata.autoStart) {
            fileMetadata.writeFile(true)
            ServerObject.initialServer(context, hostedFileData)
            navController.navigate(Routes.WEB_VIEW)
        } else navController.navigate(Routes.PREVIEW)
        notInitialized = true
    }

    fun fileSettings(index: Int) {
        val fileMetadata: SavedFileModel = savedFiles[index].fileModel
        expandedIndex.value = -1
        val folderPath = "${SharedData.rootForUnzipped}/${fileMetadata.folderName}"

        AppSettingsArgs.apply {
            this.rootPath = folderPath
            this.fileMetadata = fileMetadata
        }

        expandedIndex.value = -1
        navController.navigate(Routes.FILE_SETTINGS)
    }

    fun onFileCardClicked(index: Int) {
        expandedIndex.value = if (expandedIndex.value == index) -1 else index
    }

    fun isActive(index: Int): Boolean = expandedIndex.value == index

    fun getDate(timestamp: Long): String? {
        if (timestamp.compareTo(0) == 0) return null
        return dateFormatter.format(timestamp)
    }
}


@Composable
fun BrowseSavedFiles(navController: NavHostController) {
    println("re-build")
    val viewModel = SavedFilesViewModel(navController)
    val context = LocalContext.current
    val subColor = Color(0XFF990000)


    ScreenBackground(
        title = "PHA VIEWER",
        color = subColor,
        action = {
            Checkbox(
                checked = InUseFile.useKtorServer.value,
                onCheckedChange = {
                    InUseFile.useKtorServer.value = !InUseFile.useKtorServer.value
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF000077),
                    uncheckedColor = Color.White
                )
            )
        },

        floatingActionButton = {
            Button(
                onClick = {
                    PhaNameAndIcon.clear()
                    navController.navigate(Routes.PICK_FILE)
                },
                modifier = Modifier.shadow(elevation = 5.dp, shape = CircleShape),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(backgroundColor = subColor)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "add",
                        tint = Color.White
                    )

                    Text(
                        text = "PICK PHA",
                        color = Color.White,
                        fontWeight = FontWeight.W600
                    )
                }
            }
        },

        content = {
            if (SavedFilesViewModel.size.value == 0) Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Save Files",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W600,
                    color = Color.Red
                )
            }


            LazyColumn(
                content = {
                    items(SavedFilesViewModel.size.value) { i: Int ->
                        val isActive = viewModel.isActive(i)
                        val fileMd = SavedFilesViewModel.savedFiles[i].fileModel

                        val icon = fileMd.appIcon
                        val folderPath = "${SharedData.rootForUnzipped}/${fileMd.folderName}"
                        val date: String? = viewModel.getDate(fileMd.lastOpened)

                        val expand: Dp by animateDpAsState(
                            targetValue = if (isActive) 124.dp else 0.dp,
                            animationSpec = tween(durationMillis = 350)
                        )

                        if (fileMd.appName != null || fileMd.folderName != null) Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                            contentPadding = PaddingValues(horizontal = 15.dp),
                            shape = RoundedCornerShape(10.dp),
                            onClick = { viewModel.onFileCardClicked(i) },

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp, vertical = 5.dp)
                                .shadow(elevation = 10.dp, shape = RoundedCornerShape(10.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 7.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (icon != null) Box(
                                        modifier = Modifier
                                            .width(40.dp)
                                            .height(40.dp)
                                            .clip(RoundedCornerShape((20 * icon.borderRadiusRatio()).dp))
                                            .background(icon.background())
                                    ) {
                                        if (File(folderPath + icon.iconPath()).exists()) Image(
                                            bitmap = BitmapFactory
                                                .decodeFile(folderPath + icon.iconPath())
                                                .asImageBitmap(),
                                            contentDescription = "App Icon",
                                            modifier = Modifier
                                                .padding((20 * icon.paddingRatio()).dp)
                                                .align(Alignment.Center)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = fileMd.appName!!,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.W700,

                                            modifier = Modifier
                                                .padding(start = 5.dp)
                                                .horizontalScroll(rememberScrollState())
                                        )

                                        if (date != null) Text(
                                            text = "$date",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(start = 5.dp, top = 3.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.fillMaxWidth(0.9f))

                                    Icon(
                                        imageVector = Icons.Filled.ArrowBackIosNew,
                                        contentDescription = "ArrowBackIosNew",
                                        tint = Color.Gray,
                                        modifier = Modifier
                                            .width(15.dp)
                                            .height(15.dp)
                                            .rotate(if (isActive) 270f else 180f)
                                    )
                                }

                                if (isActive) Column(
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                        .height(expand)
                                        .clip(RoundedCornerShape(5.dp))
                                ) {
                                    Button(
                                        onClick = { viewModel.openFile(i, context) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(40.dp)
                                            .background(
                                                brush = Brush.linearGradient(
                                                    colors = listOf(Color(0xFF000066), subColor)
                                                )
                                            ),
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = Color.Transparent,
                                            contentColor = Color.White
                                        ),
                                        shape = RectangleShape
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()

                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.OpenInNew,
                                                contentDescription = "open"
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(text = "OPEN", fontWeight = FontWeight.W600)
                                        }
                                    }

                                    Divider(thickness = 2.dp)

                                    Button(
                                        onClick = { viewModel.fileSettings(i) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(40.dp)
                                            .background(
                                                brush = Brush.linearGradient(
                                                    colors = listOf(Color(0xFF000066), subColor)
                                                )
                                            ),
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = Color.Transparent,
                                            contentColor = Color.White
                                        ),
                                        shape = RectangleShape
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Settings,
                                                contentDescription = "settings"
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "SETTINGS",
                                                fontWeight = FontWeight.W600
                                            )
                                        }
                                    }

                                    Divider(thickness = 2.dp)

                                    Button(
                                        onClick = { viewModel.deleteFile(i) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(40.dp)
                                            .background(
                                                brush = Brush.linearGradient(
                                                    colors = listOf(Color(0xFF000066), subColor)
                                                )
                                            ),
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = Color.Transparent,
                                            contentColor = Color.White
                                        ),
                                        shape = RectangleShape
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                Icons.Filled.Delete,
                                                contentDescription = "delete"
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(text = "DELETE", fontWeight = FontWeight.W600)
                                        }
                                    }
                                }
                            }
                        }
                    }
                },

                modifier = Modifier.padding(top = 10.dp)
            )
        }
    )
}

