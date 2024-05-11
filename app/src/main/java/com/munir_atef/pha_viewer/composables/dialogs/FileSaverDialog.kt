package com.munir_atef.pha_viewer.composables.dialogs


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.munir_atef.pha_viewer.shared.SharedData


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FileSaverDialog(
    forFile: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (fileName: String, warningOn: MutableState<Boolean>) -> Unit
) {
    var fileName: String by remember { mutableStateOf("") }
    val warningOn: MutableState<Boolean> = remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val textFieldShape: RoundedCornerShape =
        if (forFile) RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
        else RoundedCornerShape(10.dp)

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 30.dp)
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(10.dp))
                .border(
                    width = 2.dp,
                    color = Color(0xFFFF00FF),
                    shape = RoundedCornerShape(10.dp)
                )
                .background(Color(0xFF222222))
                .padding(vertical = 20.dp, horizontal = 10.dp),

            elevation = 0.dp,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.background(Color(0xFF222222))
            ) {
                Text(
                    text = if (forFile) "CREATING FILE" else "CREATING DIRECTORY",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W600,
                    color = Color.White
                )

                Row {
                    TextField(
                        value = fileName,
                        onValueChange = { fileName = it },
                        shape = textFieldShape,
                        singleLine = true,
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .fillMaxWidth(if (forFile) 0.75f else 0.9f)
                            .height(50.dp)
                            .focusRequester(focusRequester),

                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color(0xFFCCCCCC),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color(0xFFFF00FF)
                        ),
                        textStyle = TextStyle(fontWeight = FontWeight.W600),
                        placeholder = {
                            Text(text = if (forFile) "File name" else "Directory name")
                        }
                    )

                    if (forFile) Box(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .height(50.dp)
                            .width(50.dp)
                            .clip(RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp))
                            .background(Color(0xFF009900)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = SharedData.EXTENSION, fontWeight = FontWeight.W600, color = Color.White)
                    }
                }

                if (warningOn.value) Text(
                    text = "The file already exists.",
                    fontWeight = FontWeight.W600,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 0.dp)
                )

                Button(
                    onClick = {
                        var name = fileName.trim()
                        if (forFile) name += ".${SharedData.EXTENSION}"
                        onConfirm(name, warningOn)
                    },
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, top = 10.dp)
                        .fillMaxWidth(0.9f)
                        .height(40.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFFF00FF),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = if (warningOn.value) "OVERWRITE" else "CONFIRM")
                }
            }
        }
    }
}






