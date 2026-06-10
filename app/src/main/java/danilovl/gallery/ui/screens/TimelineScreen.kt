package danilovl.gallery.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import danilovl.gallery.R
import danilovl.gallery.data.Photo
import danilovl.gallery.ui.components.SectionedPhotoGrid

@Composable
fun TimelineScreen(photos: List<Photo>) {
    SectionedPhotoGrid(
        photos = photos,
        emptyText = stringResource(R.string.empty_timeline)
    )
}
