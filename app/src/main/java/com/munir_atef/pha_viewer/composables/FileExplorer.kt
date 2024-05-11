package com.munir_atef.pha_viewer.composables


import android.content.Context
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.munir_atef.pha_viewer.*
import com.munir_atef.pha_viewer.composables.dialogs.ConfirmPickingDialog
import com.munir_atef.pha_viewer.composables.dialogs.FileSaverDialog
import com.munir_atef.pha_viewer.hosted_file.HostedFileData
import com.munir_atef.pha_viewer.shared.SharedData
import com.munir_atef.pha_viewer.shared.Zipper
import com.munir_atef.pha_viewer.models.SavedFileModel
import com.munir_atef.pha_viewer.R.drawable
import com.munir_atef.pha_viewer.models.AppIconModel
import com.munir_atef.pha_viewer.models.ManifestModel
import com.munir_atef.pha_viewer.shared.InUseFile
import com.munir_atef.pha_viewer.shared.Routes
import java.io.File


class FileExplorerViewModel(private val navController: NavHostController, private val isToSave: Boolean): ViewModel() {
    private val root: String = SharedData.externalStoragePath
    private val currentPath: MutableState<String> = mutableStateOf(root)
    private val sortByName: MutableState<Boolean> = mutableStateOf(true)

    val currentPathList: MutableList<String> = mutableStateListOf("Internal storage")
    val sortListExpanded: MutableState<Boolean> = mutableStateOf(false)
    val sortListItems: List<String> = listOf("Sort by name", "Sort by last modified")
    var currentFiles: MutableState<List<File>> = mutableStateOf(listDirs(currentPath.value, sortByName.value))
    val isFabExpanded: MutableState<Boolean> = mutableStateOf(false)


    private fun listDirs(path: String, sortByName: Boolean): List<File> {
        val rootFile = File(path)
        println(path)

        val listedFiles: Array<File> = rootFile.listFiles { file ->
            file.isDirectory || file.extension == SharedData.EXTENSION
        } ?: emptyArray()

        val (dirs, files) =
            if (sortByName) listedFiles.sortedWith(compareBy { it.name.lowercase() }).partition { it.isDirectory }
            else listedFiles.sortedWith(compareBy { -it.lastModified() }).partition { it.isDirectory }

        return dirs + files
    }

    private fun dirsListToPath(pathList: MutableList<String>): String {
        var path = SharedData.externalStoragePath

        val size = pathList.size
        if (size == 1) return path
        for (i: Int in 1 until size)
            path += "/${pathList[i]}"
        return path
    }

    fun onBack() {
        when {
            isFabExpanded.value -> isFabExpanded.value = false
            currentPath.value == root -> navController.popBackStack()
            else -> {
                currentPath.value = currentPath.value.substring(0, currentPath.value.lastIndexOf("/"))
                currentPathList.removeLast()
            }
        }
    }

    fun backByClickOnDir(index: Int) {
        if (currentPathList.size == index + 1) return
        while (currentPathList.size > index + 1) currentPathList.removeLast()
        currentPath.value = dirsListToPath(currentPathList)
    }

    fun clickOnFileCard(Index: Int) {
        val file: File = currentFiles.value[Index]
        val isDirectory: Boolean = file.isDirectory

        if (isDirectory) {
            currentPath.value += "/${file.name}"
            currentPathList.add(file.name)
            currentFiles.value = listDirs(currentPath.value, sortByName.value)
        } else if (!isToSave) {
            val filePath = "${currentPath.value}/${file.name}"
            val isValid = Zipper.checkIsValid(filePath)

            if (!isValid) {
//                Toast.makeText(, "File may be corrupted", Toast.LENGTH_SHORT).show()
                return
            }

            navController.popBackStack()
        }
    }

    fun onResort(selectedItem: String) {
        sortByName.value = selectedItem == sortListItems[0]
        sortListExpanded.value = false
    }

    fun createNewFolder() {

    }

    fun saveAs() {

    }
}



