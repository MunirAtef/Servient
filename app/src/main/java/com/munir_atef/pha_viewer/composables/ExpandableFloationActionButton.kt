package com.munir_atef.pha_viewer.composables


import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


data class FabItem(val icon: ImageBitmap, val label: String, val onClick: () -> Unit)

@Composable
fun MultiFloatingActionButton(isExpanded: Boolean, onClick: (Boolean) -> Unit, fabItems: List<FabItem>) {
    val transition = updateTransition(targetState = isExpanded, label = "transition")

    val rotate by transition.animateFloat(label = "rotate") {
        if (it) 135f else 0f  // 315, 45
    }
    val fabScale by transition.animateFloat(label = "fabScale") {
        if (it) 60f else 0f
    }
    val alpha by transition.animateFloat(label = "alpha", transitionSpec = { tween(durationMillis = 50) }) {
        if (it) 1f else 0f
    }

    Column(
        horizontalAlignment = Alignment.End
    ) {
        if (isExpanded) {
            fabItems.forEach {
                SubFab(
                    fabItem = it,
                    alpha = alpha,
                    scale = fabScale
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }


        FloatingActionButton(
            onClick = { onClick(!transition.currentState) },
            modifier = Modifier.rotate(rotate),
            backgroundColor = Color(0xFFFF00FF)
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "add",
                tint = Color.White
            )
        }
    }
}

@Composable
fun SubFab(fabItem: FabItem, alpha: Float, scale: Float) {
    val shadow = Color.Black.copy(0.5f)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .clickable { fabItem.onClick.invoke() }
            .padding(vertical = 10.dp, horizontal = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0xFF222222))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = fabItem.label,
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                color = Color.White
            )
        }

        Canvas(
            modifier = Modifier
                .size(32.dp)
                .padding(start = 20.dp)
        ) {
            drawCircle(
                color = shadow,
                radius = scale,
                center = Offset(
                    center.x + 2f,
                    center.y + 2f
                )
            )

            drawCircle(
                color = Color(0xFFFF00FF),
                radius = scale
            )

            drawImage(
                image = fabItem.icon,
                topLeft = Offset(
                    center.x - (fabItem.icon.width / 2),
                    center.y - (fabItem.icon.height / 2)
                ),
                alpha = alpha
            )
        }
    }
}
