package danilovl.gallery.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> ChipFilterScreen(
    items: List<T>,
    initialSelection: T,
    label: (T) -> String,
    content: @Composable (T) -> Unit
) {
    var selectedIndex by rememberSaveable(items, initialSelection) {
        mutableIntStateOf(items.indexOf(initialSelection).coerceAtLeast(0))
    }
    val listState = rememberLazyListState()

    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem(selectedIndex)
    }

    Column(Modifier.fillMaxSize()) {
        LazyRow(
            state = listState,
            modifier = Modifier
                .padding(start = 8.dp, end = 24.dp, top = 4.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(items) { index, item ->
                FilterChip(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    label = { Text(label(item)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        selectedLabelColor = MaterialTheme.colorScheme.onSurface,
                    )
                )
            }
        }

        Crossfade(
            targetState = selectedIndex,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            modifier = Modifier.weight(1f),
            label = "filterContent"
        ) { index ->
            if (index in items.indices) {
                content(items[index])
            }
        }
    }
}
