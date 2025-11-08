package com.muzz.core.ui.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Reusable top bar shell with a subtle double shade at the bottom.
 * All params explicit (avoid *$default wrappers*).
 */
@Composable
fun MuzzTopBar(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    shadowElevation: Dp = 1.dp,
    height: Dp = 70.dp,                                 // taller — matches your screen
    insets: WindowInsets = WindowInsets.statusBars,
    contentPadding: PaddingValues = PaddingValues(
        start = 20.dp, end = 16.dp
    ),
    navigation: (@Composable () -> Unit)? = null,
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        modifier = Modifier,
        shape = RectangleShape,
        color = containerColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        shadowElevation = shadowElevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(1f)
                .windowInsetsPadding(insets)
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth(1f)
                    .height(height)
                    .padding(paddingValues = contentPadding),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (navigation != null) {
                    navigation()
                    Spacer(Modifier.width(12.dp))
                }
                title()
                Spacer(Modifier.weight(1f, fill = true))
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions
                )
            }

            // double shade: soft gradient + hairline
// Softer shade (~4% -> 0x0A) fading to transparent
            Box(
                Modifier
                    .fillMaxWidth(1f)
                    .height(8.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0x0A000000), Color(0x00000000)) // was 0x14000000
                        )
                    )
            )

        }
    }
}
