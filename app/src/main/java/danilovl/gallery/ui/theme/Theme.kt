package danilovl.gallery.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF3D5AFE),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF6F6F8)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8C9EFF),
    background = Color(0xFF101012),
    surface = Color(0xFF1B1B1F)
)

@Composable
fun GalleryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
