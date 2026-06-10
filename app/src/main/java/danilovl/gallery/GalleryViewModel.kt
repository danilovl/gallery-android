package danilovl.gallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import danilovl.gallery.data.Photo
import danilovl.gallery.data.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = PhotoRepository(app)

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var loaded = false

    fun loadIfNeeded() {
        if (loaded) return
        loaded = true
        load()
    }

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = withContext(Dispatchers.IO) { repo.loadPhotos() }
            _photos.value = result.sortedByDescending { it.dateTaken }
            _isLoading.value = false
        }
    }
}
