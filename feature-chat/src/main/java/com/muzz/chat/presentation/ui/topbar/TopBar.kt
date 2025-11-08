package com.muzz.chat.presentation.ui.topbar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muzz.core.ui.components.Avatar
import com.muzz.core.ui.topbar.MuzzTopBar


@Composable
fun ChatTopBar(
    partnerName: String,
    partnerInitials: String? = null,
    onBack: () -> Unit,
    onMenu: () -> Unit
) {
    MuzzTopBar(
        height = 76.dp,
        navigation = { BackCaret(onBack) },
        title = {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pink-ish avatar background similar to the mock
                Avatar(
                    size = 36.dp,
                    imagePainter = null,
                    initials = partnerInitials,
                    background = Color(0xFFF0CBD6),     // light pink
                    contentColor = Color.White
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = partnerName,
                    color = Color(0xFF20262E),
                    // slightly larger than headlineSmall to match screenshot
                    fontSize = 26.sp,
                    lineHeight = 30.sp
                )
            }
        },
        actions = { ThreeDots(onMenu) }
    )
}

// BackCaret.kt
@Composable
fun BackCaret(
    onClick: () -> Unit,
    containerSize: Dp = 44.dp,      // touch target
    caretWidth: Dp = 35.dp,         // visual width of "<"
    caretHeight: Dp = 30.dp,        // ↑ make this bigger to elongate
    strokeWidth: Dp = 6.dp          // a bit thicker to match the mock
) {
    Box(
        modifier = Modifier
            .size(containerSize)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            Modifier.size(
                width = caretWidth,
                height = caretHeight      // taller than before (was 22.dp)
            )
        ) {
            val w = size.width
            val h = size.height
            val c = Color(0xFFE64B71)
            val sw = strokeWidth.toPx()

            // longer, more vertical caret: y from ~12% to ~88%
            drawLine(
                color = c,
                start = Offset(w * 0.72f, h * 0.12f),
                end   = Offset(w * 0.38f, h * 0.50f),
                strokeWidth = sw,
                cap = StrokeCap.Round
            )
            drawLine(
                color = c,
                start = Offset(w * 0.38f, h * 0.50f),
                end   = Offset(w * 0.72f, h * 0.88f),
                strokeWidth = sw,
                cap = StrokeCap.Round
            )
        }
    }
}


/** Three soft grey dots with generous spacing, as in the mock. */
@Composable
private fun ThreeDots(onClick: () -> Unit) {
    val dot = @Composable {
        Box(
            Modifier
                .size(10.5.dp)
                .clip(CircleShape)
                .background(Color(0xFFC9CED6))
        )
    }
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            dot(); Spacer(Modifier.width(5.dp))
            dot(); Spacer(Modifier.width(5.dp))
            dot()
        }
    }
}
