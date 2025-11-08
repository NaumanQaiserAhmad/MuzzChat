package com.muzz.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.muzz.core.ui.R

@Composable
fun Avatar(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    imagePainter: Painter? = null,
    initials: String? = null,
    background: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    contentDescription: String = "Avatar"
) {
    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = background,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        when {
            imagePainter != null -> {
                Image(
                    painter = imagePainter,
                    contentDescription = contentDescription,
                    modifier = Modifier.padding(2.dp).fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            !initials.isNullOrBlank() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = initials.take(2).uppercase(),
                        color = contentColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            else -> {
                Image(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = contentDescription,
                    modifier = Modifier.padding(2.dp).fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
