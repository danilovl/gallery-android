package danilovl.gallery.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object GalleryIcons {

    val Language: ImageVector by lazy {
        buildIcon {
            moveTo(12f, 2f)
            curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
            reflectiveCurveTo(6.48f, 22f, 12f, 22f)
            reflectiveCurveTo(22f, 17.52f, 22f, 12f)
            reflectiveCurveTo(17.52f, 2f, 12f, 2f)
            close()
            moveTo(11f, 19.93f)
            curveTo(7.05f, 19.44f, 4f, 16.08f, 4f, 12f)
            curveTo(4f, 11.38f, 4.08f, 10.79f, 4.21f, 10.21f)
            lineTo(9f, 15f)
            verticalLineTo(16f)
            curveTo(9f, 17.1f, 9.9f, 18f, 11f, 18f)
            verticalLineTo(19.93f)
            close()
            moveTo(17.9f, 17.39f)
            curveTo(17.64f, 16.58f, 16.9f, 16f, 16f, 16f)
            horizontalLineTo(15f)
            verticalLineTo(13f)
            curveTo(15f, 12.45f, 14.55f, 12f, 14f, 12f)
            horizontalLineTo(8f)
            verticalLineTo(10f)
            horizontalLineTo(10f)
            curveTo(10.55f, 10f, 11f, 9.55f, 11f, 9f)
            verticalLineTo(7f)
            horizontalLineTo(13f)
            curveTo(14.1f, 7f, 15f, 6.1f, 15f, 5f)
            verticalLineTo(4.59f)
            curveTo(17.93f, 5.77f, 20f, 8.64f, 20f, 12f)
            curveTo(20f, 14.08f, 19.2f, 15.97f, 17.9f, 17.39f)
            close()
        }
    }

    val Day: ImageVector by lazy {
        buildIcon { rect(5f, 5f, 19f, 19f) }
    }

    val Calendar: ImageVector by lazy {
        buildIcon {
            rect(4f, 6f, 20f, 20f)
            moveTo(4f, 10f)
            lineTo(20f, 10f)
            moveTo(8f, 4f)
            lineTo(8f, 8f)
            moveTo(16f, 4f)
            lineTo(16f, 8f)
        }
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
