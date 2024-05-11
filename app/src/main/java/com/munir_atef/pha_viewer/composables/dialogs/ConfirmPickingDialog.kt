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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties



@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ConfirmPickingDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    var isLoading: Boolean by remember { mutableStateOf(false) }

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
                    text = "PICKING FILE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W600,
                    color = Color.White
                )

                Text(
                    text = "Confirm picking the file",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    color = Color(0xFFFF00FF),
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth(0.9f),
                )

                Button(
                    onClick = {
                        if (!isLoading) {
                            isLoading = true
                            onConfirm()
                        }
                    },

                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, top = 10.dp)
                        .fillMaxWidth(0.9f)
                        .height(40.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (isLoading) Color.Gray else Color(0xFFFF00FF),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(0.2f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier
                                    .height(20.dp)
                                    .width(20.dp)
                            )
                        }

                        Text(
                            text = "CONFIRM",
                            fontWeight = FontWeight.W600,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(0.75f)
                        )

                        Spacer(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

