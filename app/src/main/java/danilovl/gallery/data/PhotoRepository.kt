package danilovl.gallery.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore

class PhotoRepository(private val context: Context) {

    fun loadPhotos(): List<Photo> {
        val photos = mutableListOf<Photo>()
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATE_MODIFIED
        )
        val sortOrder = "COALESCE(${MediaStore.Images.Media.DATE_TAKEN}, ${MediaStore.Images.Media.DATE_MODIFIED} * 1000, ${MediaStore.Images.Media.DATE_ADDED} * 1000) DESC"

        context.contentResolver.query(collection, projection, null, null, sortOrder)
            ?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val takenCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val addedCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val modifiedCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val taken = cursor.getLong(takenCol)
                    val added = cursor.getLong(addedCol)
                    val modified = cursor.getLong(modifiedCol)

                    val date = when {
                        taken > 0L -> taken
                        modified > 0L -> modified * 1000L
                        else -> added * 1000L
                    }
                    val uri = ContentUris.withAppendedId(collection, id)
                    photos.add(Photo(id = id, uri = uri, dateTaken = date))
                }
            }
        return photos
    }
}
