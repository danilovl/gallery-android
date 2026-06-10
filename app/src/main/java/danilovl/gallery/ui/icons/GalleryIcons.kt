package danilovl.gallery.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object GalleryIcons {

    val Day: ImageVector by lazy {
        buildIcon { rect(5f, 5f, 19f, 19f) }
    }

    val Month: ImageVector by lazy {
        buildIcon {
            rect(4f, 4f, 10.5f, 10.5f)
            rect(13.5f, 4f, 20f, 10.5f)
            rect(4f, 13.5f, 10.5f, 20f)
            rect(13.5f, 13.5f, 20f, 20f)
        }
    }

    val Timeline: ImageVector by lazy {
        buildIcon {
            rect(4f, 5f, 20f, 8f)
            rect(4f, 10.5f, 20f, 13.5f)
            rect(4f, 16f, 20f, 19f)
        }
    }

    val ArrowDropDown: ImageVector by lazy {
        buildIcon {
            moveTo(7f, 10f)
            lineTo(12f, 15f)
            lineTo(17f, 10f)
            close()
        }
    }

    private fun buildIcon(block: PathBuilder.() -> Unit): ImageVector =
        ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) { block() }
        }.build()

    private fun PathBuilder.rect(left: Float, top: Float, right: Float, bottom: Float) {
        moveTo(left, top)
        lineTo(right, top)
        lineTo(right, bottom)
        lineTo(left, bottom)
        close()
    }
}
