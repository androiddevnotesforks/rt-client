package com.automotivecodelab.rtclient.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.automotivecodelab.coreui.ui.theme.DefaultPadding
import com.automotivecodelab.rtclient.R

@Composable
fun ThemeSelector(
    theme: AppTheme,
    onThemeChanged: (AppTheme) -> Unit
) {
    val contentColor = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.IconOpacity)
    Surface(
        elevation = 0.dp,
        shape = RoundedCornerShape(100),
        border = BorderStroke(
            width = 1.dp,
            color = contentColor
        ),
        color = Color.Transparent,
        modifier = Modifier
            .padding(DefaultPadding)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ThemeSelectorRowEntry(
                modifier = Modifier.weight(1f),
                contentColor = contentColor,
                isSelected = theme == AppTheme.AUTO,
                onSelect = { onThemeChanged(AppTheme.AUTO) },
                icon = painterResource(id = R.drawable.ic_union)
            )
            Divider(
                Modifier
                    .background(contentColor)
                    .fillMaxHeight()
                    .width(1.dp)
            )
            ThemeSelectorRowEntry(
                modifier = Modifier.weight(1f),
                contentColor = contentColor,
                isSelected = theme == AppTheme.LIGHT,
                onSelect = { onThemeChanged(AppTheme.LIGHT) },
                icon = painterResource(id = R.drawable.ic_day)
            )
            Divider(
                Modifier
                    .background(contentColor)
                    .fillMaxHeight()
                    .width(1.dp)
            )
            ThemeSelectorRowEntry(
                modifier = Modifier.weight(1f),
                contentColor = contentColor,
                isSelected = theme == AppTheme.DARK,
                onSelect = { onThemeChanged(AppTheme.DARK) },
                icon = painterResource(id = R.drawable.ic_night)
            )
        }
    }
}

@Composable
fun ThemeSelectorRowEntry(
    modifier: Modifier = Modifier,
    contentColor: Color,
    isSelected: Boolean,
    onSelect: () -> Unit,
    icon: Painter
) {
    Box(
        modifier =
        modifier
            .fillMaxHeight()
            .clickable { onSelect() }
            .background(
                if (isSelected)
                    MaterialTheme.colors.primary.copy(alpha = 0.2f)
                else Color.Transparent
            )
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier
                .padding(DefaultPadding)
                .align(Alignment.Center)
        )
    }
}
