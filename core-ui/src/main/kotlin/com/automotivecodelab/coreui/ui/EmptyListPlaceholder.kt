package com.automotivecodelab.coreui.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.automotivecodelab.coreui.ui.theme.DefaultPadding

@Composable
fun EmptyListPlaceholder(hint: String, painter: Painter) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(DefaultPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colors.onSurface.copy(
                    alpha = 0.4f
                ),
                painter = painter,
                contentDescription = null
            )
            Text(
                text = hint,
                fontWeight = FontWeight.Light
            )
        }
    }
}
