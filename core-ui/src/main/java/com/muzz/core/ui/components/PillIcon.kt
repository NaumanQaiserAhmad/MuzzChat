package com.muzz.core.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Simple pill icon using Surface (avoids Box/background inline wrappers).
 */
@Composable
fun PillIcon(
    image: ImageVector,
    contentDescription: String?,
    tint: Color,
    containerColor: Color,
    onClick: () -> Unit,
    size: Dp = 36.dp,
    cornerRadius: Dp = 12.dp
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(cornerRadius),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(size)) {
            Icon(imageVector = image, contentDescription = contentDescription, tint = tint)
        }
    }
}