@Deprecated("wait to be fixed")
@Composable
fun FileExplorer2(navController: NavHostController, isToSave: Boolean) {
    val viewModel = FileExplorerViewModel(navController, isToSave)

    var sortListExpanded by remember { viewModel.sortListExpanded }

    BackHandler(
        enabled = true,
        onBack = { viewModel.onBack() },
    )

    Scaffold(
        backgroundColor = Color.White,

        topBar = {
            DropdownMenu(
                expanded = sortListExpanded,
                onDismissRequest = { sortListExpanded = false },
                offset = DpOffset((-20).dp, 0.dp),
            ) {
                viewModel.sortListItems.forEach { item: String ->
                    DropdownMenuItem(
                        onClick = { viewModel.onResort(item) },
                    ) {
                        Text(text = item, fontWeight = FontWeight.W600, color = Color(0xFFFF00FF))
                    }
                }
            }

            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },

                            content = {
                                Icon(
                                    Icons.Filled.ArrowBack,
                                    tint = Color.White,
                                    contentDescription = "Back"
                                )
                            }
                        )

                        Text("File Explorer", color = Color.White)
                    }
                },

                actions = {
                    IconButton(
                        onClick = {
                            viewModel.sortListExpanded.value = true
                        },
                        content = {
                            Icon(
                                Icons.Filled.Sort,
                                tint = Color.White,
                                contentDescription = "Sort"
                            )
                        }
                    )
                },

                backgroundColor = Color(0xFF000000),
                modifier = Modifier.height(60.dp)
            )
        },

        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(40.dp),
                backgroundColor = Color(0xFFCC00CC),
                content = {
                    Text(
                        text = if (isToSave) "SAVE FILE" else "PICK FILE",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W600,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        },

        floatingActionButton = {
            if (isToSave) MultiFloatingActionButton(
                viewModel.isFabExpanded.value,
                onClick = {
                    viewModel.isFabExpanded.value = it
                },
                listOf(
                    FabItem(
                        icon = ImageBitmap.imageResource(id = drawable.new_folder),
                        label = "New Folder",
                        onClick = { viewModel.createNewFolder() }
                    ),
                    FabItem(
                        icon = ImageBitmap.imageResource(id = drawable.save),
                        label = "Save As",
                        onClick = { viewModel.saveAs() }
                    ),
                )
            )
        }
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState(), reverseScrolling = true)
                    .padding(top = (0.5).dp)
                    .background(Color(0xFF333333))
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                for (i: Int in 0 until viewModel.currentPathList.size) {
                    Text(
                        text = "/",
                        color = Color.White,
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp,
                    )

                    Text(
                        text = viewModel.currentPathList[i],
                        color = Color(0xFFFF00FF),
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .clickable { viewModel.backByClickOnDir(i) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.padding(bottom = 40.dp),

                content = {
                    items(viewModel.currentFiles.value.size) { i ->
                        val file: File = viewModel.currentFiles.value[i]
                        val isDirectory: Boolean = file.isDirectory

                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                            contentPadding = PaddingValues(start = 10.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),

                            onClick = { viewModel.clickOnFileCard(i) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .padding(1.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start,
                            ) {
                                if (isDirectory) Image(
                                    painterResource(drawable.dir_icon),
                                    contentDescription = "directory",
                                    modifier = Modifier.width(40.dp)
                                ) else Image(
                                    painterResource(drawable.file_icon),
                                    contentDescription = "file",
                                    modifier = Modifier.width(40.dp)
                                )

                                Text(
                                    text = file.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.W500,

                                    modifier = Modifier
                                        .padding(start = 5.dp)
                                        .horizontalScroll(rememberScrollState())
                                )
                            }
                        }
                    }
                }
            )
        }


        if (viewModel.isFabExpanded.value) Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x77CCCCCC))
                .clickable { viewModel.isFabExpanded.value = false }
        )
    }
}



fun isInvalidFileName(fileName: String): Boolean =
    Regex("[*:/\\\\?\n\"|<>]").containsMatchIn(fileName)


class PhaNameAndIcon(file: File) {
    var appName: String? = null
    var icon: AppIconModel? = null
    var iconImage: ByteArray? = null

