package com.automotivecodelab.coreui.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun RTClientTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (darkTheme)
            darkColors(
                primary = Color.White,
                secondary = Color(0xFF6FCC55),
                secondaryVariant = Color(0xFFFF2222)
            )
        else lightColors(
            primary = Blue,
            secondary = Green,
            secondaryVariant = Red,
            surface = Gray,
            background = LightGray
        ),
        typography = Typography,
        content = content,
        shapes = Shapes(
            medium = RoundedCornerShape(DefaultCornerRadius)
        )
    )
}
