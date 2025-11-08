package com.muzz.chat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

// ✅ add these two imports for Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send

@Composable
fun ConversationDivider(
    modifier: Modifier = Modifier,
    darkColor: Color = Color.Black,
    firstAlpha: Float = 0.08f,
    secondAlpha: Float = 0.04f
) {
    Column(modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(
                    Brush.verticalGradient(
                        0f to darkColor.copy(alpha = firstAlpha),
                        1f to Color.Transparent
                    )
                )
        )
        Spacer(Modifier.height(2.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    Brush.verticalGradient(
                        0f to darkColor.copy(alpha = secondAlpha),
                        1f to Color.Transparent
                    )
                )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputBar(
    placeholder: String,
    onSend: (String) -> Unit,
    sendButtonColor: Color,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf(TextFieldValue("")) }

    // Track focus to swap border colors (optional, but harmless)
    val interaction = remember { MutableInteractionSource() }
    val isFocused by interaction.collectIsFocusedAsState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp, max = 100.dp),
            placeholder = { Text(placeholder) },
            singleLine = false,
            minLines = 1,
            maxLines = 6,
            shape = RoundedCornerShape(28.dp),
            interactionSource = interaction,
            // ✅ Use TextFieldDefaults.colors in Material3
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = sendButtonColor,      // border when focused
                unfocusedIndicatorColor = Color(0xFFD0D5DD),  // border when not focused
                focusedTextColor = Color(0xFF111827),
                unfocusedTextColor = Color(0xFF111827),
                cursorColor = sendButtonColor
            )
        )

        Spacer(Modifier.width(10.dp))

        val gradient = Brush.horizontalGradient(
            listOf(Color(0xFFFF9AB5), sendButtonColor)
        )
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(gradient, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = {
                val t = text.text.trim()
                if (t.isNotEmpty()) {
                    onSend(t)
                    text = TextFieldValue("")
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Send, // ✅ use Filled (or Outlined/Rounded)
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}
