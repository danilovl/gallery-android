package danilovl.gallery.ui.components

import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import danilovl.gallery.data.Photo
import danilovl.gallery.util.DateUtils

enum class SubGroup { MONTH, DAY }

private const val MIN_CELL = 72f
private const val MAX_CELL = 220f
private const val DEFAULT_CELL = 110f

private sealed interface Section {
    val label: String
    data class Sub(override val label: String) : Section
    data class Item(override val label: String, val photo: Photo) : Section
}

@Composable
fun SectionedPhotoGrid(
    photos: List<Photo>,
    emptyText: String,
    subGroup: SubGroup = SubGroup.MONTH,
    modifier: Modifier = Modifier
) {
    if (photos.isEmpty()) {
        EmptyState(emptyText, modifier)
        return
    }

    val sortedPhotos = remember(photos) { photos.sortedByDescending { it.dateTaken } }
    val sections = remember(sortedPhotos, subGroup) { buildSections(sortedPhotos, subGroup) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var cellSize by rememberSaveable { mutableFloatStateOf(DEFAULT_CELL) }

    val gridState = rememberLazyGridState()
    val density = LocalDensity.current
    val headerHeightPx = remember(density) { with(density) { 48.dp.toPx() } }

    val stickyHeaderInfo by remember(sections, headerHeightPx) {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) return@derivedStateOf null

            val firstItem = visibleItems.first()
            val section = sections.getOrNull(firstItem.index) ?: return@derivedStateOf null

            val nextHeader = visibleItems.drop(1).firstOrNull {
                val s = sections.getOrNull(it.index)
                s is Section.Sub
            }

            val firstHeaderOffset = if (section is Section.Sub) {
                firstItem.offset.y.toFloat()
            } else {
                -1f
            }

            val baseOffset = firstHeaderOffset.coerceAtLeast(0f)

            val pushOffset = nextHeader?.let {
                if (it.offset.y < headerHeightPx) it.offset.y - headerHeightPx else 0f
            } ?: 0f

            val finalOffset = if (pushOffset < 0) pushOffset else baseOffset
            section.label to finalOffset
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.changes.size >= 2) {
                            val zoom = event.calculateZoom()
                            if (zoom != 1f) {
                                cellSize = (cellSize * zoom).coerceIn(MIN_CELL, MAX_CELL)
                                event.changes.forEach { it.consume() }
                            }
                        }
                    }
                }
            }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(cellSize.dp),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 4.dp, end = 24.dp, top = 0.dp, bottom = 8.dp)
        ) {
            sections.forEach { section ->
                when (section) {
                    is Section.Sub -> item(span = { GridItemSpan(maxLineSpan) }) {
                        SubHeader(section.label)
                    }

                    is Section.Item -> item {
                        PhotoCell(section.photo) {
                            selectedIndex = sortedPhotos.indexOf(section.photo)
                        }
                    }
                }
            }
        }

        stickyHeaderInfo?.let { (label, offset) ->
            StickyHeaderOverlay(label, offset)
        }
    }

    selectedIndex?.let { index ->
        PhotoViewer(
            photos = sortedPhotos,
            initialIndex = index,
            onDismiss = { selectedIndex = null }
        )
    }
}

@Composable
private fun SubHeader(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
private fun StickyHeaderOverlay(label: String, offsetY: Float) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .graphicsLayer { translationY = offsetY }
    ) {
        Box(contentAlignment = Alignment.CenterStart) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

private fun buildSections(sortedPhotos: List<Photo>, subGroup: SubGroup): List<Section> {
    val result = mutableListOf<Section>()
    var lastSubKey: Int? = null
    var currentLabel = ""
    for (photo in sortedPhotos) {
        val subKey = subGroupKey(photo, subGroup)
        if (subKey != lastSubKey) {
            currentLabel = "${DateUtils.year(photo.dateTaken)} ${subGroupLabel(photo, subGroup)}"
            result.add(Section.Sub(currentLabel))
            lastSubKey = subKey
        }
        result.add(Section.Item(currentLabel, photo))
    }
    return result
}

private fun subGroupKey(photo: Photo, subGroup: SubGroup): Int = when (subGroup) {
    SubGroup.MONTH -> DateUtils.month(photo.dateTaken)
    SubGroup.DAY -> DateUtils.month(photo.dateTaken) * 100 + DateUtils.dayOfMonth(photo.dateTaken)
}

private fun subGroupLabel(photo: Photo, subGroup: SubGroup): String = when (subGroup) {
    SubGroup.MONTH -> DateUtils.monthFull(DateUtils.month(photo.dateTaken))
    SubGroup.DAY -> "${DateUtils.dayOfMonth(photo.dateTaken)} ${DateUtils.monthFull(DateUtils.month(photo.dateTaken))}"
}
