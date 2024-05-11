package com.munir_atef.pha_viewer.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ScreenBackground(
    title: String,
    color: Color,
    content: @Composable (BoxScope.() -> Unit),
    floatingActionButton: @Composable () -> Unit = {},
    action: @Composable (BoxScope.() -> Unit) = {}
) {
    Scaffold(
        floatingActionButton = floatingActionButton
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF000066), color, color)
                    )
                ),
        ) {
            Row {
                Box(modifier = Modifier.width(60.dp)) {}

                Text(
                    text = title,
                    fontFamily = FontFamily.Cursive,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.W900,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .fillMaxWidth()
                        .weight(1f)
                )

                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(60.dp),
                    contentAlignment = Alignment.Center,
                    content = action
                )
            }

            Box(
                modifier = Modifier
                    .padding(top = 60.dp, start = 10.dp, end = 10.dp, bottom = 10.dp)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
                    .background(Color.White),

                content = content
            )
        }
    }
}

