package com.automotivecodelab.rtclient.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.automotivecodelab.coreui.ui.theme.DefaultPadding

@Composable
fun DrawerItem(
    painter: Painter,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val surfaceColor = if (isSelected) MaterialTheme.colors.primary.copy(alpha = 0.2f)
    else Color.Transparent
    val contentColor = MaterialTheme.colors.onSurface
    Surface(
        shape = RoundedCornerShape(100),
        color = surfaceColor,
        modifier = Modifier
            .padding(DefaultPadding)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painter,
                contentDescription = text,
                tint = contentColor,
                modifier = Modifier
                    .padding(DefaultPadding)
                    .weight(1f)
            )
            Text(
                text = (text),
                color = contentColor,
                modifier = Modifier
                    .padding(DefaultPadding)
                    .weight(4f)
            )
        }
    }
}
