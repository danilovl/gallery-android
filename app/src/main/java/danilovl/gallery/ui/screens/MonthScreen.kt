package danilovl.gallery.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import danilovl.gallery.R
import danilovl.gallery.data.Photo
import danilovl.gallery.ui.components.SectionedPhotoGrid
import danilovl.gallery.ui.components.SubGroup
import danilovl.gallery.util.DateUtils

@Composable
fun MonthScreen(photos: List<Photo>) {
    ChipFilterScreen(
        items = (0..11).toList(),
        initialSelection = DateUtils.currentMonth(),
        label = { DateUtils.monthShort(it) }
    ) { month ->
        SectionedPhotoGrid(
            photos = photos.filter { DateUtils.month(it.dateTaken) == month },
            emptyText = stringResource(R.string.empty_month, DateUtils.monthFull(month)),
            subGroup = SubGroup.DAY
        )
    }
}
