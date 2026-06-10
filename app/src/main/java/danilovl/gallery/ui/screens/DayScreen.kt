package danilovl.gallery.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import danilovl.gallery.R
import danilovl.gallery.data.Photo
import danilovl.gallery.ui.components.SectionedPhotoGrid
import danilovl.gallery.util.DateUtils

@Composable
fun DayScreen(photos: List<Photo>) {
    ChipFilterScreen(
        items = DateUtils.weekdayOrder,
        initialSelection = DateUtils.currentDayOfWeek(),
        label = { DateUtils.weekdayShort(it) }
    ) { dow ->
        SectionedPhotoGrid(
            photos = photos.filter { DateUtils.dayOfWeek(it.dateTaken) == dow },
            emptyText = stringResource(R.string.empty_day, DateUtils.weekdayFull(dow))
        )
    }
}
