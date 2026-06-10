package danilovl.gallery.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import danilovl.gallery.data.Photo

@Composable
fun PhotoCell(photo: Photo, onClick: () -> Unit) {
    AsyncImage(
        model = photo.uri,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .padding(2.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
    )
}

@Composable
fun EmptyState(text: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(32.dp)
        )
    }
}
