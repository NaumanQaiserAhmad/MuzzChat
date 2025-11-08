package com.muzz.chat.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.muzz.core.domain.model.DeliveryStatus
import com.muzz.core.ui.design.Palette
import com.muzz.chat.R

@Composable
fun DateChip(text: String) {
    // Accessibility text (computed in composable scope)
    val cd = stringResource(R.string.date_chip_cd, text)

    // Split once: "EEEE HH mm" -> ["<full day>", "<HH", "mm>"].
    // We bold just the first token (the full day).
    val parts = text.split(' ', limit = 2)
    val header: AnnotatedString = buildAnnotatedString {
        if (parts.isNotEmpty()) {
            // full day in bold
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            append(parts[0])
            pop()
            if (parts.size > 1) {
                append(' ')
                append(parts[1]) // "HH mm"
            }
        } else {
            append(text) // fallback if pattern changes unexpectedly
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            color = Palette.DateChipBg,
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = header,
                modifier = Modifier
                    .padding(vertical = 6.dp, horizontal = 12.dp)
                    .semantics { contentDescription = cd },
                style = MaterialTheme.typography.labelLarge,
                color = Palette.BubbleOtherFg
            )
        }
    }
}


@Composable
fun ConversationDivider() {
    Divider(
        color = Palette.DividerWeak,
        thickness = 1.dp,
        modifier = Modifier.padding(top = 6.dp)
    )
}

@Composable
fun MessageRow(
    text: String,
    isMine: Boolean,
    status: DeliveryStatus? = null,
    compactSpacingBelow: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (compactSpacingBelow) 4.dp else 10.dp),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
    ) {
        Row(
            horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            MessageBubble(text = text, isMine = isMine, status = status)
        }
    }
}

@Composable
private fun MessageBubble(
    text: String,
    isMine: Boolean,
    status: DeliveryStatus?
) {
    val shape = if (isMine) {
        RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomStart = 22.dp, bottomEnd = 3.dp)
    } else {
        RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomStart = 3.dp, bottomEnd = 22.dp)
    }

    val bg = if (isMine) MaterialTheme.colorScheme.primary else Palette.BubbleOtherBg
    val fg = if (isMine) MaterialTheme.colorScheme.onPrimary else Palette.BubbleOtherFg

    Surface(color = bg, shape = shape) {
        Box(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Column(horizontalAlignment = if (isMine) Alignment.End else Alignment.Start) {
                Text(text = text, color = fg, style = MaterialTheme.typography.bodyLarge)
                if (isMine && status != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    val glyph = statusGlyph(status)
                    val cd    = statusContentDescription(status)
                    Text(
                        text = glyph,
                        color = fg.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.semantics { contentDescription = cd }
                    )
                }
            }
        }
    }
}

@Composable
private fun statusGlyph(status: DeliveryStatus): String = when (status) {
    DeliveryStatus.SENDING   -> stringResource(R.string.status_glyph_sending)
    DeliveryStatus.SENT      -> stringResource(R.string.status_glyph_sent)
    DeliveryStatus.DELIVERED -> stringResource(R.string.status_glyph_delivered)
    DeliveryStatus.READ      -> stringResource(R.string.status_glyph_read)
    DeliveryStatus.FAILED    -> stringResource(R.string.status_glyph_failed)
}

@Composable
private fun statusContentDescription(status: DeliveryStatus): String = when (status) {
    DeliveryStatus.SENDING   -> stringResource(R.string.status_text_sending)
    DeliveryStatus.SENT      -> stringResource(R.string.status_text_sent)
    DeliveryStatus.DELIVERED -> stringResource(R.string.status_text_delivered)
    DeliveryStatus.READ      -> stringResource(R.string.status_text_read)
    DeliveryStatus.FAILED    -> stringResource(R.string.status_text_failed)
}