    companion object {
        private val map: MutableMap<String, PhaNameAndIcon> = mutableMapOf()

        fun getPhaNameAndIcon(file: File): PhaNameAndIcon {
            var phaNameAndIcon = map[file.path]
            if (phaNameAndIcon == null) {
                phaNameAndIcon = PhaNameAndIcon(file)
                map[file.path] = phaNameAndIcon
            }
            return phaNameAndIcon
        }

        fun clear() { map.clear() }
    }

    init {
        val manifest: ByteArray? = Zipper.getFileFromZip(file, "manifest.json")

        if (manifest != null) {
            val manifestModel = ManifestModel(manifest)
            appName = manifestModel.appName()
            icon = manifestModel.appIcon()
            val iconPath: String? = manifestModel.appIcon()?.iconPath()?.removePrefix("/")
            iconImage = if (iconPath != null) Zipper.getFileFromZip(file, iconPath) else null
        }
    }
}


enum class DialogState { NO_DIALOG, SAVER_DIALOG, PICKER_DIALOG }

@Composable
fun FileExplorer(navController: NavHostController, isToSave: Boolean) {
    println("Re-build")

    val context: Context = LocalContext.current
    val root = SharedData.externalStoragePath

    var currentPath by remember { mutableStateOf(root) }
    var sortByName by remember { mutableStateOf(true) }
    val currentPathList: MutableList<String> = remember { mutableStateListOf("Internal storage") }

    val currentFiles: List<File> = listDirs(currentPath, sortByName)

    var sortListExpanded by remember { mutableStateOf(false) }
    var isFabExpanded by remember { mutableStateOf(false) }
    var forFile by remember { mutableStateOf(true) }

    var currentDialog: DialogState by remember { mutableStateOf(DialogState.NO_DIALOG) }

    var clickedFilePath: String? by remember { mutableStateOf(null) }
    var isLoading by remember { mutableStateOf(false) }


    val height: Dp by animateDpAsState(
        targetValue = if (sortListExpanded) 100.dp else 0.dp,
        animationSpec = tween(durationMillis = 500)
    )
    val size: Float by animateFloatAsState(
        targetValue = if (sortListExpanded) 1f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    BackHandler(
        enabled = true,

        onBack = {
            when {
                isFabExpanded -> isFabExpanded = false
                sortListExpanded -> sortListExpanded = false
                currentPath == root -> navController.popBackStack()
                else -> {
                    currentPath = currentPath.substring(0, currentPath.lastIndexOf("/"))
                    currentPathList.removeLast()
                }
            }
        },
    )


    Scaffold(
        backgroundColor = Color.White,

        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.offset((-10).dp, 0.dp)
                    ) {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            },
                            content = {
                                Icon(
                                    Icons.Filled.ArrowBack,
                                    tint = Color.White,
                                    contentDescription = "Back"
                                )
                            }
                        )

                        Text(
                            text = "FILE EXPLORER",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.W900,
                            fontFamily = FontFamily.Cursive,
                            color = Color.White,
                        )
                    }
                },

                actions = {
                    IconButton(
                        onClick = {
                            sortListExpanded = !sortListExpanded
                        },
                        content = {
                            Icon(
                                Icons.Filled.Sort,
                                tint = Color.White,
                                contentDescription = "Sort"
                            )
                        }
                    )
                },

                backgroundColor = Color(0xFF000000),
                modifier = Modifier.height(60.dp)
            )
        },

        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(40.dp),
                backgroundColor = Color(0xFFCC00CC),
                content = {
                    Text(
                        text = if (isToSave) "SAVE FILE" else "PICK FILE",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W600,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        },

        floatingActionButton = {
            if (isToSave) MultiFloatingActionButton(
                isFabExpanded,
                onClick = {
                    isFabExpanded = it
                },
                listOf(
                    FabItem(
                        icon = ImageBitmap.imageResource(id = drawable.new_folder),
                        label = "New Folder",
                        onClick = {
                            isFabExpanded = false
                            forFile = false
                            currentDialog = DialogState.SAVER_DIALOG
                        }
                    ),
                    FabItem(
                        icon = ImageBitmap.imageResource(id = drawable.save),
                        label = "Save As",
                        onClick = {
                            isFabExpanded = false
                            forFile = true
                            currentDialog = DialogState.SAVER_DIALOG
                        }
                    ),
                )
            )
        }
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState(), reverseScrolling = true)
                    .padding(top = (0.5).dp)
                    .background(Color(0xFF333333))
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                for (i: Int in 0 until currentPathList.size) {
                    Text(
                        text = "/",
                        color = Color.White,
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp,
                    )

                    Text(
                        text = currentPathList[i],
                        color = Color(0xFFFF00FF),
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .clickable {
                                if (currentPathList.size == i + 1) return@clickable
                                while (currentPathList.size > i + 1) currentPathList.removeLast()
                                currentPath = dirsListToPath(currentPathList)
                            }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.padding(bottom = 40.dp),

                content = {
                    items(currentFiles.size) { i ->
                        val file: File = currentFiles[i]

                        if (file.isDirectory) DirectoryCard(
                            file = file,
                            onClick = {
                                currentPath += "/${file.name}"
                                currentPathList.add(file.name)
                            }
                        ) else FileCard(
                            file = file,
                            onClick = {
                                if (!isToSave) {
                                    val filePath = "$currentPath/${file.name}"

                                    if (!it) {
                                        Toast.makeText(context, "File may be corrupted", Toast.LENGTH_SHORT).show()
                                        return@FileCard
                                    }

                                    clickedFilePath = filePath
                                    currentDialog = DialogState.PICKER_DIALOG
                                }
                            }
                        )
                    }
                }
            )
        }


        if (isFabExpanded) Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x44CCCCCC))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { isFabExpanded = false }
                )
        )
    }


    if (height.value > 2) Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(9.5.dp)
            .fillMaxHeight(size)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { sortListExpanded = false }
            ),
        contentAlignment = Alignment.TopEnd
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xEE444444))
                .border(
                    width = 2.dp,
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFF00FF)
                )
                .height(height)
                .width(180.dp)
        ) {
            Column {
                TextButton(
                    onClick = {
                        sortByName = true
                        sortListExpanded = false
                    },
                    modifier = Modifier
                        .width(180.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        backgroundColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(start = 10.dp)
                ) {
                    Text(
                        text = "Sort by name",
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Divider(color = Color.White, thickness = 0.5.dp)

                TextButton(
                    onClick = {
                        sortByName = false
                        sortListExpanded = false
                    },
                    modifier = Modifier
                        .width(180.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        backgroundColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(start = 10.dp)
                ) {
                    Text(
                        text = "Sort by last modified",
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }


    if (currentDialog == DialogState.SAVER_DIALOG) FileSaverDialog(
        forFile = forFile,
        onDismiss = { currentDialog = DialogState.NO_DIALOG },
        onConfirm = { fileName: String, warningOn: MutableState<Boolean> ->
            var dialogState = DialogState.NO_DIALOG
            var message: String? = null

            if (isInvalidFileName(fileName)) {
                message = "Invalid name"
                dialogState = DialogState.SAVER_DIALOG
            } else {
                val file = File("$currentPath/$fileName")
                if (!file.exists()) {
                    if (forFile) {
                        file.createNewFile()
                        message = "File created"
                        AppSettingsArgs.fileMetadata?.externalZipFile = "$currentPath/$fileName"
                        navController.popBackStack()
                    } else {
                        file.mkdir()
                        currentPath = "$currentPath/$fileName"
                        currentPathList.add(fileName)
                        message = "Directory created"
                    }
                } else {
                    if (forFile) {
                        println(warningOn.value)
                        if (warningOn.value) {
                            file.writeText("")
                            message = "File overwritten"
                            AppSettingsArgs.fileMetadata?.externalZipFile = "$currentPath/$fileName"
                            navController.popBackStack()
                        } else {
                            warningOn.value = true
                            dialogState = DialogState.SAVER_DIALOG
                        }
                    } else {
                        currentPath = "$currentPath/$fileName"
                        currentPathList.add(fileName)
                        message = "Directory already exists"
                    }
                }
            }

            if (message != null) Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            currentDialog = dialogState
        }
    )


    if (currentDialog == DialogState.PICKER_DIALOG) ConfirmPickingDialog(
        onDismiss = { if (!isLoading) currentDialog = DialogState.NO_DIALOG },

        onConfirm = {
            isLoading = true

            val unzippedFolderName = "hybrid-${System.currentTimeMillis()}"
            val dest = "${SharedData.rootForUnzipped}/$unzippedFolderName"
            Zipper.unzip(clickedFilePath!!, dest)

            val hostedFile = HostedFileData(dest)

            val fileMd: SavedFileModel = SavedFileModel().apply {
                appName = hostedFile.manifest.appName()
                folderName = unzippedFolderName
                externalZipFile = clickedFilePath
                lastOpened = 0
                appIcon = hostedFile.manifest.appIcon()
            }

            hostedFile.metadata = fileMd
            InUseFile.hostedFileData = hostedFile

            fileMd.writeFile(false)
            SavedFilesViewModel.notInitialized = true
            navController.popBackStack()
            navController.navigate(Routes.PREVIEW)
        }
    )
}


@Composable
fun DirectoryCard(file: File, onClick: () -> Unit) {
    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
        contentPadding = PaddingValues(start = 10.dp, end = 20.dp),

        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Image(
                painterResource(drawable.dir_icon),
                contentDescription = "directory",
                modifier = Modifier.width(40.dp)
            )

            Text(
                text = file.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.W600,

                modifier = Modifier
                    .padding(start = 5.dp)
                    .horizontalScroll(rememberScrollState())
            )
        }
    }
}

@Composable
fun FileCard(file: File, onClick: (Boolean) -> Unit) {
    val phaNameAndIcon = PhaNameAndIcon.getPhaNameAndIcon(file)
    val icon: AppIconModel? = phaNameAndIcon.icon
    val image: ByteArray? = phaNameAndIcon.iconImage
    val appName = phaNameAndIcon.appName

    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
        contentPadding = PaddingValues(start = 10.dp, end = 20.dp),

        onClick = { onClick(appName != null) },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {

            if (icon != null) Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape((20 * icon.borderRadiusRatio()).dp))
                    .background(icon.background())
            ) {
                if (image != null) Image(
                    bitmap = BitmapFactory.decodeByteArray(image, 0, image.size).asImageBitmap(),
                    contentDescription = "App Icon",
                    modifier = Modifier
                        .padding((20 * icon.paddingRatio()).dp)
                        .align(Alignment.Center)
                )
            }

            Column {
                Text(
                    text = file.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,

                    modifier = Modifier
                        .padding(start = 5.dp)
                        .horizontalScroll(rememberScrollState())
                )

                if (appName != null) Text(
                    text = appName,
                    color = Color(0xFFCC00CC),
                    fontWeight = FontWeight.W600,

                    modifier = Modifier
                        .padding(start = 5.dp)
                        .horizontalScroll(rememberScrollState())
                )
            }
        }
    }
}


fun listDirs(path: String, sortByName: Boolean): List<File> {
    val rootFile = File(path)
    println(path)

    val listedFiles: Array<File> = rootFile.listFiles { file ->
        file.isDirectory || file.extension == SharedData.EXTENSION
    } ?: emptyArray()

    val (dirs, files) =
        if (sortByName) listedFiles.sortedWith(compareBy { it.name.lowercase() }).partition { it.isDirectory }
        else listedFiles.sortedWith(compareBy { -it.lastModified() }).partition { it.isDirectory }

    return dirs + files
}

fun dirsListToPath(pathList: MutableList<String>): String {
    var path = SharedData.externalStoragePath

    val size = pathList.size
    if (size == 1) return path
    for (i: Int in 1 until size)
        path += "/${pathList[i]}"
    return path
}

