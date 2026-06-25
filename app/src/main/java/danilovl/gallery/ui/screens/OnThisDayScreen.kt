package danilovl.gallery.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import danilovl.gallery.R
import danilovl.gallery.data.Photo
import danilovl.gallery.ui.components.SectionedPhotoGrid
import danilovl.gallery.ui.components.SubGroup
import danilovl.gallery.util.DateUtils
import java.util.Calendar

@Composable
fun OnThisDayScreen(photos: List<Photo>) {
    val today = Calendar.getInstance()
    val currentDay = today.get(Calendar.DAY_OF_MONTH)
    val currentMonth = today.get(Calendar.MONTH)

    SectionedPhotoGrid(
        photos = photos.filter {
            DateUtils.dayOfMonth(it.dateTaken) == currentDay &&
                    DateUtils.month(it.dateTaken) == currentMonth
        },
        emptyText = stringResource(
            R.string.empty_on_this_day,
            DateUtils.formatDayMonth(currentDay, currentMonth)
        ),
        subGroup = SubGroup.DAY
    )
}
