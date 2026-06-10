package danilovl.gallery.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import danilovl.gallery.data.Photo
import danilovl.gallery.util.DateUtils
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val MAX_SCALE = 5f
private const val DOUBLE_TAP_SCALE = 2.5f
private const val DISMISS_FRACTION = 0.18f
private const val MIN_DISMISS_SCALE = 0.6f

@Composable
fun PhotoViewer(photos: List<Photo>, initialIndex: Int, onDismiss: () -> Unit) {
    val pagerState = rememberPagerState(initialPage = initialIndex) { photos.size }
    var isAnyPageZoomed by remember { mutableStateOf(false) }
    var dismissProgress by remember { mutableFloatStateOf(0f) }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val appear by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "appear",
        finishedListener = { if (it == 0f && !visible) onDismiss() }
    )

    Dialog(
        onDismissRequest = { visible = false },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val bgAlpha = (1f - (dismissProgress * 5f)).coerceIn(0f, 1f) * appear
        val bgColor = lerp(Color.Black, Color.White, (dismissProgress * 4f).coerceIn(0f, 1f))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor.copy(alpha = bgAlpha)),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 16.dp,
                userScrollEnabled = dismissProgress == 0f && !isAnyPageZoomed
            ) { page ->
                val isCurrent = page == pagerState.currentPage
                ZoomablePhotoPage(
                    photo = photos[page],
                    appear = appear,
                    isCurrentPage = isCurrent,
                    onDismiss = { visible = false },
                    onDismissProgress = { if (isCurrent) dismissProgress = it },
                    onZoomedChanged = { if (isCurrent) isAnyPageZoomed = it }
                )
            }

            PhotoInfoOverlay(
                modifier = Modifier.align(Alignment.TopCenter),
                photo = photos[pagerState.currentPage],
                alpha = appear * (1f - dismissProgress * 5f).coerceIn(0f, 1f),
                visible = !isAnyPageZoomed
            )
        }
    }
}

@Composable
private fun PhotoInfoOverlay(
    modifier: Modifier = Modifier,
    photo: Photo,
    alpha: Float,
    visible: Boolean
) {
    if (alpha <= 0f || !visible) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .graphicsLayer { this.alpha = alpha }
            .padding(top = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = DateUtils.formatDateTime(photo.dateTaken),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun ZoomablePhotoPage(
    photo: Photo,
    appear: Float,
    isCurrentPage: Boolean,
    onDismiss: () -> Unit,
    onDismissProgress: (Float) -> Unit,
    onZoomedChanged: (Boolean) -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }

    var dragX by remember { mutableFloatStateOf(0f) }
    var dragY by remember { mutableFloatStateOf(0f) }
    var settling by remember { mutableStateOf(false) }

    var imageSize by remember { mutableStateOf(Size.Zero) }
    var containerSize by remember { mutableStateOf(Size.Zero) }

    val zoomSpec = { if (isAnimating) spring<Float>(stiffness = Spring.StiffnessMediumLow) else snap<Float>() }
    val animatedScale by animateFloatAsState(scale, zoomSpec(), label = "scale")
    val animatedOffsetX by animateFloatAsState(offsetX, zoomSpec(), label = "offsetX")
    val animatedOffsetY by animateFloatAsState(offsetY, zoomSpec(), label = "offsetY")

    val dragSpec = { if (settling) spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow) else snap<Float>() }
    val animDragX by animateFloatAsState(dragX, dragSpec(), label = "dragX")
    val animDragY by animateFloatAsState(dragY, dragSpec(), label = "dragY")

    LaunchedEffect(isCurrentPage) {
        if (!isCurrentPage) {
            isAnimating = false
            scale = 1f
            offsetX = 0f
            offsetY = 0f
            settling = false
            dragX = 0f
            dragY = 0f
        }
    }

    LaunchedEffect(scale, isCurrentPage) {
        if (isCurrentPage) onZoomedChanged(scale > 1f)
    }

    val dismissReference = if (containerSize.height > 0f) containerSize.height else 1600f
    val dismissProgress = (abs(dragY) / dismissReference / 2f).coerceIn(0f, 1f)
    val dismissScale = (1f - dismissProgress * (1f - MIN_DISMISS_SCALE)).coerceIn(MIN_DISMISS_SCALE, 1f)

    LaunchedEffect(dismissProgress, isCurrentPage) {
        if (isCurrentPage) onDismissProgress(dismissProgress)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { containerSize = it.size.toSize() }
            .pointerInput(photo.id) {
                detectTapGestures(
                    onDoubleTap = {
                        isAnimating = true
                        if (scale > 1f) {
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                        } else {
                            scale = DOUBLE_TAP_SCALE
                        }
                    }
                )
            }
            .pointerInput(photo.id) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    isAnimating = false
                    settling = false
                    var isDraggingDown = false
                    do {
                        val event = awaitPointerEvent()
                        val pressed = event.changes.count { it.pressed }
                        val zoom = event.calculateZoom()
                        val pan = event.calculatePan()

                        if (pressed >= 2) {
                            val nextScale = (scale * zoom).coerceIn(1f, MAX_SCALE)
                            val s = fitScale(imageSize, containerSize)
                            scale = nextScale
                            offsetX = (offsetX + pan.x).coerceIn(-maxOffset(imageSize.width, s, nextScale, containerSize.width), maxOffset(imageSize.width, s, nextScale, containerSize.width))
                            offsetY = (offsetY + pan.y).coerceIn(-maxOffset(imageSize.height, s, nextScale, containerSize.height), maxOffset(imageSize.height, s, nextScale, containerSize.height))
                            event.changes.forEach { it.consume() }
                        } else if (pressed == 1) {
                            if (scale > 1f) {
                                val s = fitScale(imageSize, containerSize)
                                offsetX = (offsetX + pan.x).coerceIn(-maxOffset(imageSize.width, s, scale, containerSize.width), maxOffset(imageSize.width, s, scale, containerSize.width))
                                offsetY = (offsetY + pan.y).coerceIn(-maxOffset(imageSize.height, s, scale, containerSize.height), maxOffset(imageSize.height, s, scale, containerSize.height))
                                event.changes.forEach { it.consume() }
                            } else if (isCurrentPage && (isDraggingDown || abs(pan.y) > abs(pan.x))) {
                                isDraggingDown = true
                                dragX += pan.x
                                dragY += pan.y
                                event.changes.forEach { it.consume() }
                            }
                        }
                    } while (event.changes.any { it.pressed })

                    if (scale <= 1f && isCurrentPage && isDraggingDown) {
                        val ref = if (containerSize.height > 0f) containerSize.height else 1600f
                        if (abs(dragY) > ref * DISMISS_FRACTION) {
                            onDismiss()
                        } else {
                            settling = true
                            dragX = 0f
                            dragY = 0f
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = photo.uri,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            onState = { state ->
                if (state is AsyncImagePainter.State.Success) {
                    imageSize = state.painter.intrinsicSize
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = appear
                    scaleX = animatedScale * dismissScale
                    scaleY = animatedScale * dismissScale
                    translationX = animatedOffsetX + animDragX
                    translationY = animatedOffsetY + animDragY
                }
        )
    }
}

private fun fitScale(image: Size, container: Size): Float =
    if (image.width > 0f) min(container.width / image.width, container.height / image.height) else 1f

private fun maxOffset(imageDimension: Float, fit: Float, scale: Float, containerDimension: Float): Float =
    max(0f, (imageDimension * fit * scale - containerDimension) / 2f)
